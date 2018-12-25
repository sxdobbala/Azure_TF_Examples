provider "aws" {
  region = "${var.aws_region}"
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  aws_region               = "${var.aws_region}"
  vpc_cidr                 = "10.0.0.0/16"
  aws_azs                  = ["us-east-1a", "us-east-1b", "us-east-1c"]
  vpc_name                 = "cc-team"
  internet_gateway_enabled = "${var.internet_gateway_enabled}"
  tag_name_identifier      = "${var.tag_name_identifier}"

  global_tags = {
    terraform = "true"
  }
}
