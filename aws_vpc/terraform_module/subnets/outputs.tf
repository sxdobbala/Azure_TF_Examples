output "subnets_public_subnet_ids" {
  value = ["${split(",", join(",", aws_subnet.public.*.id))}"]
}

output "subnets_private_subnet_ids" {
  value = ["${split(",",join(",", aws_subnet.private.*.id))}"]
}

output "subnets_public_subnet_cidrs" {
  value = ["${split(",",join(",", aws_subnet.public.*.cidr_block))}"]
}

output "subnets_private_subnet_cidrs" {
  value = ["${split(",",join(",", aws_subnet.private.*.cidr_block))}"]
}

output "subnets_public_nacl_id" {
  value = "${element(concat(aws_network_acl.public.*.id, list("")), 0)}"
}

output "subnets_private_nacl_id" {
  value = "${element(concat(aws_network_acl.private.*.id, list("")), 0)}"
}

output "subnets_private_route_table" {
  value = ["${split(",", join(",", aws_route_table.private.*.id))}"]
}

output "subnets_public_route_table" {
  value = ["${split(",", join(",", aws_route_table.public.*.id))}"]
}

output "subnets_public_az" {
  value = ["${split(",",join(",", aws_subnet.public.*.availability_zone))}"]
}

output "subnets_private_az" {
  value = ["${split(",",join(",", aws_subnet.private.*.availability_zone))}"]
}
