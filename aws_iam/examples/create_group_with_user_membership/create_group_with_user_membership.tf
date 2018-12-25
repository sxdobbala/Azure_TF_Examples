# Create an IAM user; create a managed and inline policy; create a IAM group and assign the user to the group

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create a user
module "create-user1" {
  source    = "../../modules/iam-user"
  name      = "user1"
  namespace = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}

# Create a user2
module "create-user2" {
  source    = "../../modules/iam-user"
  name      = "user2"
  namespace = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}

# Create a policy
module "managed-policy" {
  source      = "../../modules/iam-policy"
  name        = "managed-policy"
  path        = "/"
  description = "Used for group-with-policies example"
  document    = "${data.aws_iam_policy_document.managed-policy.json}"
  namespace   = "example-${module.random-name.name}"
}

# Create a group, attach the managed policy created above to the new group
module "create-group" {
  source                      = "../../modules/iam-group"
  name                        = "group-with-policies"
  custom_managed_policy_count = 1
  custom_managed_policy       = ["${module.managed-policy.arn}"]
  custom_inline_policy_count  = 1

  custom_inline_policy = [
    {
      group_custom_inline_name = "group-inline-policy"
      custom_inline_policy     = "${data.aws_iam_policy_document.custom_inline_policy.json}"
    },
  ]

  users     = ["${module.create-user1.name}", "${module.create-user2.name}"]
  namespace = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}
