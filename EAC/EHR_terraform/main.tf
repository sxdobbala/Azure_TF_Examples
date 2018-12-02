locals {
  vnet_name = "${var.name}-vnet"
  appservice="${var.name}-appservice"
  # appassword ="${module.app-gw-pwd.id}"
}

module "generate-random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "create-resource-group" {
  source    = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  # name      = "resource-group-eg"
  name     = "${var.name}-rg"
  namespace = "${module.generate-random-name.name}"
  location  = "centralus"
}

module "create_virtual_network" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network?ref=v2.0.0-beta1"
  name                = "${local.vnet_name}"
  namespace           = "${module.generate-random-name.name}"
  # address_space       = ["10.0.0.0/16"]
  address_space       = "${var.vnet_cidr}"
  resource_group_name = "${module.create-resource-group.name}"
  location            = "centralus"
}

module "create_subnet" {
  source                          = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network.git//modules/subnet?ref=v2.0.0-beta1"
  virtual_network_name            = "${module.create_virtual_network.name}"
  resource_group_name             = "${module.create-resource-group.name}"
  namespace                       = "${module.generate-random-name.name}"
  # name                            = "example_subnet"
  name                            = "${var.name}-subnet"
  network_security_group_name     = "${var.name}-nsg"
  # address_prefix                  = "10.0.1.0/24"
  address_prefix                  = "${var.list_of_cidr_block_for_subnets}"
  service_endpoints               = ["Microsoft.Storage", "Microsoft.Sql"]
  network_security_group_location = "centralus"
}

resource "azurerm_network_security_rule" "https_in_optum" {
  name                        = "https_in_optum"
  resource_group_name         = "${module.create-resource-group.name}"
  network_security_group_name = "${module.create_subnet.network_security_group_name}"
  description                 = "Allow Optum networks"
  protocol                    = "tcp"
  source_port_range           = "*"
  destination_port_range      = "443"
  # source_address_prefix     = "*"
  source_address_prefixes     = ["198.203.177.177/32", "198.208.175.175/32", "198.203.181.181/32", "168.183.84.12/32", "149.111.26.128/32", "149.111.28.128/32", "149.111.30.128/32", "220.227.15.70/32", "203.39.148.18/32", "161.249.192.14/32", "161.249.72.14/32", "161.249.80.14/32", "161.249.96.14/32", "161.249.144.14/32", "161.249.176.14/32", "161.249.16.0/23", "12.163.96.0/24"]
  destination_address_prefix  = "*"
  access                      = "Allow"
  priority                    = "210"
  direction                   = "Inbound"
}

 resource "azurerm_network_security_rule" "allow-appgatewayhealthcheck" {
  name                        = "appgate"
  resource_group_name         = "${module.create-resource-group.name}"
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

 resource "azurerm_network_security_rule" "allowhttpsoutbound" {
  name                        = "https_out"
  resource_group_name         = "${module.create-resource-group.name}"
  network_security_group_name = "${module.create_subnet.network_security_group_name}"
  description                 = "Allow outbound https access"
  protocol                    = "tcp"
  source_port_range           = "*"
  destination_port_range      = "443"
  source_address_prefix       = "VirtualNetwork"
  destination_address_prefix  = "Internet"
  access                      = "Allow"
  priority                    = "500"
  direction                   = "Outbound"
}


resource "azurerm_public_ip" "primary_public_ip" {
  # name                         = "primary_public_ip"
  name                         = "${var.name}-ip"
  location                     = "centralus"
  resource_group_name          = "${module.create-resource-group.name}"
  public_ip_address_allocation = "Dynamic"
  idle_timeout_in_minutes      = "5"
}

 module "application_gateway" {
  source                                             = "terraform_module/application_gateway_https/"
  app_gateway_name                                   = "${var.name}-gateway"
  app_gateway_arm_location                           = "centralus"
  app_gateway_resource_group_name                    = "${module.create-resource-group.name}"
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
  frontend_ip_configuration_name                     = "${local.vnet_name}-feip"
  app_gateway_fortend_port_name                      = "${local.vnet_name}-feport"

  /*for unlimited ssl*/
  app_gateway_ssl_cert = [{
    name     = "optum_cert"
    # data     = "${base64encode(file("${path.module}/master.pfx"))}"
    data     = "${base64encode(file("${path.module}/caseadvisor.optum.pfx"))}"
    # password = "Test123"
    # password = "C@se_1234_cert"
    password = "${var.appgwcertpasswd}"
  }]

  app_gateway_authentication_cert = [{
    name = "optumcer"
    data = "${base64encode(file("${path.module}/caseadvisor.optum.com.cer"))}"
  }]

  app_gateway_backend_address_pool = [{
    name = "${local.vnet_name}-beap"
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
    name                  = "${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 443
    protocol              = "https"
    request_timeout       = 40
    probe_name            ="default-health-probe"
    authentication_certificate = [{
      name = "optumcer"
    }]
  }]

  app_gateway_http_listener_details = [{
    name                           = "${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${local.vnet_name}-feip"
    frontend_port_name             = "${local.vnet_name}-feport"
    protocol                       = "https"
    ssl_certificate_name           = "optum_cert"
  }]

  app_gateway_routing_request_rule_details = [{
    name                       = "${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  }]

  app_gateway_url_path_map_name                  = "${local.vnet_name}-path-map"
  app_gateway_default_backend_address_pool_name  = "${local.vnet_name}-beap"
  app_gateway_default_backend_http_settings_name = "${local.vnet_name}-be-htst1"

  app_gateway_path_rule_details = [{
    name                       = "vm-0"
    paths                      = ["/vm-0/"]
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  },
    {
      name                       = "vm-1"
      paths                      = ["/vm-1/"]
      backend_address_pool_name  = "${local.vnet_name}-beap"
      backend_http_settings_name = "${local.vnet_name}-be-htst1"
    },
  ]
}

output "app_gateway_id" {
  value = "${module.application_gateway.application_gateway_configuration_id}"
}
