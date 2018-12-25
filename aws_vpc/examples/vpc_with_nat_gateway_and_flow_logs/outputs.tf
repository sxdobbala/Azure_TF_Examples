output "elb_dns_address" {
  value = "${aws_elb.example-web.dns_name}"
}

output "vpc_properties" {
  value = {
    vpc_name     = "${module.vpc.vpc_name}"
    vpc_id       = "${module.vpc.vpc_id}"
    vpc_cidr     = "${module.vpc.vpc_cidr_block}"
    public_sg    = "${module.vpc.vpc_public_sg_id}"
    private_sg   = "${module.vpc.vpc_private_sg_id}"
    public_nacl  = "${module.vpc.vpc_public_nacl_id}"
    private_nacl = "${module.vpc.vpc_private_nacl_id}"
    flow_log_id  = "${module.flow_logs.flow_log_id}"
  }
}

output "private_subnet_ids" {
  value = ["${module.vpc.vpc_private_subnet_ids}"]
}

output "public_subnet_ids" {
  value = ["${module.vpc.vpc_public_subnet_ids}"]
}

output "vpc_subnet_id_by_availability_zone_public" {
  value = "${
    zipmap(
      module.vpc.vpc_public_subnets_az,
      module.vpc.vpc_public_subnet_ids
    )
  }"
}

output "vpc_subnet_cidr_block_by_availability_zone_public" {
  value = "${
    zipmap(
      module.vpc.vpc_public_subnets_az,
      module.vpc.vpc_public_subnet_cidrs,
    )
  }"
}

output "vpc_subnet_id_by_availability_zone_private" {
  value = "${
    zipmap(
      module.vpc.vpc_private_subnets_az,
      module.vpc.vpc_private_subnet_ids
    )
  }"
}

output "vpc_subnet_cidr_block_by_availability_zone_private" {
  value = "${
    zipmap(
      module.vpc.vpc_private_subnets_az,
      module.vpc.vpc_private_subnet_cidrs,
    )
  }"
}
