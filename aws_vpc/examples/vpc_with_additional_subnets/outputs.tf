output "vpc_id" {
  value = "${module.vpc.vpc_id}"
}

output "private_subnet_ids" {
  value = ["${module.vpc.vpc_private_subnet_ids}"]
}

output "public_subnet_ids" {
  value = ["${module.vpc.vpc_public_subnet_ids}"]
}

output "extra_public_subnet_ids" {
  value = "${module.rds-subnets.subnets_public_subnet_ids}"
}

output "extra_private_subnet_ids" {
  value = "${module.rds-subnets.subnets_private_subnet_ids}"
}

output "extra_public_subnet_cidrs" {
  value = "${module.rds-subnets.subnets_public_subnet_cidrs}"
}

output "extra_private_subnets_cidrs" {
  value = "${module.rds-subnets.subnets_private_subnet_cidrs}"
}

output "extra_public_subnet_route_tables" {
  value = "${module.rds-subnets.subnets_private_route_table}"
}

output "extra_private_subnet_route_tables" {
  value = "${module.rds-subnets.subnets_private_route_table}"
}

output "extra_public_subnet_nacl" {
  value = "${module.rds-subnets.subnets_public_nacl_id}"
}

output "extra_private_subnet_nacl" {
  value = "${module.rds-subnets.subnets_private_nacl_id}"
}
