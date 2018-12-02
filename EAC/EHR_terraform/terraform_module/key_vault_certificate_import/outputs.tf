output "id" {
  value = "${join("", concat(azurerm_key_vault_certificate.certificate_without_lifetime.*.id, azurerm_key_vault_certificate.certificate_with_lifetime.*.id))}"
}

output "version" {
  value = "${join("", concat(azurerm_key_vault_certificate.certificate_without_lifetime.*.version, azurerm_key_vault_certificate.certificate_with_lifetime.*.version))}"
}

output "name" {
  value = "${module.namespace.name}"
}
