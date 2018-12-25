output "role" {
  value = "${module.create-role.name}"
}

output "service_principal" {
  value = "${module.create-role.service_principals}"
}
