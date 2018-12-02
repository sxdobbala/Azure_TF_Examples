output "id" {
  value = "${join("",concat(azurerm_key_vault.key_vault.*.id, azurerm_key_vault.key_vault_auxap.*.id))}"
}

output "vault_uri" {
  value = "${join("",concat(azurerm_key_vault.key_vault.*.vault_uri,azurerm_key_vault.key_vault_auxap.*.vault_uri))}"
}

output "name" {
  value = "${join("",concat(azurerm_key_vault.key_vault.*.name,azurerm_key_vault.key_vault_auxap.*.name))}"
}
