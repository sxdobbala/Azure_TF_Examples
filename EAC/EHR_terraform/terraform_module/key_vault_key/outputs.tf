output "id" {
  value = "${azurerm_key_vault_key.key.id}"
}

output "version" {
  value = "${azurerm_key_vault_key.key.version}"
}

output "n" {
  value = "${azurerm_key_vault_key.key.n}"
}

output "e" {
  value = "${azurerm_key_vault_key.key.e}"
}

output "name" {
  value = "${module.namespace.name}"
}
