output "tenant_id" {
  value = "${module.central_management_subscription_event_hub.tenant_id}"
}

output "subscription_id" {
  value = "${module.central_management_subscription_event_hub.subscription_id}"
}

output "event_hub_resource_group_name" {
  value = "${module.central_management_subscription_event_hub.resource_group_name}"
}

output "event_hub_vault_name" {
  value = "${module.central_management_subscription_event_hub.vault_name}"
}

output "event_hub_secret_name" {
  value = "${module.central_management_subscription_event_hub.event_hub_secret_name}"
}

output "event_hub_secret_version" {
  value = "${module.central_management_subscription_event_hub.event_hub_secret_version}"
}

output "event_hub_namespace_name" {
  value = "${module.central_management_subscription_event_hub.event_hub_namespace_name}"
}

output "splunk_forwarder_url" {
  value = "${module.central_management_subscription_splunk_forwarder.forwarder_url}"
}

output "splunk_forwarder_admin_username" {
  value = "${module.central_management_subscription_splunk_forwarder.forwarder_admin_username}"
}

output "splunk_forwarder_admin_password" {
  value = "${module.central_management_subscription_splunk_forwarder.forwarder_admin_password}"
}

output "splunk_os_admin_password" {
  value = "${module.central_management_subscription_splunk_forwarder.os_admin_password}"
}

output "splunk_os_admin_username" {
  value = "${module.central_management_subscription_splunk_forwarder.os_admin_username}"
}

output "splunk_resource_group_name" {
  value = "${module.central_management_subscription_splunk_forwarder.resource_group_name}"
}
