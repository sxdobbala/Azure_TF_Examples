module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "resource_group" {
  source    = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  name      = "resource_group"
  namespace = "${module.random_name.name}"
  location  = "centralus"
}

module "network_security_group" {
  source              = "../../modules/network_security_group"
  name                = "network_security_group"
  namespace           = "${module.random_name.name}"
  resource_group_name = "${module.resource_group.name}"
  location            = "centralus"
}

resource "azurerm_network_security_rule" "network_security_rule" {
  name                        = "ExampleRule"
  resource_group_name         = "${module.resource_group.name}"
  network_security_group_name = "${module.network_security_group.name}"
  description                 = "An example network security rule."
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "3306"
  source_address_prefix       = "10.0.2.0/24"
  destination_address_prefix  = "10.0.3.0/24"
  access                      = "Allow"
  priority                    = "300"
  direction                   = "Outbound"
}
