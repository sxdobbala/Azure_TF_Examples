provider "aws" {
  region = "${var.aws_region}"
}

data "aws_caller_identity" "current" {}

resource "aws_flow_log" "main_vpc" {
  log_group_name = "${var.flow_log_group_name}"
  iam_role_arn   = "${module.create-role-for-flow-logs.arn}"
  vpc_id         = "${var.vpc_id}"
  traffic_type   = "${var.traffic_type}"
}

module "create-role-for-flow-logs" {
  source                         = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//modules/iam-role?ref=v2.0.0"
  name                           = "${var.tag_name_identifier}_flow_logs"
  description                    = "vpc flow logs role"
  assume_role_service_principals = ["vpc-flow-logs.amazonaws.com"]
  custom_managed_policy_count    = 1
  custom_managed_policy          = ["${module.managed-policy-for-flow-logs.arn}"]
}

module "managed-policy-for-flow-logs" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//modules/iam-policy?ref=v2.0.0"
  name        = "${var.tag_name_identifier}_flow_logs_policy"
  path        = "/"
  description = "vpc flow logs policy"
  document    = "${data.aws_iam_policy_document.flow_logs.json}"
}

data "aws_iam_policy_document" "flow_logs" {
  statement {
    effect = "Allow"

    actions = [
      "logs:DescribeLogGroups",
      "logs:PutLogEvents",
      "logs:CreateLogStream",
      "logs:DescribeLogStreams",
    ]

    resources = ["arn:aws:logs:${var.aws_region}:${data.aws_caller_identity.current.account_id}:log-group:${var.flow_log_group_name}:*"]
  }
}
