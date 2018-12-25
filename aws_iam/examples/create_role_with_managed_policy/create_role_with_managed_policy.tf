# Create an IAM policy; create an IAM role and attach the managed policy to the role

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create a policy
module "managed-policy" {
  source      = "../../modules/iam-policy"
  name        = "managed-policy"
  path        = "/"
  description = "Used for role-with-managed-policy example"
  document    = "${data.aws_iam_policy_document.managed-policy.json}"
  namespace   = "example-${module.random-name.name}"
}

# Create a role, attach the managed policy created above to the new role
module "create-role" {
  source                         = "../../modules/iam-role"
  name                           = "role-with-managed-policy"
  description                    = "role-with-managed-policy example"
  assume_role_service_principals = ["ec2.amazonaws.com"]
  custom_managed_policy_count    = 1
  custom_managed_policy          = ["${module.managed-policy.arn}"]
  namespace                      = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}
