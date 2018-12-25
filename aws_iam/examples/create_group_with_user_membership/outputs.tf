output "user1" {
  value = "${module.create-user1.name}"
}

output "user2" {
  value = "${module.create-user2.name}"
}

output "group" {
  value = "${module.create-group.name}"
}
