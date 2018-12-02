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
  address_space       = ["10.0.0.0/16"]
  resource_group_name = "${module.resource_group.name}"
}

module "subnet" {
  source                      = "../../modules/subnet"
  name                        = "subnet1"
  namespace                   = "${module.random_name.name}"
  resource_group_name         = "${module.resource_group.name}"
  virtual_network_name        = "${module.virtual_network.name}"
  address_prefix              = "10.0.1.0/24"
  network_security_group_name = "network_security_group"
}

module "primary_public_ip" {
  source                  = "../../modules/public_ip/static_without_domain"
  name                    = "primary_public_ip"
  namespace               = "${module.random_name.name}"
  resource_group_name     = "${module.resource_group.name}"
  location                = "centralus"
  idle_timeout_in_minutes = "4"
}

module "secondary_public_ip" {
  source                  = "../../modules/public_ip/static_without_domain"
  name                    = "secondary_public_ip"
  namespace               = "${module.random_name.name}"
  resource_group_name     = "${module.resource_group.name}"
  location                = "centralus"
  idle_timeout_in_minutes = "4"
}

module "network_interface" {
  source              = "../../modules/network_interface/primary_and_secondary_ip_configuration"
  name                = "network_interface"
  resource_group_name = "${module.resource_group.name}"
  location            = "centralus"

  ip_configuration_name                                    = "primary_config"
  ip_configuration_subnet_id                               = "${module.subnet.id}"
  ip_configuration_private_ip_address_allocation           = "Dynamic"
  ip_configuration_public_ip_address_id                    = "${module.primary_public_ip.id}"
  secondary_ip_configuration_name                          = "secondary_config"
  secondary_ip_configuration_subnet_id                     = "${module.subnet.id}"
  secondary_ip_configuration_private_ip_address            = "10.0.1.5"
  secondary_ip_configuration_private_ip_address_allocation = "Static"
  secondary_ip_configuration_public_ip_address_id          = "${module.secondary_public_ip.id}"
}
