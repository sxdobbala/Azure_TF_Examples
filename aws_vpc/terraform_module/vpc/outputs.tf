output "vpc_id" {
  value = "${aws_vpc.main.id}"
}

output "vpc_name" {
  value = "${var.vpc_name}"
}

output "vpc_cidr_block" {
  value = "${var.vpc_cidr}"
}

# We have to do this join() & split() 'trick' because null_data_source and 
# the ternary operator can't output lists or maps

output "vpc_public_subnet_cidrs" {
  value = "${module.vpc-subnets.subnets_public_subnet_cidrs}"
}

output "vpc_private_subnet_cidrs" {
  value = "${module.vpc-subnets.subnets_private_subnet_cidrs}"
}

output "vpc_private_subnet_ids" {
  value = "${module.vpc-subnets.subnets_private_subnet_ids}"
}

output "vpc_public_subnet_ids" {
  value = "${module.vpc-subnets.subnets_public_subnet_ids}"
}

output "vpc_public_elb_sg_id" {
  value = "${element(concat(aws_security_group.default_public_elb.*.id, list("")), 0)}"
}

output "vpc_public_sg_id" {
  value = "${element(concat(aws_security_group.default_public_instance.*.id, list("")), 0)}"
}

output "vpc_private_sg_id" {
  value = "${aws_security_group.default_private_instance.id}"
}

output "vpc_public_nacl_id" {
  value = "${module.vpc-subnets.subnets_public_nacl_id}"
}

output "vpc_private_nacl_id" {
  value = "${module.vpc-subnets.subnets_private_nacl_id}"
}

output "vpc_s3_endpoint" {
  value = "${element(concat(aws_vpc_endpoint.s3.*.id, list("")), 0)}"
}

output "vpc_dynamodb_endpoint" {
  value = "${element(concat(aws_vpc_endpoint.dynamodb.*.id, list("")), 0)}"
}

output "vpc_internet_gateway_id" {
  value = "${element(concat(aws_internet_gateway.default.*.id, list("")), 0)}"
}

output "vpc_private_route_table" {
  value = "${module.vpc-subnets.subnets_private_route_table}"
}

output "vpc_public_route_table" {
  value = "${module.vpc-subnets.subnets_public_route_table}"
}

output "vpc_nat_gateway" {
  value = ["${split(",", join(",", aws_nat_gateway.nat_gw.*.id))}"]
}

output "vpc_public_subnets_az" {
  value = "${module.vpc-subnets.subnets_public_az}"
}

output "vpc_private_subnets_az" {
  value = "${module.vpc-subnets.subnets_private_az}"
}

output "flow_log_cw_log_group_name" {
  value = "${module.flow_logs.flow_log_cw_log_group_name}"
}
