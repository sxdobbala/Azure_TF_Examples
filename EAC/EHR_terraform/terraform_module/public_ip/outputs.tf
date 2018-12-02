output "id" {
  value = "${azurerm_public_ip.public_ip.id}"
  description = "The Public IP ID."
}

output "name" {
  value = "${azurerm_public_ip.public_ip.name}"
  description = "The Public IP name."
}
