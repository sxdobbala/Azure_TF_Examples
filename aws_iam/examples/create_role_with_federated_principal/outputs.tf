output "federated_principal_role" {
  value = "${module.create-role.name}"
}

output "federated_principal" {
  value = "${module.create-role.federated_principals}"
}

output "managed_policies" {
  value = ["${module.create-role.managed_policies}"]
}
