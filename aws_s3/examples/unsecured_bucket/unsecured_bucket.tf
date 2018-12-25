provider "aws" {
  region = "us-east-1"
}

data "aws_caller_identity" "current" {}

locals {
  aws_account_id = "${data.aws_caller_identity.current.account_id}"
  name           = "unsecured-bucket-example-${local.aws_account_id}"
}

data "aws_iam_policy_document" "custom_policy" {
  statement {
    sid     = "AWSGetBucketAcl"
    actions = ["s3:GetBucketAcl"]
    effect  = "Allow"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    resources = ["arn:aws:s3:::${local.name}"]
  }

  statement {
    sid     = "AWSGetPut"
    actions = ["s3:PutObject", "s3:GetObject"]
    effect  = "Allow"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    resources = ["arn:aws:s3:::${local.name}/AWSLogs/${local.aws_account_id}/*"]
  }
}

module "s3_bucket" {
  source             = "../../modules/simple"
  name               = "${local.name}"                                      # Can't do random name due to interpolation error
  versioning_enabled = true
  custom_policy      = "${data.aws_iam_policy_document.custom_policy.json}"
  force_destroy      = "true"
}
