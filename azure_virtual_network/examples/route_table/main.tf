module "generate_name1" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "generate_name2" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "generate_name3" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "generate_name4" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "resource_group" {
  source   = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  name     = "${module.generate_name1.name}"
  location = "Central US"
}

module "route_table" {
  source              = "../../modules/route_table"
  name                = "route_table"
  namespace           = "example"
  resource_group_name = "${module.resource_group.name}"
  route_count         = 2

  route_list = [
    {
      name           = "${module.generate_name2.name}"
      address_prefix = "10.1.0.0/16"
      next_hop_type  = "vnetlocal"
    },
    {
      name           = "${module.generate_name3.name}"
      address_prefix = "10.3.0.0/16"
      next_hop_type  = "vnetlocal"
    },
  ]
}

module "virtual_network" {
  source              = "../../"
  name                = "virtual_network"
  namespace           = "${module.generate_name4.name}"
  address_space       = ["10.0.0.0/16"]
  resource_group_name = "${module.resource_group.name}"
}

module "subnet" {
  source                      = "../../modules/subnet"
  name                        = "subnet1"
  resource_group_name         = "${module.resource_group.name}"
  virtual_network_name        = "${module.virtual_network.name}"
  address_prefix              = "10.0.1.0/24"
  route_table_id              = "${module.route_table.id}"
  network_security_group_name = "network_security_group"
}
