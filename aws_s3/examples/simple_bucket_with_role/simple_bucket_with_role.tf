# Create Simple Bucket, with a read_write role

# Configure the AWS Provider
provider "aws" {
  version    = "= 1.22.0"
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "simple-bucket-with-role" {
  source        = "../../modules/simple"
  name_suffix   = "simple-bucket-with-role"
  custom_policy = ""
  force_destroy = "true"

  roles = {
    bucket_read_only    = false
    bucket_read_write   = true
    bucket_full_control = false
  }

  namespace = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "simple-bucket-with-role"
  }
}
