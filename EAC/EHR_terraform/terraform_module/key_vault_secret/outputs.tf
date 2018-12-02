output "id" {
  value = "${azurerm_key_vault_secret.secret.id}"
}

output "version" {
  value = "${azurerm_key_vault_secret.secret.version}"
}

output "name" {
  value = "${module.namespace.name}"
}
