# Central security account outputs
output "central_security_ro_role" {
  value = "${module.eis-central-ro-access-role.role_name}"
}

output "central_security_ro_managed_role_policies" {
  value = ["${module.eis-central-ro-access-role.federated_principals_managed_role_policies}"]
}

output "central_security_bg_role" {
  value = "${module.eis-central-bg-access-role.role_name}"
}

output "central_security_bg_managed_role_policies" {
  value = ["${module.eis-central-bg-access-role.federated_principals_managed_role_policies}"]
}
