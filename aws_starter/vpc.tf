provider "aws" {
  region = "us-east-1"
}

module "vpc" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/aws_vpc.git//terraform_module/vpc?ref=v1.6.0"
  aws_region          = "us-east-1"
  aws_profile         = "saml"
  vpc_cidr            = "10.0.0.0/16"
  aws_azs             = ["us-east-1a", "us-east-1b", "us-east-1c"]
  vpc_name            = "common-tech"
  tag_name_identifier = "pe"

  global_tags = {
    terraform = "true"
  }
}
