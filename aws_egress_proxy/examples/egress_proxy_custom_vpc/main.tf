# provider
provider "aws" {
  profile = "${var.aws_profile}"
  region  = "${var.aws_region}"
}

data "aws_caller_identity" "current" {}

module "vpc" {
  source                   = "git::https://github.optum.com/CommercialCloud-EAC/aws_vpc.git//terraform_module/vpc?ref=v1.5.2"
  vpc_cidr                 = "${var.vpc_cidr}"
  aws_region               = "${var.aws_region}"
  aws_profile              = "${var.aws_profile}"
  aws_azs                  = "${var.aws_azs}"
  vpc_name                 = "${var.aws_account}"
  tag_name_identifier      = "${var.tag_name_identifier}"
  internet_gateway_enabled = "${var.internet_gateway_enabled}"
  enable_dynamodb_endpoint = "${var.enable_dynamodb_endpoint}"
  enable_s3_endpoint       = "${var.enable_s3_endpoint}"
}

module "egress_proxy" {
  source                      = "../../"
  aws_region                  = "${var.aws_region}"
  proxy_name                  = "example-egress-proxy"
  vpc_id                      = "${module.vpc.vpc_id}"
  subnets_for_proxy_placement = ["${module.vpc.vpc_public_subnet_ids}"]
  s3_bucket_name_prefix       = "test1234"

  ## Using this "${module.bucket-for-logging.bucket_id}" results in value of 'count' cannot be computed
  s3_log_bucket_name = "bucket-with-logging-logs-${data.aws_caller_identity.current.account_id}"
  enable_asg_policy  = false
  namespace          = "${var.tag_name_identifier}"
}

module "bucket-for-logging" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module//simple?ref=v1.4.0"
  bucket_name = "bucket-with-logging-logs-${data.aws_caller_identity.current.account_id}"
  bucket_acl  = "log-delivery-write"
}
