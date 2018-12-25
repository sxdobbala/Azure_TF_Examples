# Create an IAM user; create an IAM role and allow the user to 'assume' the role

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

# Create a role, allow the user created above to assume the new role
module "create-role" {
  source                     = "../../modules/iam-role"
  name                       = "role-with-user-principal"
  assume_role_aws_principals = ["${module.create-user1.arn}"]
  namespace                  = "example-${module.random-name.name}"

  global_tags = {
    global_tag = "example"
  }
}
