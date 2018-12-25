output "role" {
  value = "${module.create-role.name}"
}

output "inline_policies" {
  value = ["${module.create-role.inline_policies}"]
}
