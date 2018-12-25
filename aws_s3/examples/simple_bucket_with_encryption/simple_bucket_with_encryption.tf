# Create Simple Bucket, with AES256 encryption

# Configure the AWS Provider
provider "aws" {
  version    = "= 1.23.0"
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "simple-bucket-with-encryption" {
  source        = "../../modules/simple"
  name_suffix   = "simple-bucket-with-encryption"
  sse_algorithm = "aes256"
  custom_policy = "${data.aws_iam_policy_document.bucket_custom_policy.json}"
  force_destroy = "true"
  namespace     = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "simple-bucket-with-encryption"
  }
}
