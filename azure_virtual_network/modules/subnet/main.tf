module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

module "network_security_group" {
  source              = "../network_security_group"
  name                = "${var.network_security_group_name}"
  namespace           = "${var.namespace}"
  resource_group_name = "${var.resource_group_name}"
  location            = "${var.network_security_group_location}"
}

resource "azurerm_subnet" "subnet" {
  name                      = "${module.namespace.name}"
  resource_group_name       = "${var.resource_group_name}"
  virtual_network_name      = "${var.virtual_network_name}"
  address_prefix            = "${var.address_prefix}"
  network_security_group_id = "${module.network_security_group.id}"
  route_table_id            = "${var.route_table_id}"
  service_endpoints         = "${var.service_endpoints}"
}
