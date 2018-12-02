module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "resource_group" {
  source    = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  name      = "resource_group"
  namespace = "${module.random_name.name}"
  location  = "centralus"
}

module "virtual_network" {
  source              = "../../"
  name                = "virtual_network"
  namespace           = "${module.random_name.name}"
  resource_group_name = "${module.resource_group.name}"
  address_space       = ["10.0.0.0/16"]
  location            = "centralus"
}

module "subnet" {
  source                          = "../../modules/subnet"
  name                            = "subnet1"
  namespace                       = "${module.random_name.name}"
  resource_group_name             = "${module.resource_group.name}"
  virtual_network_name            = "${module.virtual_network.name}"
  address_prefix                  = "10.0.1.0/24"
  network_security_group_name     = "network_security_group"
  network_security_group_location = "centralus"
}

resource "azurerm_network_security_rule" "network_security_rule" {
  name                        = "ExampleRule"
  resource_group_name         = "${module.resource_group.name}"
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
