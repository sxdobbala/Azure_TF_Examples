# Create an IAM role and allow the 'ec2' service principal to 'assume' the role

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create a role; allow ec2 service principal to assume the role
module "create-role" {
  source                         = "../../modules/iam-role"
  name                           = "role-with-service-principal"
  namespace                      = "example-${module.random-name.name}"
  assume_role_service_principals = ["ec2.amazonaws.com"]

  global_tags = {
    global_tag = "example"
  }
}
