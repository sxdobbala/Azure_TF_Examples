# Create Bucket With a Terraform lifecycle Example

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "bucket-with-tflife" {
  source      = "../../modules/tflife"
  name_suffix = "bucket-with-tflife"
  namespace   = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "bucket-with-tflife"
  }
}
