module "generate-random-name" { 
   source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.1.0" 
   //Note: Once a random name has been generated, it is not regenerated unless the state is "taint"ed. This could be useful for avoiding naming conflicts
   //       but also requires that additional step if we are trying to force a regeneration (eg. for migrations or transient environments)
}

locals { 
    namespace = "${terraform.workspace}" 
	  vnet_name = "${var.NAME}-vnet"
	appservice="${local.namespaced_project_name}"
    //TODO: Implement and use global tags
    global_tags = {environment="${terraform.workspace}", project="${var.PROJECT_NAME}" }

    //ARM templates don't support namespaces, so implement it manually for now
    namespaced_project_name = "${format("%s-%s", var.PROJECT_NAME, local.namespace)}"    
}

module "resource_group"{
    source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group.git"
    providers = {
        "azurerm"= "azurerm"
    }
    name = "${var.PROJECT_NAME}"
    location = "${var.LOCATION}"
    namespace  = "${local.namespace}"
    //Syntax is wrong- need to fix later
    //global_tags = ${local.global_tags}

	tags = {
    Description = "NonProd example"
  }

	global_tags = {
    environment = "NonProd example Global"
  }
}
data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "keyvault" {
  name                = "${module.resource_group.name}"
  location            = "${var.LOCATION}"
  resource_group_name = "${module.resource_group.name}"
  tenant_id			  = "db05faca-c82a-4b9d-b9c5-0f64b6755421"
  sku {
    name = "standard"
  }
  //AZU_P00001156_Contributor permissions
  access_policy {
    tenant_id = "db05faca-c82a-4b9d-b9c5-0f64b6755421"
    object_id = "5faed7c3-92a1-479a-980e-f33b844c085e"

    key_permissions =  ["get", "list", "update", "create", "import", "delete", "recover", "backup", "restore", "decrypt", "encrypt", "wrapkey", "unwrapkey", "verify", "sign", "purge"]

    secret_permissions = ["get", "list", "set", "delete", "recover", "backup", "restore", "purge"]
  }
	//spURAdvisorAppDev1 permissions
    access_policy {
    tenant_id = "db05faca-c82a-4b9d-b9c5-0f64b6755421"
    object_id = "762cea24-3288-465d-ba61-a87ce34e3644"

    key_permissions =  ["get", "list", "update", "create", "import", "delete", "recover", "backup", "restore", "decrypt", "encrypt", "wrapkey", "unwrapkey", "verify", "sign", "purge"]

    secret_permissions = ["get", "list", "set", "delete", "recover", "backup", "restore", "purge"]
  }

  enabled_for_disk_encryption = true
  enabled_for_deployment = true
  enabled_for_template_deployment = true

  tags {
    environment = "NonProd"
  }
}

resource "azurerm_key_vault_secret" "URAdvisorClientSecret" {
  name      = "${module.resource_group.name}-clientSecret"
  value     = "${var.OPTUMID_CLIENTSECRET}"
  vault_uri = "${azurerm_key_vault.keyvault.vault_uri}"

  tags {
    environment = "NonProd ClientSecret"
  }
}

resource "azurerm_key_vault_secret" "URAdvisorClientID" {
  name      = "${module.resource_group.name}-clientID"
  value     = "${var.OPTUMID_CLIENTID}"
  vault_uri = "${azurerm_key_vault.keyvault.vault_uri}"

  tags {
    environment = "NonProd ClientID"
  }
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.PROJECT_NAME}"
  namespace   = "${var.PROJECT_NAME}"
  name_format = "%s-%s"
}
module "create_virtual_network" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network?ref=v2.0.0-beta1"
  name                = "${var.PROJECT_NAME}${local.vnet_name}"
  address_space       = "${var.VNET_CIDR}"
  resource_group_name = "${module.resource_group.name}"
  location            = "${var.LOCATION}"
}

module "create_subnet" {
  source                          = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network.git//modules/subnet?ref=v2.0.0-beta1"
  virtual_network_name            = "${module.create_virtual_network.name}"
  resource_group_name             = "${module.resource_group.name}"
  name                            = "${var.PROJECT_NAME}-subnet"
  network_security_group_name     = "${var.PROJECT_NAME}-nsg"
  address_prefix                  = "${var.LIST_OF_CIDR_BLOCK_FOR_SUBNETS}"
  service_endpoints               = ["Microsoft.Storage", "Microsoft.Sql"]
  network_security_group_location = "${var.LOCATION}"
}

  resource "azurerm_network_security_rule" "allow-https" {
  name                        = "https"
  resource_group_name         = "${module.resource_group.name}"
  network_security_group_name = "${module.create_subnet.network_security_group_name}"
  description                 = "rule to allow inbound https traffic"
  protocol                    = "tcp"
  source_port_range           = "*"
  destination_port_range      = "443"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  access                      = "Allow"
  priority                    = "210"
  direction                   = "Inbound"
}

 resource "azurerm_network_security_rule" "allow-appgatewayhealthcheck" {
  name                        = "appgate"
  resource_group_name         = "${module.resource_group.name}"
  network_security_group_name = "${module.create_subnet.network_security_group_name}"
  description                 = "rule to allow inbound app gateway health check"
  protocol                    = "tcp"
  source_port_range           = "*"
  destination_port_range      = "65503-65534"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  access                      = "Allow"
  priority                    = "250"
  direction                   = "Inbound"
}
#HTTP HERE

resource "azurerm_public_ip" "primary_public_ip" {
  name                         = "${var.PROJECT_NAME}-ip"
  location                     = "${var.LOCATION}"
  resource_group_name          = "${module.resource_group.name}"
  public_ip_address_allocation = "Dynamic"
  idle_timeout_in_minutes      = "5"
}

/*application gateway for http protocol*/
module "application_gateway" {
  source                                             = "terraform_module/application_gateway_https/"
  app_gateway_name                                   = "${var.PROJECT_NAME}-gateway"
  app_gateway_arm_location                           = "${var.LOCATION}"
  app_gateway_resource_group_name                    = "${module.resource_group.name}"
  app_gateway_subnet_id                              = "${module.create_subnet.id}"
  app_gateway_virtual_network_name                   = "${module.create_virtual_network.name}"
  # app_gateway_frontend_ip_address_type               = "both_private_publicip_with_waf_unlimited_ssl"
  app_gateway_frontend_ip_address_type               ="publicip_with_waf_unlimited_ssl"
  # app_gateway_frontend_private_ip_address_allocation = "static"
  # app_gateway_frontend_private_ip_address            = "10.0.1.16"
  app_gateway_frontend_public_ip_address_id          = "${azurerm_public_ip.primary_public_ip.id}"
  app_gateway_sku                                    = "WAF_Medium"
  app_gateway_capacity                               = "2"
  app_gateway_waf_configuration_enabled              = "true"
  frontend_ip_configuration_name                     = "${var.PROJECT_NAME}${local.vnet_name}-feip"
  app_gateway_fortend_port_name                      = "${var.PROJECT_NAME}${local.vnet_name}-feport"

  /*for unlimited ssl*/
  app_gateway_ssl_cert = [{
    name     = "ssl_cert"
    data     = "${base64encode(file("${path.module}/master.pfx"))}"
    password = "Test123"
  }]

  app_gateway_authentication_cert = [{
    name = "master"
    data = "${base64encode(file("${path.module}/master.cer"))}"
  }]

  app_gateway_backend_address_pool = [{
    name = "${var.PROJECT_NAME}${local.vnet_name}-beap"
    "fqdn_list" = ["${local.appservice}.azurewebsites.net"]
  }]

 app_gateway_health_probe_list= [{
   name                = "default-health-probe"
    protocol            = "https"
    path                = "/"
    interval            = 30
    timeout             = 120
    unhealthy_threshold = 3
    host                = "${local.appservice}.azurewebsites.net"
  }]

  app_gateway_backend_http_settings = [{
    name                  = "${var.PROJECT_NAME}${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 443
    protocol              = "https"
    request_timeout       = 40
    probe_name            ="default-health-probe"
    authentication_certificate = [{
      name = "master"
    }]
  }]

  app_gateway_http_listener_details = [{
    name                           = "${var.PROJECT_NAME}${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${var.PROJECT_NAME}${local.vnet_name}-feip"
    frontend_port_name             = "${var.PROJECT_NAME}${local.vnet_name}-feport"
    protocol                       = "https"
    ssl_certificate_name           = "ssl_cert"
  }]

  app_gateway_routing_request_rule_details = [{
    name                       = "${var.PROJECT_NAME}${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${var.PROJECT_NAME}${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${var.PROJECT_NAME}${local.vnet_name}-beap"
    backend_http_settings_name = "${var.PROJECT_NAME}${local.vnet_name}-be-htst1"
  }]

  app_gateway_url_path_map_name                  = "${var.PROJECT_NAME}${local.vnet_name}-path-map"
  app_gateway_default_backend_address_pool_name  = "${var.PROJECT_NAME}${local.vnet_name}-beap"
  app_gateway_default_backend_http_settings_name = "${var.PROJECT_NAME}${local.vnet_name}-be-htst1"

  app_gateway_path_rule_details = [{
    name                       = "vm-0"
    paths                      = ["/vm-0/"]
    backend_address_pool_name  = "${var.PROJECT_NAME}${local.vnet_name}-beap"
    backend_http_settings_name = "${var.PROJECT_NAME}${local.vnet_name}-be-htst1"
  },
    {
      name                       = "vm-1"
      paths                      = ["/vm-1/"]
      backend_address_pool_name  = "${var.PROJECT_NAME}${local.vnet_name}-beap"
      backend_http_settings_name = "${var.PROJECT_NAME}${local.vnet_name}-be-htst1"
    },
  ]
}

