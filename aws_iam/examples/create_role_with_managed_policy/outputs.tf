output "role" {
  value = "${module.create-role.name}"
}

output "managed_policies" {
  value = ["${module.create-role.managed_policies}"]
}
