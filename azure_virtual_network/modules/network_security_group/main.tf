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

resource "azurerm_network_security_group" "network_security_group" {
  name                = "${module.namespace.name}"
  resource_group_name = "${var.resource_group_name}"
  location            = "${var.location}"
  tags                = "${merge(var.global_tags, var.tags, local.version_tag)}"
}

// Automatically add three rules when a new NSG is created.
// These rules deny all inbound and outbound traffic.
// These deny rules are given the lowest priorities so users
// can incrementally allow access as needed.
// Also, inbound Azure Load Balancer is also allowed so VMs
// can function.

resource "azurerm_network_security_rule" "deny_all_outbound" {
  name                        = "DenyAllOutboundTraffic"
  resource_group_name         = "${var.resource_group_name}"
  network_security_group_name = "${azurerm_network_security_group.network_security_group.name}"
  description                 = "Denies all outbound traffic."
  protocol                    = "*"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  access                      = "Deny"
  priority                    = "4096"
  direction                   = "Outbound"
}

resource "azurerm_network_security_rule" "deny_all_inbound" {
  name                        = "DenyAllInboundTraffic"
  resource_group_name         = "${var.resource_group_name}"
  network_security_group_name = "${azurerm_network_security_group.network_security_group.name}"
  description                 = "Denies all inbound traffic."
  protocol                    = "*"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  access                      = "Deny"
  priority                    = "4095"
  direction                   = "Inbound"
}

resource "azurerm_network_security_rule" "allow_inbound_azure_load_balancer" {
  name                        = "AllowInboundAzureLoadBalancer"
  resource_group_name         = "${var.resource_group_name}"
  network_security_group_name = "${azurerm_network_security_group.network_security_group.name}"
  description                 = "Allow inbound access from Azure Load Balancer."
  protocol                    = "*"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "AzureLoadBalancer"
  destination_address_prefix  = "*"
  access                      = "Allow"
  priority                    = "1000"
  direction                   = "Inbound"
}

// Automatically sets up diagnostic logging when a new NSG is created.
// Diagnostic logs will stream to the central logging subscription's event hub.

module "nsg_diagnostic_log" {
  source             = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common.git//terraform_module/azure/diagnostic_logs?ref=v1.2.1"
  resource_id        = "${azurerm_network_security_group.network_security_group.id}"
  event_hub_location = "${azurerm_network_security_group.network_security_group.location}"

  log_config = <<SETTINGS
'[{"category":"NetworkSecurityGroupRuleCounter","enabled":true},{"category":"NetworkSecurityGroupEvent","enabled":true}]'
SETTINGS
}
