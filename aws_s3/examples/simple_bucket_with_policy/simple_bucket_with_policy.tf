# Create Bucket Example with a policy attached

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create Bucket With a Policy Example
module "simple-bucket-with-policy" {
  source        = "../../modules/simple"
  name_suffix   = "simple-bucket-with-policy"
  custom_policy = "${data.aws_iam_policy_document.bucket_custom_policy.json}"
  namespace     = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "simple-bucket-with-policy"
  }
}
