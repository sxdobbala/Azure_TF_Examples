output "fqdn" {
  value                                                = "https://${azurerm_public_ip.publicip01.fqdn}"
}
output "location" {
  value                                                = "${var.location}"
}
output "name" {
  value                                                 = "${module.random_name.name}"
}
output "os_admin_password" {
  value                                                = "${random_string.password.result}"
}
output "os_admin_username" {
  value                                                = "${var.admin_username}"
}
