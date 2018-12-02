locals {
  version_tag = {
    "cc-eac_azure_virtual_network" = "v2.0.0"
  }
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

resource "azurerm_virtual_network" "virtual_network" {
  name                = "${module.namespace.name}"
  address_space       = ["${var.address_space}"]
  location            = "${var.location}"
  resource_group_name = "${var.resource_group_name}"
  tags                = "${merge(var.global_tags, var.tags, local.version_tag)}"
}
