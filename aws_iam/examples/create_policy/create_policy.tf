# Create policy Example

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "create-policy" {
  source      = "../../modules/iam-policy"
  name        = "create-policy"
  path        = "/"
  description = "create-policy"
  document    = "${data.aws_iam_policy_document.create-policy.json}"
  namespace   = "example-${module.random-name.name}"
}
