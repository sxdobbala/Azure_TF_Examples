output "vpc_properties" {
  value = {
    vpc_name = "${module.vpc.vpc_name}"
    vpc_id   = "${module.vpc.vpc_id}"
    vpc_cidr = "${module.vpc.vpc_cidr_block}"
  }
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

output "kms_interface_endpoint_id" {
  value = "${module.vpc-endpoint-interface.interface_endpoint_id}"
}

output "kms_interface_endpoint_network_interface_ids" {
  value = "${module.vpc-endpoint-interface.interface_endpoint_network_interface_ids}"
}

output "kms_interface_endpoint_primary_dns_name" {
  value = "${module.vpc-endpoint-interface.interface_endpoint_primary_dns_name}"
}

output "kms_interface_endpoint_primary_hosted_zone_ids" {
  value = "${module.vpc-endpoint-interface.interface_endpoint_primary_dns_zone_id}"
}

output "kms_interface_endpoint_all_dns_records" {
  value = "${module.vpc-endpoint-interface.interface_endpoint_dns_entry}"
}
