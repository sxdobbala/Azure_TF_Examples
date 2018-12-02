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

resource "azurerm_public_ip" "public_ip" {
  name                         = "${module.namespace.name}"
  resource_group_name          = "${var.resource_group_name}"
  location                     = "${var.location}"
  sku                          = "${var.sku}"
  public_ip_address_allocation = "Static"
  idle_timeout_in_minutes      = "${var.idle_timeout_in_minutes}"
  reverse_fqdn                 = "${var.reverse_fqdn}"
  tags                         = "${merge(var.global_tags, var.tags, local.version_tag)}"
}

output "ip_address" {
  value       = "${azurerm_public_ip.public_ip.ip_address}"
  description = "The IP address value that was allocated."
}
