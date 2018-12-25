# provider
provider "aws" {
  region  = "${var.aws_region}"
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  vpc_cidr                 = "${var.vpc_cidr}"
  aws_region               = "${var.aws_region}"
  aws_azs                  = "${var.aws_azs}"
  vpc_name                 = "${var.aws_account}"
  tag_name_identifier      = "${var.tag_name_identifier}"
  internet_gateway_enabled = "${var.internet_gateway_enabled}"
  enable_dynamodb_endpoint = "${var.enable_dynamodb_endpoint}"
  enable_s3_endpoint       = "${var.enable_s3_endpoint}"
}

module "vpc-endpoint-interface" {
  source                     = "../../terraform_module/endpoint-interface"
  aws_region                 = "${var.aws_region}"
  vpc_id                     = "${module.vpc.vpc_id}"
  type_of_service            = "${var.type_of_service}"
  endpoint_service_name      = "${var.endpoint_service_name}"
  private_dns_enabled        = "${var.private_dns_enabled}"
  list_of_security_group_ids = ["${module.vpc.vpc_private_sg_id}"]
  list_of_endpoint_subnets   = ["${module.vpc.vpc_private_subnet_ids}"]
}
