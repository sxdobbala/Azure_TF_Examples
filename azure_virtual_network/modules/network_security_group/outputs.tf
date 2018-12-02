output "id" {
  value       = "${azurerm_network_security_group.network_security_group.id}"
  description = "The Network Security Group ID."
}

output "name" {
  value       = "${azurerm_network_security_group.network_security_group.name}"
  description = "The Network Security Group name."
}

output "event_hub_namespace_id" {
  value       = "${module.nsg_diagnostic_log.event_hub_namespace_id}"
  description = "The default event hub namespace for diagnostic logging."
}
