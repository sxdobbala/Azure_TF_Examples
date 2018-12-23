data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "bucket_config_policy" {
  statement {
    sid     = "AWSConfigAclCheck"
    actions = ["s3:GetBucketAcl"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["config.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${var.config_s3_bucket}"]
  }

  statement {
    sid     = "AWSConfigWrite"
    actions = ["s3:PutObject"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["config.amazonaws.com"]
    }

    resources = "${var.config_client_accounts}"
  }
}

module "s3-central-config" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module/tflife?ref=v1.2.1"
  bucket_name               = "${var.config_s3_bucket}"
  bucket_custom_policy      = "${data.aws_iam_policy_document.bucket_config_policy.json}"
  bucket_versioning_enabled = true
  global_tags               = "${var.global_tags}"
  s3_tags                   = "${var.s3_tags}"
}

resource "aws_sqs_queue" "deadletter_queue" {
  name = "deadletter-queue-${data.aws_caller_identity.current.account_id}"
}

resource "aws_sqs_queue" "config_queue" {
  name                       = "s3-config-event-notification-queue-${data.aws_caller_identity.current.account_id}"
  visibility_timeout_seconds = 300                                                                                         # 5 minutes minimum
  redrive_policy             = "{\"deadLetterTargetArn\":\"${aws_sqs_queue.deadletter_queue.arn}\",\"maxReceiveCount\":5}"

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": [
        "sqs:GetQueueAttributes",
        "sqs:ListQueues",
        "sqs:ReceiveMessage",
        "sqs:GetQueueUrl",
        "sqs:SendMessage",
        "sqs:DeleteMessage"
      ],
      "Resource": "arn:aws:sqs:*:*:s3-config-event-notification-queue-${data.aws_caller_identity.current.account_id}",
      "Condition": {
        "ArnEquals": { "aws:SourceArn": "${module.s3-central-config.bucket_arn}" }
      }
    }
  ]
}
POLICY
}

resource "aws_s3_bucket_notification" "config_bucket_notification" {
  bucket = "${module.s3-central-config.bucket_id}"

  queue {
    queue_arn = "${aws_sqs_queue.config_queue.arn}"
    events    = ["s3:ObjectCreated:*"]
  }
}
