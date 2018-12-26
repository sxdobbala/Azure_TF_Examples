output "forwarder_admin_username" {
  value                                                = "${var.spadmuser}"
}
output "forwarder_admin_password" {
  value                                                = "${random_string.spadmpass.result}"
}
output "forwarder_url" {
  value                                                = "https://${azurerm_public_ip.splunkvm_splunk01_publicip01.fqdn}"
}
output "forwarder_vm_size" {
  value                                                = "${var.forwarder_vm_size}"
}
output "location" {
  value                                                = "${var.location}"
}
output "resource_group_name" {
  value                                                = "${module.splunkvm_resource_group.name}"
}
output "os_admin_username" {
  value                                                = "${var.admin_username}"
}
output "os_admin_password" {
  value                                                = "${random_string.password.result}"
}
