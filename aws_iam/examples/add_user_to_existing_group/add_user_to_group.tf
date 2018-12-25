# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Add user to already existing group in AWS

#Module to create single user with enable-MFA policy
module "create-user1" {
  source    = "../../modules/iam-user"
  name      = "user1"
  namespace = "example-${module.random-name.name}"

  global_tags = {
    global_tag = ""
  }
}

#Uncomment after initial run. Additional user to be added.
/*
module "create-user2" {
  source = "../../modules/iam-user"
  name = "user2"
  namespace = "example-${module.random-name.name}"
  global_tags = {
    global_tag = ""
   }
  }
*/

#Initial run: Create group.
#Second run: Add "${module.create-user2.user_name}" to users
module "create-group" {
  source                      = "../../modules/iam-group"
  name                        = "example-group"
  custom_managed_policy_count = 0
  custom_managed_policy       = []
  custom_inline_policy        = []
  users                       = ["${module.create-user1.name}"]
  namespace                   = "example-${module.random-name.name}"

  global_tags = {
    global_tag = ""
  }
}
