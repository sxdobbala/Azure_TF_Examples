output "id" {
  value = "${azurerm_virtual_network.virtual_network.id}"
  description = "The virtual NetworkConfiguration ID."
}

output "name" {
  value = "${azurerm_virtual_network.virtual_network.name}"
  description = "The name of the virtual network."
}

output "resource_group_name" {
  value = "${azurerm_virtual_network.virtual_network.resource_group_name}"
  description = "The name of the resource group in which to create the virtual network."
}

output "location" {
  value = "${azurerm_virtual_network.virtual_network.location}"
  description = "The location/region where the virtual network is created."
}

output "address_space" {
  value = "${azurerm_virtual_network.virtual_network.address_space}"
  description = "The address space that is used the virtual network."
}
