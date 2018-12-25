provider "aws" {
  region  = "us-east-1"
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  aws_region               = "us-east-1"
  vpc_cidr                 = "10.0.0.0/16"
  aws_azs                  = ["us-east-1a", "us-east-1b", "us-east-1c"]
  vpc_name                 = "common-tech"
  tag_name_identifier      = "pe"
  internet_gateway_enabled = true
  enable_dynamodb_endpoint = true
  enable_s3_endpoint       = true

  global_tags = {
    terraform   = "true"
    Team        = "pe-cc-team"
    Environment = "Stage"
    CostCenter  = "abc123"
  }
}
