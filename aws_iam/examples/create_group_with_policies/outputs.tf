output "group" {
  value = "${module.create-group.name}"
}

output "managed_policies" {
  value = ["${module.create-group.managed_policies}"]
}

output "inline_policies" {
  value = ["${module.create-group.inline_policies}"]
}

output "group_members" {
  value = ["${module.create-group.users}"]
}
