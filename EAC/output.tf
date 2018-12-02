output "subnet_name_list" {
  value = "${module.subnet.subnet_name}"
}

output "subnet_id_list" {
  value = "${module.subnet.subnet_id}"
}

output "virtual_network_name" {
  value = "${module.virtual_network.virtual_network_name}"
}

output "virtual_network_id" {
  value = "${module.virtual_network.virtual_network_id}"
}