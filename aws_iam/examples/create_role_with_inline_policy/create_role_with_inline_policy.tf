# Create an IAM role and attach a custom inline policy

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create a role, allow the user created above to assume the new role
module "create-role" {
  source                         = "../../modules/iam-role"
  name                           = "role-with-custom-inline-policy"
  description                    = "role-with-custom-inline-policy example"
  assume_role_service_principals = ["ec2.amazonaws.com"]
  custom_inline_policy_count     = 2

  custom_inline_policy = [
    {
      custom_inline_name   = "Policy1"
      custom_inline_policy = "${data.aws_iam_policy_document.custom_inline_policy1.json}"
    },
    {
      custom_inline_name   = "Policy2"
      custom_inline_policy = "${data.aws_iam_policy_document.custom_inline_policy2.json}"
    },
  ]

  namespace = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}
