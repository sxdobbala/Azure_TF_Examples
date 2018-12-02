output "id" {
  value       = "${azurerm_subnet.subnet.id}"
  description = "The subnet ID."
}

output "ip_configurations" {
  value       = "${azurerm_subnet.subnet.ip_configurations}"
  description = "The collection of IP Configurations with IPs within this subnet."
}

output "name" {
  value       = "${azurerm_subnet.subnet.name}"
  description = "The name of the subnet."
}

output "resource_group_name" {
  value       = "${azurerm_subnet.subnet.resource_group_name}"
  description = "The name of the resource group in which the subnet is created in."
}

output "virtual_network_name" {
  value       = "${azurerm_subnet.subnet.virtual_network_name}"
  description = "The name of the virtual network in which the subnet is created in."
}

output "address_prefix" {
  value       = "${azurerm_subnet.subnet.address_prefix}"
  description = "The address prefix for the subnet."
}

output "network_security_group_id" {
  value       = "${module.network_security_group.id}"
  description = "The subnet's Network Security Group ID."
}

output "network_security_group_name" {
  value       = "${module.network_security_group.name}"
  description = "The subnet's Network Security Group name."
}

output "event_hub_namespace_id" {
  value       = "${module.network_security_group.event_hub_namespace_id}"
  description = "The default event hub namespace for diagnostic logging."
}
