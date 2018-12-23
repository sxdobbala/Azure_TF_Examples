module "eis-member-bg-access-role" {
  source                           = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.4"
  role_name                        = "EIS_AWS_BreakGlass"
  role_description                 = "Break glass access to all resources for EIS personnel."
  name_space                       = "${var.name_space}"
  role_assumerole_aws_principals   = ["arn:aws:iam::${var.eis_central_security_id}:role/AWS_${var.eis_central_security_id}_EISBreakGlass"]
  role_custom_managed_policy_count = "1"
  role_custom_managed_policy       = ["arn:aws:iam::aws:policy/PowerUserAccess"]

  global_tags = {
    global_tag = "${var.name_space}"
  }
}

module "eis-member-ro-access-policy" {
  source             = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/policy?ref=v1.0.4"
  policy_name        = "EnterpriseSecurityReadOnlyAccessAddendum"
  policy_path        = "/"
  policy_description = "Further restricts AWS IAM read-only access to EIS personnel by removing unneeded permissions."
  policy_document    = "${data.aws_iam_policy_document.eis_readonly_access_addendum_policy.json}"
  name_space         = "${var.name_space}"
}

module "eis-member-ro-access-role" {
  source                           = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.4"
  role_name                        = "EIS_AWS_Read"
  role_description                 = "Read-only access to all resources for EIS personnel."
  name_space                       = "${var.name_space}"
  role_assumerole_aws_principals   = ["arn:aws:iam::${var.eis_central_security_id}:role/AWS_${var.eis_central_security_id}_EISRead"]
  role_custom_managed_policy_count = "2"
  role_custom_managed_policy       = ["arn:aws:iam::aws:policy/ReadOnlyAccess", "${module.eis-member-ro-access-policy.policy_arn}"]

  global_tags = {
    global_tag = "${var.name_space}"
  }
}

data "aws_iam_policy_document" "eis_readonly_access_addendum_policy" {
  statement {
    effect = "Deny"

    actions = [
      "cloudformation:GetTemplate",
      "dynamodb:GetItem",
      "dynamodb:BatchGetItem",
      "dynamodb:Query",
      "dynamodb:Scan",
      "ec2:GetConsoleOutput",
      "ec2:GetConsoleScreenshot",
      "ecr:BatchGetImage",
      "ecr:GetAuthorizationToken",
      "ecr:GetDownloadUrlForLayer",
      "kinesis:Get*",
      "lambda:GetFunction",
      "logs:GetLogEvents",
      "s3:GetObject",
      "sdb:Select*",
      "sqs:ReceiveMessage",
    ]

    resources = ["*"]
  }
}
