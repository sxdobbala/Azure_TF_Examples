output "vpc_id" {
  value = "${module.vpc.vpc_id}"
}

output "private_subnet_ids" {
  value = ["${module.vpc.vpc_private_subnet_ids}"]
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
