module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "resource_group" {
  source    = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group?ref=v2.0.0"
  name      = "resource_group"
  namespace = "${module.random_name.name}"
  location  = "centralus"
}

module "public_ip" {
  source                  = "../../modules/public_ip/dynamic_with_domain"
  name                    = "public_ip"
  namespace               = "${module.random_name.name}"
  resource_group_name     = "${module.resource_group.name}"
  location                = "centralus"
  idle_timeout_in_minutes = "4"
  domain_name_label       = "${module.random_name.name}"
}
