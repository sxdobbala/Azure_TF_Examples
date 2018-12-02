locals {
  private_ip_address_allocation_types = {
    "static"  = "Static"
    "Static"  = "Static"
    "dynamic" = "Dynamic"
    "Dynamic" = "Dynamic"
  }

  ip_configuration_private_ip_address_allocation = "${lookup(local.private_ip_address_allocation_types, var.ip_configuration_private_ip_address_allocation, "Dynamic")}"

  // Appending random string to Internal DNS name label to avoid overlaps
  internal_dns_name_label = "${var.internal_dns_name_label == "default" ? format("%s-%s", var.internal_dns_name_label, module.random_dns_label_postfix.name) : var.internal_dns_name_label }"

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

module "random_dns_label_postfix" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

resource "azurerm_network_interface" "network_interface" {
  name                          = "${module.namespace.name}"
  resource_group_name           = "${var.resource_group_name}"
  location                      = "${var.location}"
  network_security_group_id     = "${var.network_security_group_id}"
  internal_dns_name_label       = "${local.internal_dns_name_label}"
  enable_ip_forwarding          = "${var.enable_ip_forwarding}"
  enable_accelerated_networking = "${var.enable_accelerated_networking}"
  dns_servers                   = ["${var.dns_servers}"]
  tags                          = "${merge(var.tags, local.version_tag)}"

  ip_configuration {
    name                                    = "${var.ip_configuration_name}"
    subnet_id                               = "${var.ip_configuration_subnet_id}"
    private_ip_address                      = "${var.ip_configuration_private_ip_address}"
    private_ip_address_allocation           = "${local.ip_configuration_private_ip_address_allocation}"
    public_ip_address_id                    = "${var.ip_configuration_public_ip_address_id}"
    load_balancer_backend_address_pools_ids = ["${var.ip_configuration_load_balancer_backend_address_pools_ids}"]
    load_balancer_inbound_nat_rules_ids     = ["${var.ip_configuration_load_balancer_inbound_nat_rules_ids}"]
    primary                                 = "true"
  }
}
