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
  sku                          = "Basic"
  public_ip_address_allocation = "Dynamic"
  idle_timeout_in_minutes      = "${var.idle_timeout_in_minutes}"
  domain_name_label            = "${var.domain_name_label}"
  reverse_fqdn                 = "${var.reverse_fqdn}"
  tags                         = "${merge(var.global_tags, var.tags, local.version_tag)}"
}

output "fqdn" {
  value       = "${azurerm_public_ip.public_ip.fqdn}"
  description = "Fully qualified domain name of the A DNS record associated with the public IP. This is the concatenation of the domainNameLabel and the regionalized DNS zone."
}
