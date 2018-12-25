provider "aws" {
   region  = "us-east-1"
}

locals {
  vpc_cidr                               = "10.3.0.0/16"
  current_subnet_length                  = 6
  number_of_public_subnets               = 0
  number_of_private_subnets              = 3
  list_of_cidr_block_for_public_subnets  = ["${split(",",join(",", null_resource.public-cidr-helper.*.triggers.list_of_cidr_block_for_public_subnets))}"]
  list_of_cidr_block_for_private_subnets = ["${split(",",join(",", null_resource.private-cidr-helper.*.triggers.list_of_cidr_block_for_private_subnets))}"]
  list_of_aws_az                         = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  aws_region               = "us-east-1"
  vpc_cidr                 = "${local.vpc_cidr}"
  vpc_name                 = "common-tech"
  tag_name_identifier      = "pe"
  internet_gateway_enabled = true
  enable_dynamodb_endpoint = true
  enable_s3_endpoint       = true
  nat_enabled              = true
  aws_azs                  = "${local.list_of_aws_az}"
}

resource "null_resource" "public-cidr-helper" {
  count = "${local.number_of_public_subnets}"

  triggers {
    list_of_cidr_block_for_public_subnets = "${cidrsubnet(local.vpc_cidr, 4, count.index + local.current_subnet_length)}"
  }
}

resource "null_resource" "private-cidr-helper" {
  count = "${local.number_of_private_subnets}"

  triggers {
    list_of_cidr_block_for_private_subnets = "${cidrsubnet(local.vpc_cidr, 4, count.index + local.number_of_public_subnets + local.current_subnet_length)}"
  }
}

module "rds-subnets" {
  source                                               = "../../terraform_module/subnets/"
  aws_region                                           = "us-east-1"
  vpc_id                                               = "${module.vpc.vpc_id}"
  vpc_endpoint_dynamodb_id                             = "${module.vpc.vpc_dynamodb_endpoint}"
  vpc_endpoint_s3_id                                   = "${module.vpc.vpc_s3_endpoint}"
  vpc_nat_gateway_ids                                  = "${module.vpc.vpc_nat_gateway}"
  associate_nat_gateway_with_private_route_table       = false
  associate_dynamodb_endpoint_with_private_route_table = false
  associate_s3_endpoint_with_private_route_table       = true
  create_nacl_for_private_subnets                      = true
  number_of_public_subnets                             = "${local.number_of_public_subnets}"
  number_of_private_subnets                            = "${local.number_of_private_subnets}"
  list_of_aws_az                                       = "${local.list_of_aws_az}"
  list_of_cidr_block_for_public_subnets                = "${local.list_of_cidr_block_for_public_subnets}"
  list_of_cidr_block_for_private_subnets               = "${local.list_of_cidr_block_for_private_subnets}"
  public_ip_on_launch                                  = false
  tag_name_identifier                                  = "pe-rds"

  other_private_subnet_tags = {
    "example-tag" = "rds-aurora"
  }
}
