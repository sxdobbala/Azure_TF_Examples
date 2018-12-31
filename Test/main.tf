data "azurerm_resource_group" "resource_group" {
  name = "${var.resource_group_name}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "virtual_network" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//?ref=v2.0.0-beta2"
  name                = "${var.virtual_network_name}"
  namespace           = "${module.random_name.name}"
  resource_group_name = "${data.azurerm_resource_group.resource_group.name}"
  address_space       = "${var.virtual_network_address_space}"
  location            = "${var.location}"
}

module "subnet" {
  source                          = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/subnet?ref=v2.0.0-beta2"
  name                            = "${var.subnet_name}"
  namespace                       = "${module.random_name.name}"
  resource_group_name             = "${data.azurerm_resource_group.resource_group.name}"
  virtual_network_name            = "${module.virtual_network.name}"
  address_prefix                  = "${var.address_prefix}"
  network_security_group_name     = "${var.redbox_nsg_name}"
  network_security_group_location = "${var.location}"
}

resource "azurerm_network_security_rule" "network_security_rule" {
  name                        = "ExampleRule"
  resource_group_name         = "${data.azurerm_resource_group.resource_group.name}"
  network_security_group_name = "${module.subnet.network_security_group_name}"
  description                 = "An example network security rule."
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "443"
  source_address_prefix       = "Internet"
  destination_address_prefix  = "${module.subnet.address_prefix}"
  access                      = "Allow"
  priority                    = "200"
  direction                   = "Inbound"
}
