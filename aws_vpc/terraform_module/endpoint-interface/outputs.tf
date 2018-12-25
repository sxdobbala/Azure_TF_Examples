output "interface_endpoint_id" {
  value = "${aws_vpc_endpoint.interface.id}"
}

output "interface_endpoint_state" {
  value = "${aws_vpc_endpoint.interface.state}"
}

output "interface_endpoint_network_interface_ids" {
  value = "${aws_vpc_endpoint.interface.network_interface_ids}"
}

output "interface_endpoint_dns_entry" {
  value = "${aws_vpc_endpoint.interface.dns_entry}"
}

output "interface_endpoint_primary_dns_name" {
  value = "${lookup(aws_vpc_endpoint.interface.dns_entry[0], "dns_name")}"
}

output "interface_endpoint_primary_dns_zone_id" {
  value = "${lookup(aws_vpc_endpoint.interface.dns_entry[0], "hosted_zone_id")}"
}
