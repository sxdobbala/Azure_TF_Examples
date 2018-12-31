module "random_name" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
  total_length                                         = 17
  name_stub                                            = "odxipoc"
}

resource "random_string" "password" {
  length                                               = 16
  special                                              = true
  override_special                                     = "-_=+;:[]{}"
  min_special                                          = 1
  min_upper                                            = 1
  min_lower                                            = 1
  min_numeric                                          = 1
}

module "vnet01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//?ref=v2.0.0-beta1"
  name                                                 = "${module.random_name.name}"
  location                                             = "${var.location}"
  namespace                                            = "${var.namespace}"
  address_space                                        = ["10.0.0.0/16"]
  resource_group_name                                  = "${var.resource_group_name}"
  tags                                                 = "${var.global_tags}"
}

module "subnet01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/subnet?ref=v2.0.0-beta1"
  name                                                 = "${module.random_name.name}-web"
  namespace                                            = "${var.namespace}"
  resource_group_name                                  = "${var.resource_group_name}"
  virtual_network_name                                 = "${module.vnet01.name}"
  address_prefix                                       = "10.0.1.0/24"
  network_security_group_name                          = "${module.random_name.name}-web"
  network_security_group_location                      = "${var.location}"
}


module "subnet02" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/subnet?ref=v2.0.0-beta1"
  name                                                 = "${module.random_name.name}-app"
  namespace                                            = "${var.namespace}"
  resource_group_name                                  = "${var.resource_group_name}"
  virtual_network_name                                 = "${module.vnet01.name}"
  address_prefix                                       = "10.0.2.0/24"
  network_security_group_name                          = "${module.random_name.name}-app"
  network_security_group_location                      = "${var.location}"
}

module "subnet03" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/subnet?ref=v2.0.0-beta1"
  name                                                 = "${module.random_name.name}-db"
  namespace                                            = "${var.namespace}"
  resource_group_name                                  = "${var.resource_group_name}"
  virtual_network_name                                 = "${module.vnet01.name}"
  address_prefix                                       = "10.0.3.0/24"
  network_security_group_name                          = "${module.random_name.name}-db"
  network_security_group_location                      = "${var.location}"
}



################################################################################
# NSG RULES
################################################################################

resource "azurerm_network_security_rule" "vnet_inbound" {
  name                                                 = "vnet_inbound"
  description                                          = "Allow VNET Inbound"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1001"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "http_in" {
  name                                                 = "http_in"
  description                                          = "Allow http in for HTTP->HTTPS redirect (controlled by Apache config) and LetsEncrypt"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1002"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "80"
}

resource "azurerm_network_security_rule" "https_in" {
  name                                                 = "https_in_optum"
  description                                          = "Allow https in from Optum networks"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1003"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefixes                              = "${var.optum_addresses}"
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "443"
}

resource "azurerm_network_security_rule" "azure_autoconfig_in" {
  name                                                 = "azure_autoconfig_in"
  description                                          = "Allow inbound access from Azure's special config IP"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1004"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "168.63.129.16/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "vnet_outbound" {
  name                                                 = "vnet_outbound"
  description                                          = "Allow VNET Outbound"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "2000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "azure_autoconfig_out" {
  name                                                 = "azure_autoconfig_out"
  description                                          = "Allow outbound access to Azure's special config IP"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "2001"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "168.63.129.16/32"
  destination_port_range                               = "*"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "http_out" {
  name                                                 = "http_out"
  description                                          = "Allow outbound http access"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "3000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "80"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "https_out" {
  name                                                 = "https_out"
  description                                          = "Allow outbound https access"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "3001"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "443"
}
