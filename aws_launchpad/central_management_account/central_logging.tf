module "central_logging_firehose_to_splunk" {
  source                         = "git::https://github.optum.com/CommercialCloud-EAC/aws_kinesis_firehose.git?ref=v1.1.1"
  namespace                      = "${var.namespace}"
  central_logging_account        = "${var.central_logging_account}"
  name                           = "${var.name}"
  kinesis_stream_arn             = "${module.aws_kinesis_data_stream.arn}"
  kinesis_stream_role_arn        = "${module.data_stream_role.arn}"
  destination                    = "${var.destination}"
  splunk_conf_hec_endpoint       = "${var.splunk_conf_hec_endpoint}"
  hec_token                      = "${var.hec_token}"
  hec_endpoint_type              = "${var.hec_endpoint_type}"
  hec_acknowledgment_timeout     = "${var.hec_acknowledgment_timeout}"
  s3_backup_mode                 = "${var.s3_backup_mode}"
  s3_conf_buffer_interval        = "${var.s3_conf_buffer_interval}"
  s3_conf_buffer_size            = "${var.s3_conf_buffer_size}"
  s3_conf_compression_format     = "${var.s3_conf_compression_format}"
  lambda_processor_function_name = "${var.lambda_processor_function_name}"
  lambda_function_timeout        = "${var.lambda_function_timeout}"
  lambda_function_filename       = "${var.lambda_function_filename}"
  lambda_runtime                 = "${var.lambda_runtime}"
  versioning_enabled             = "${var.versioning_enabled}"
}

module "aws_cloudwatch_log_destination" {
  source                 = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git//modules/log_destination?ref=v2.0.0"
  name                   = "${var.log_destination_name}"
  target_arn             = "${module.central_logging_firehose_to_splunk.arn}"
  allowed_target_actions = ["firehose:*"]
  allowed_accounts       = "${var.allowed_accounts}"
}

module "aws_kinesis_data_stream" {
  source           = "git::https://github.optum.com/CommercialCloud-EAC/aws_kinesis.git//module/data_stream?ref=v1.0.0"
  namespace        = "${var.namespace}"
  name             = "${var.data_stream_name}"
  shard_count      = "${var.shard_count}"
  retention_period = "${var.retention_period}"
  encryption_type  = "KMS"
  kms_key_id       = "alias/aws/kinesis"
}

module "data_stream_role" {
  source                         = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//modules/iam-role?ref=v2.0.0"
  name                           = "${var.data_stream_name}-central-logging_role"
  description                    = "role-with-custom-inline-policy example"
  assume_role_service_principals = ["firehose.amazonaws.com"]
  custom_inline_policy_count     = 1

  custom_inline_policy = [
    {
      custom_inline_name   = "${var.data_stream_name}-central-logging_role_policy"
      custom_inline_policy = "${data.aws_iam_policy_document.data-stream-role-policy-document.json}"
    },
  ]
}

data "aws_iam_policy_document" "data-stream-role-policy-document" {
  statement {
    effect = "Allow"

    actions = [
      "kinesis:DescribeStream",
      "kinesis:GetShardIterator",
      "kinesis:GetRecords",
    ]

    resources = ["${module.aws_kinesis_data_stream.arn}"]
  }
}

data "aws_iam_policy_document" "lambda_logs_processor_role_policy" {
  statement {
    effect = "Allow"

    actions = [
      "sqs:SendMessage",
    ]

    resources = [
      "${module.sqs.arn}",
    ]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:ListBucket",
      "s3:GetObject",
    ]

    resources = [
      "${module.central_logging_firehose_to_splunk.bucket_arn}",
      "${module.central_logging_firehose_to_splunk.bucket_arn}/*",
    ]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:ListBucket",
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject",
    ]

    resources = [
      "${module.destination-s3-bucket.arn}",
      "${module.destination-s3-bucket.arn}/*",
    ]
  }
}

data "aws_iam_policy_document" "kms_policy" {
  statement {
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.central_account_id}:role/OrganizationAccountAccessRole"]
    }

    actions = [
      "kms:Create*",
      "kms:List*",
      "kms:Describe*",
      "kms:Enable*",
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

    actions = ["kms:Encrypt*",
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

module "destination-s3-bucket" {
  source        = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//modules/simple?ref=v2.0.0"
  name          = "lambda-logs-processor-bucket-${var.central_account_id}"
  force_destroy = true
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = "${module.central_logging_firehose_to_splunk.bucket_id}"

  lambda_function = {
    lambda_function_arn = "${module.lambda-with-s3-trigger.arn}"
    events              = ["s3:ObjectCreated:*"]
  }
}

module "create-kms-key" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_kms.git?ref=v2.0.0"
  description = "kms key for dead letter queue - sqs"
  alias_names = "${var.cmk_alias_name}"
  policy      = "${data.aws_iam_policy_document.kms_policy.json}"
}

module "sqs" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_sqs.git?ref=v1.0.0"
  name                      = "${var.dead_letter_queue_name}"
  policy_enforced           = "${var.policy_enforced}"
  redrive_queue             = "${var.redrive_queue}"
  max_message_size          = "${var.max_message_size}"
  message_retention_seconds = "${var.message_retention_seconds}"
  fifo_queue                = "${var.fifo_queue}"
  kms_master_key_id         = "alias/aws/sqs"
}

module "lambda-with-s3-trigger" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_lambda.git?ref=v2.1.0"
  attach_dead_letter_config = true

  dead_letter_config = {
    target_arn = "${module.sqs.arn}"
  }

  function_name              = "${var.function_name}"
  description                = "${var.description}"
  filename                   = "${var.filename}"
  custom_inline_policy_count = "${var.custom_inline_policy_count}"

  custom_inline_policies = [
    {
      custom_inline_name   = "${var.function_name}"
      custom_inline_policy = "${data.aws_iam_policy_document.lambda_logs_processor_role_policy.json}"
    },
  ]

  environment_vars = {
    destination_s3_bucket = "${module.destination-s3-bucket.id}"
  }

  trigger_count = "${var.trigger_count}"

  triggers = [
    {
      trigger_id         = "AllowExecuteFromS3Bucket"
      trigger_principal  = "s3.amazonaws.com"
      trigger_source_arn = "${module.central_logging_firehose_to_splunk.bucket_arn}"
    },
  ]

  runtime     = "${var.runtime}"
  memory_size = "${var.memory_size}"
  timeout     = "${var.timeout}"
}

module "log_group" {
  source            = "git::https://github.optum.com/CommercialCloud-EAC/aws_cloudwatch.git?ref=v2.1.0"
  name              = "/aws/lambda/${var.function_name}"
  retention_in_days = "${var.log_group_retention_in_days}"
  kms_key_id        = "${module.create-kms-key.arn}"
}
