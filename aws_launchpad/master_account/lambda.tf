provider "aws" {
  region = "${var.aws_region}"
}

resource "aws_sns_topic" "topic_lambda_invoker" {
  name = "${var.lambda_topic_name}"
}

data "aws_iam_policy_document" "lambda_invoker_exec_policy" {
  statement {
    effect = "Allow"

    actions = [
      "organizations:ListAccounts",
      "sts:assumerole",
      "sns:Publish",
      "sns:ListTopics",
      "logs:PutLogEvents",
      "s3:GetObject",
      "s3:ListBucket",
    ]

    resources = ["*"]
  }
}

module "lambda_invoker" {
  source        = "git::https://github.optum.com/CommercialCloud-EAC/aws_lambda.git?ref=v2.0.0"
  namespace     = "${var.namespace}"
  function_name = "lambda_invoker"
  description   = "Launchpad Lambda Function to Invoke Central Management Lambdas."
  s3_bucket     = "${var.lambda_zip_bucket}"
  s3_key        = "${var.lambda_zip_path}"

  custom_inline_policy_count = 1

  custom_inline_policies = [
    {
      custom_inline_name   = "LambdaInvokerPolicy"
      custom_inline_policy = "${data.aws_iam_policy_document.lambda_invoker_exec_policy.json}"
    },
  ]

  trigger_count = 1

  triggers = [{
    trigger_id         = "AllowTriggerFromCloudWatchScheduler"
    trigger_principal  = "events.amazonaws.com"
    trigger_source_arn = "${module.scheduler.rule_arn}"
  }]

  environment_vars = {
    LOGLEVEL        = "${var.env_loglevel}"
    ASSUME_ROLE_ARN = "${var.env_assume_role_arn}"
    LAMBDA_ARN_LIST = "${var.env_lambda_arn_list}"
    SNS_TOPIC_ARN   = "${var.env_sns_topic_arn}"
  }

  global_tags = "${var.global_tags}"
  tags        = "${var.lambda_tags}"
}

module "scheduler" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git//modules/event_scheduler?ref=v2.1.0"
  name                = "scheduler-for-lambda-invoker"
  namespace           = "${var.namespace}"
  schedule_expression = "${var.schedule_expression}"
  target_id           = "EC2MetadataCollectionLambda"
  target_arn          = "${module.lambda_invoker.arn}"
}
