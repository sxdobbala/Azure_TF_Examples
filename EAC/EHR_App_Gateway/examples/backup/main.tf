locals {
  vnet_name = "virtual_network"
}

module "create-resource-group" {
  source   = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  name     = "resource-group-eg"
  location = "centralus"
}

module "create_virtual_network" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network?ref=v2.0.0-beta1"
  name                = "${local.vnet_name}"
  address_space       = ["10.0.0.0/16"]
  resource_group_name = "${module.create-resource-group.name}"
  location            = "centralus"
}

module "create_subnet" {
  source                          = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network.git//modules/subnet?ref=v2.0.0-beta1"
  virtual_network_name            = "${module.create_virtual_network.name}"
  resource_group_name             = "${module.create-resource-group.name}"
  name                            = "example_subnet"
  network_security_group_name     = "example-nsg"
  address_prefix                  = "10.0.1.0/24"
  service_endpoints               = ["Microsoft.Storage", "Microsoft.Sql"]
  network_security_group_location = "centralus"
}

resource "azurerm_network_security_rule" "allow-https" {
  name                        = "https"
  resource_group_name         = "${module.create-resource-group.name}"
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

resource "azurerm_network_security_rule" "allow-http" {
  name                        = "http"
  resource_group_name         = "${module.create-resource-group.name}"
  network_security_group_name = "${module.create_subnet.network_security_group_name}"
  description                 = "rule to allow inbound http traffic"
  protocol                    = "tcp"
  source_port_range           = "*"
  destination_port_range      = "80"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  access                      = "Allow"
  priority                    = "215"
  direction                   = "Inbound"
}

resource "azurerm_public_ip" "primary_public_ip" {
  name                         = "primary_public_ip"
  location                     = "centralus"
  resource_group_name          = "${module.create-resource-group.name}"
  public_ip_address_allocation = "Dynamic"
  idle_timeout_in_minutes      = "5"
}

/*application gateway for https protocol*/

module "application_gateway" {
  source                                             = "../../"
  app_gateway_name                                   = "example-app-gateway"
  app_gateway_arm_location                           = "centralus"
  app_gateway_resource_group_name                    = "${module.create-resource-group.name}"
  app_gateway_subnet_id                              = "${module.create_subnet.id}"
  app_gateway_virtual_network_name                   = "${module.create_virtual_network.name}"
#  app_gateway_frontend_ip_address_type               = "both_private_publicip_with_waf_unlimited_ssl"
  app_gateway_frontend_ip_address_type               = "publicip_without_waf_unlimited_ssl"
#  app_gateway_frontend_private_ip_address_allocation = "static"
#  app_gateway_frontend_private_ip_address            = "10.0.1.16"
  app_gateway_frontend_public_ip_address_id          = "${azurerm_public_ip.primary_public_ip.id}"
#  app_gateway_sku                                    = "WAF_Medium"
  app_gateway_sku				     = "Standard_Small"
  app_gateway_capacity                               = "2"
#  app_gateway_waf_configuration_enabled              = "true"
  frontend_ip_configuration_name                     = "${local.vnet_name}-feip"

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

  /*
              for limited ssl
              Note: the reason for this seting is output of a module or data resource or computed vaule cannot be passed in a list(bug in terraform).
              Below is the issue with terraform: https://github.com/hashicorp/terraform/issues/16856
                    
              ssl_cert_name_1 = "ssl_cert"
              pfx_file_path_1     = "${base64encode(file("${path.module}/master.pfx"))}"
              ssl_cert_password_1 = "Test123"
              auth_cert_name_1    = "master"
              cer_file_path_1     = "${base64encode(file("${path.module}/master.cer"))}"
              ssl_cert_name_2     = "ssl_cert_2"
              pfx_file_path_2     = "${base64encode(file("${path.module}/master_2.pfx"))}"
              ssl_cert_password_2 = "Test123"
              auth_cert_name_2    = "master_2"
              cer_file_path_2     = "${base64encode(file("${path.module}/master_2.cer"))}"
              */

  app_gateway_backend_address_pool = [{
    name = "${local.vnet_name}-beap"
  }]
  app_gateway_fortend_port_details = [
    {
      name = "${local.vnet_name}-feport1"
      port = "443"
    },
    {
      name = "${local.vnet_name}-feport2"
      port = "80"
    },
    {
      name = "${local.vnet_name}-feport3"
      port = "70"
    },
  ]
  app_gateway_backend_http_settings = [{
    name                  = "${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 443
    protocol              = "https"
    request_timeout       = 40

    authentication_certificate = [{
      name = "master"
    }]
  },
    {
      name                  = "${local.vnet_name}-be-htst2"
      cookie_based_affinity = "Disabled"
      port                  = 80
      protocol              = "http"
      request_timeout       = 40
    },
  ]
  app_gateway_http_listener_details = [{
    name                           = "${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${local.vnet_name}-feip"
    frontend_port_name             = "${local.vnet_name}-feport1"
    protocol                       = "https"
    ssl_certificate_name           = "ssl_cert"
  },
    {
      name                           = "${local.vnet_name}-httplstn2"
      frontend_ip_configuration_name = "${local.vnet_name}-feip"
      frontend_port_name             = "${local.vnet_name}-feport2"
      protocol                       = "http"
    },
  ]
  app_gateway_routing_request_rule_details = [{
    name                       = "${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  },
    {
      name                       = "${local.vnet_name}-rqrt2"
      rule_type                  = "Basic"
      http_listener_name         = "${local.vnet_name}-httplstn2"
      backend_address_pool_name  = "${local.vnet_name}-beap"
      backend_http_settings_name = "${local.vnet_name}-be-htst2"
    },
  ]
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


/* For both public and private Application Gateway with URL based routing feature, WAF enabled and http protocol
module "application_gateway" {
  source                                             = "../../"
  app_gateway_name                                   = "example-app-gateway"
  app_gateway_arm_location                           = "centralus"
  app_gateway_resource_group_name                    = "${module.create-resource-group.name}"
  app_gateway_subnet_id                              = "${module.create_subnet.id}"
  app_gateway_virtual_network_name                   = "${module.create_virtual_network.name}"
  app_gateway_frontend_private_ip_address_allocation = "static"
  app_gateway_frontend_private_ip_address            = "10.0.1.16"
  app_gateway_frontend_public_ip_address_id          = "${module.primary_public_ip.public_ip_id}"
  app_gateway_sku                                    = "WAF_Medium"
  app_gateway_capacity                               = "2"
  app_gateway_waf_configuration_enabled              = "true"
  app_gateway_frontend_ip_address_type               = "both_private_public_ip_with_waf"
  frontend_ip_configuration_name                     = "${local.vnet_name}-feip"
  app_gateway_ssl_cert                               = []
  app_gateway_authentication_cert                    = []

  app_gateway_backend_address_pool = [{
    name = "${local.vnet_name}-beap"
  }]

  app_gateway_fortend_port_details = [{
    name = "${local.vnet_name}-feport1"
    port = "80"
  },
    {
      name = "${local.vnet_name}-feport2"
      port = "90"
    },
    {
      name = "${local.vnet_name}-feport3"
      port = "70"
    },
  ]

  app_gateway_backend_http_settings = [{
    name                  = "${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 80
    protocol              = "Http"
    request_timeout       = 40
  },
    {
      name                  = "${local.vnet_name}-be-htst2"
      cookie_based_affinity = "Disabled"
      port                  = 80
      protocol              = "Http"
      request_timeout       = 40
    },
  ]

  app_gateway_http_listener_details = [{
    name                           = "${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${local.vnet_name}-feip"
    frontend_port_name             = "${local.vnet_name}-feport1"
    protocol                       = "Http"
  },
    {
      name                           = "${local.vnet_name}-httplstn2"
      frontend_ip_configuration_name = "${local.vnet_name}-feip"
      frontend_port_name             = "${local.vnet_name}-feport2"
      protocol                       = "Http"
    },
  ]

  app_gateway_routing_request_rule_details = [{
    name                       = "${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  },
    {
      name                       = "${local.vnet_name}-rqrt2"
      rule_type                  = "Basic"
      http_listener_name         = "${local.vnet_name}-httplstn2"
      backend_address_pool_name  = "${local.vnet_name}-beap"
      backend_http_settings_name = "${local.vnet_name}-be-htst2"
    },
  ]

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
*/

/* For public Application Gateway 

module "application_gateway" {
  source                                    = "../../"
  app_gateway_name                          = "example-app-gateway"
  app_gateway_arm_location                  = "centralus"
  app_gateway_resource_group_name           = "${module.create-resource-group.name}"
  app_gateway_subnet_id                     = "${module.create_subnet.id}"
  app_gateway_virtual_network_name          = "${module.create_virtual_network.name}"
  app_gateway_frontend_public_ip_address_id = "${module.primary_public_ip.public_ip_id}"
  app_gateway_frontend_ip_address_type      = "publicip_with_waf"
  app_gateway_sku                           = "WAF_Medium"
  app_gateway_capacity                      = "2"
  app_gateway_backend_ip_address_list       = ["${azurerm_network_interface.nic.*.private_ip_address}"]
  app_gateway_backendpool_name              = "${local.vnet_name}-beap"
  frontend_ip_configuration_name            = "${local.vnet_name}-feip"

  app_gateway_fortend_port_details = [{
    name = "${local.vnet_name}-feport1"
    port = "80"
  },
    {
      name = "${local.vnet_name}-feport2"
      port = "90"
    },
    {
      name = "${local.vnet_name}-feport3"
      port = "70"
    },
  ]

  app_gateway_backend_http_settings = [{
    name                  = "${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 80
    protocol              = "Http"
    request_timeout       = 40
  },
    {
      name                  = "${local.vnet_name}-be-htst2"
      cookie_based_affinity = "Disabled"
      port                  = 80
      protocol              = "Http"
      request_timeout       = 40
    },
  ]

  app_gateway_http_listener_details = [{
    name                           = "${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${local.vnet_name}-feip"
    frontend_port_name             = "${local.vnet_name}-feport1"
    protocol                       = "Http"
  },
    {
      name                           = "${local.vnet_name}-httplstn2"
      frontend_ip_configuration_name = "${local.vnet_name}-feip"
      frontend_port_name             = "${local.vnet_name}-feport2"
      protocol                       = "Http"
    },
  ]

  app_gateway_routing_request_rule_details = [{
    name                       = "${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  },
    {
      name                       = "${local.vnet_name}-rqrt2"
      rule_type                  = "Basic"
      http_listener_name         = "${local.vnet_name}-httplstn2"
      backend_address_pool_name  = "${local.vnet_name}-beap"
      backend_http_settings_name = "${local.vnet_name}-be-htst2"
    },
  ]
}
*/

/*For private Application Gateway
module "application_gateway" {
  source                                             = "../../"
  app_gateway_name                                   = "example-app-gateway"
  app_gateway_arm_location                           = "centralus"
  app_gateway_resource_group_name                    = "${module.create-resource-group.name}"
  app_gateway_subnet_id                              = "${module.create_subnet.id}"
  app_gateway_virtual_network_name                   = "${module.create_virtual_network.name}"
  app_gateway_frontend_ip_address_type               = "privateip_with_waf"
  app_gateway_frontend_private_ip_address_allocation = "dynamic"
  app_gateway_sku                                    = "WAF_Medium"
  app_gateway_capacity                               = "2"
  app_gateway_backend_ip_address_list                = ["${azurerm_network_interface.nic.*.private_ip_address}"]

  app_gateway_backendpool_name   = "${local.vnet_name}-beap"
  frontend_ip_configuration_name = "${local.vnet_name}-feip"

  app_gateway_fortend_port_details = [{
    name = "${local.vnet_name}-feport1"
    port = "80"
  },
    {
      name = "${local.vnet_name}-feport2"
      port = "90"
    },
    {
      name = "${local.vnet_name}-feport3"
      port = "70"
    },
  ]

  app_gateway_backend_http_settings = [{
    name                  = "${local.vnet_name}-be-htst1"
    cookie_based_affinity = "Disabled"
    port                  = 80
    protocol              = "Http"
    request_timeout       = 40
  },
    {
      name                  = "${local.vnet_name}-be-htst2"
      cookie_based_affinity = "Disabled"
      port                  = 80
      protocol              = "Http"
      request_timeout       = 40
    },
  ]

  app_gateway_http_listener_details = [{
    name                           = "${local.vnet_name}-httplstn1"
    frontend_ip_configuration_name = "${local.vnet_name}-feip"
    frontend_port_name             = "${local.vnet_name}-feport1"
    protocol                       = "Http"
  },
    {
      name                           = "${local.vnet_name}-httplstn2"
      frontend_ip_configuration_name = "${local.vnet_name}-feip"
      frontend_port_name             = "${local.vnet_name}-feport2"
      protocol                       = "Http"
    },
  ]

  app_gateway_routing_request_rule_details = [{
    name                       = "${local.vnet_name}-rqrt1"
    rule_type                  = "Basic"
    http_listener_name         = "${local.vnet_name}-httplstn1"
    backend_address_pool_name  = "${local.vnet_name}-beap"
    backend_http_settings_name = "${local.vnet_name}-be-htst1"
  },
    {
      name                       = "${local.vnet_name}-rqrt2"
      rule_type                  = "Basic"
      http_listener_name         = "${local.vnet_name}-httplstn2"
      backend_address_pool_name  = "${local.vnet_name}-beap"
      backend_http_settings_name = "${local.vnet_name}-be-htst2"
    },
  ]
}
*/

