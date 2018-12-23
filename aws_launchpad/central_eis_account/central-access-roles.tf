provider "aws" {
  region = "${var.aws_region}"
}

data "aws_iam_policy_document" "eis_assume_role_policy" {
  statement {
    effect = "Allow"

    actions = [
      "sts:AssumeRole",
    ]

    resources = ["*"]
  }
}

module "eis-central-assume-role-policy" {
  source             = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/policy?ref=v1.0.4"
  policy_name        = "EnterpriseSecurityAssumeRolePolicy"
  policy_path        = "/"
  policy_description = "Further restricts AWS IAM read-only access to EIS personnel by removing unneeded permissions."
  policy_document    = "${data.aws_iam_policy_document.eis_assume_role_policy.json}"
  name_space         = "${var.namespace}"
}

module "eis-central-ro-access-role" {
  source                               = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.4"
  role_name                            = "AWS_${var.account_id}_EISRead"
  role_description                     = "Read-only access to all resources for EIS personnel."
  name_space                           = "${var.namespace}"
  role_assumerole_federated_principals = ["arn:aws:iam::${var.account_id}:saml-provider/${var.saml_provider}"]
  role_custom_managed_policy_count     = "1"
  role_custom_managed_policy           = ["${module.eis-central-assume-role-policy.policy_arn}"]

  global_tags = {
    global_tag = "${var.namespace}"
  }
}

module "eis-central-bg-access-role" {
  source                               = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.4"
  role_name                            = "AWS_${var.account_id}_EISBreakGlass"
  role_description                     = "Break glass access to all resources for EIS personnel."
  name_space                           = "${var.namespace}"
  role_assumerole_federated_principals = ["arn:aws:iam::${var.account_id}:saml-provider/${var.saml_provider}"]
  role_custom_managed_policy_count     = "1"
  role_custom_managed_policy           = ["${module.eis-central-assume-role-policy.policy_arn}"]

  global_tags = {
    global_tag = "${var.namespace}"
  }
}
