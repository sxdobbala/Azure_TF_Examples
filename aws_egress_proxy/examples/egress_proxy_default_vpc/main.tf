# provider
provider "aws" {
  profile = "${var.aws_profile}"
  region  = "${var.aws_region}"
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "default" {
  vpc_id = "${data.aws_vpc.default.id}"
}

data "aws_caller_identity" "current" {}

module "egress_proxy" {
  source                      = "../../"
  aws_region                  = "${var.aws_region}"
  proxy_name                  = "example-egress-proxy"
  vpc_id                      = "${data.aws_vpc.default.id}"
  subnets_for_proxy_placement = ["${data.aws_subnet_ids.default.ids}"]
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
