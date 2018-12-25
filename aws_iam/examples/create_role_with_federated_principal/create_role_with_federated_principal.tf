# Create an IAM role and allow the federated principals to 'assume' the role

# Configure the AWS Provider
provider "aws" {
  region = "${var.aws_region}"
}

module "random-name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Throw in a managed policy
module "managed-policy" {
  source      = "../../modules/iam-policy"
  name        = "managed-policy"
  path        = "/"
  description = "Used for role-with-managed-policy example"
  document    = "${data.aws_iam_policy_document.managed-policy.json}"
  namespace   = "example-${module.random-name.name}"
}

# Create the role to allow the federated principals to assume this role
module "create-role" {
  source                           = "../../modules/iam-role"
  name                             = "role-with-fed-principal"
  description                      = "A role with federated principal"
  namespace                        = "example-${module.random-name.name}"
  assume_role_federated_principals = ["arn:aws:iam::006694512080:saml-provider/UHG_AWS_POC"]
  custom_managed_policy_count      = 2
  custom_managed_policy            = ["arn:aws:iam::aws:policy/ReadOnlyAccess", "${module.managed-policy.arn}"]

  global_tags = {
    global_tag = "example"
  }
}
