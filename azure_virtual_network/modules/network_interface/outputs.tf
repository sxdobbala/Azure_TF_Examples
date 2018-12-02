output "id" {
  description = "The Virtual Network Interface ID"
  value       = "${azurerm_network_interface.network_interface.id}"
}

output "private_ip_address" {
  description = "The private ip address of the network interface"
  value       = "${azurerm_network_interface.network_interface.private_ip_address}"
}