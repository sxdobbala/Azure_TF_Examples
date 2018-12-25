# Create Simple Bucket Without a Policy Example

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "simple-bucket-without-policy" {
  source        = "../../modules/simple"
  name_suffix   = "simple-bucket-without-policy"
  custom_policy = ""
  namespace     = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example-${module.random_name.name}"
  }

  tags = {
    Name = "simple-bucket-without-policy"
  }
}
