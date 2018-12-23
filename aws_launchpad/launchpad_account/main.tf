# Needed to remove old cloudtrail
module "launchpad" {
  source               = "./launchpad"
  name_space           = "${var.name_space}"
  cloudtrail_name      = "${var.cloudtrail_name}"
  cloudtrail_s3_bucket = "${var.cloudtrail_s3_bucket}"
  global_tags          = "${var.global_tags}"
  cloudtrail_tags      = "${var.cloudtrail_tags}"
}

module "cloudtrail" {
  source                     = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudtrail.git//terraform_module/existing_s3_with_loggroup?ref=v1.2.0"
  name_space                 = "${var.name_space}"
  cloudtrail_name            = "${var.cloudtrail_name}"
  cloudtrail_s3_bucket       = "${var.cloudtrail_s3_bucket}"
  global_tags                = "${var.global_tags}"
  cloudtrail_tags            = "${var.cloudtrail_tags}"
  cloud_watch_logs_group_arn = "${module.cloudtrail_loggroup.arn}"
}

module "cloudtrail_loggroup" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git?ref=v2.1.0"
  name        = "${var.cloudtrail_log_group_name}"
  global_tags = "${var.global_tags}"
  tags        = "${var.cloudwatch_tags}"
  kms_key_id  = "${module.create_kms_key_with_alias.arn}"
}

module "vpc_flow_loggroup" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git?ref=v2.1.0"
  name        = "${var.cw_log_group_name}"
  global_tags = "${var.global_tags}"
  tags        = "${var.cloudwatch_tags}"
  kms_key_id  = "${module.create_kms_key_with_alias.arn}"
}

module "flow_log_subscription_filter" {
  source          = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git//modules/subscription_filter?ref=v2.1.0"
  name            = "subscription_filter-${module.vpc_flow_loggroup.name}"
  destination_arn = "arn:aws:logs:${var.aws_region}:${var.central_logging_account}:destination:${var.log_destination_name}"
  filter_pattern  = "${var.subscription_filter_pattern}"
  log_group_name  = "${module.vpc_flow_loggroup.name}"
}

module "cloudtrail_subscription_filter" {
  source          = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git//modules/subscription_filter?ref=v2.1.0"
  name            = "subscription-filter-${module.cloudtrail_loggroup.name}"
  destination_arn = "arn:aws:logs:${var.aws_region}:${var.central_logging_account}:destination:${var.log_destination_name}"
  filter_pattern  = "${var.subscription_filter_pattern}"
  log_group_name  = "${module.cloudtrail_loggroup.name}"
}

module "create_kms_key_with_alias" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_kms.git?ref=v2.0.0"
  description = "kms-key-for-loggroup"
  alias_names = ["loggroup_kms"]
  policy      = "${data.aws_iam_policy_document.kms_policy.json}"
  namespace   = "${var.name_space}"

  tags = {
    Name = "create-kms-key-with-alias"
  }
}

data "aws_iam_policy_document" "kms_policy" {
  statement {
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.launchpad_account}:role/OrganizationAccountAccessRole"]
    }

    actions = [
      "kms:Create*",
      "kms:Describe*",
      "kms:Enable*",
      "kms:List*",
      "kms:Put*",
      "kms:Update*",
      "kms:Revoke*",
      "kms:Disable*",
      "kms:Get*",
      "kms:Delete*",
      "kms:TagResource",
      "kms:UntagResource",
      "kms:ScheduleKeyDeletion",
      "kms:CancelKeyDeletion",
    ]

    resources = [
      "*",
    ]
  }

  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logs.${var.aws_region}.amazonaws.com"]
    }

    actions = [
      "kms:Encrypt*",
      "kms:Decrypt*",
      "kms:ReEncrypt*",
      "kms:GenerateDataKey*",
      "kms:Describe*",
    ]

    resources = [
      "*",
    ]
  }
}
