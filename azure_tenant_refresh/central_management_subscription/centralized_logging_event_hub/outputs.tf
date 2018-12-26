output "vault_name" {
  value = "${module.logging_key_vault.name}"
}

output "event_hub_secret_name" {
  value = "${module.event_hub_secret.name}"
}

output "event_hub_secret_version" {
  value = "${module.event_hub_secret.version}"
}

output "event_hub_namespace_name" {
  value = "${var.location}-${local.subscription_substring}"
}

# These are needed by azure_tenant_refresh

output "tenant_id" {
  value = "${data.azurerm_client_config.current.tenant_id}"
}

output "subscription_id" {
  value = "${data.azurerm_client_config.current.subscription_id}"
}

output "resource_group_name" {
  value = "${module.logging_resource_group.name}"
}
