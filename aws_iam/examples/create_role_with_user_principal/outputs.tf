output "user" {
  value = "${module.create-user1.name}"
}

output "role" {
  value = "${module.create-role.name}"
}

output "user_principal" {
  value = "${module.create-role.user_principals}"
}
