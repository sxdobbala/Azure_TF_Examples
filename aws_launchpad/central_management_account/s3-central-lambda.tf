data "aws_iam_policy_document" "bucket_lambda_policy" {
  statement {
    sid     = "AWSLambdaAccountsAclCheck"
    actions = ["s3:GetBucketAcl"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${var.lambda_client_accounts}"]
    }

    resources = ["arn:aws:s3:::${var.lambda_s3_bucket}"]
  }

  statement {
    sid     = "AWSLambdaAccountsGetObject"
    actions = ["s3:GetObject"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${var.lambda_client_accounts}"]
    }

    resources = ["arn:aws:s3:::${var.lambda_s3_bucket}/*"]
  }

  statement {
    sid     = "AWSLambdaAccountsWriteObject"
    actions = ["s3:PutObject"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${var.central_account_id}"]
    }

    resources = ["arn:aws:s3:::${var.lambda_s3_bucket}/*"]
  }
}

module "s3-central-lambda" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module/tflife?ref=v1.2.1"
  bucket_name               = "${var.lambda_s3_bucket}"
  bucket_custom_policy      = "${data.aws_iam_policy_document.bucket_lambda_policy.json}"
  bucket_versioning_enabled = true
  global_tags               = "${var.global_tags}"
  s3_tags                   = "${var.s3_tags}"
}

resource "aws_s3_bucket_object" "upload_lambda_invoker" {
  bucket                 = "${module.s3-central-lambda.bucket_id}"
  key                    = "lambda_invoker.zip"
  source                 = "lambda_functions/lambda_invoker.zip"
  server_side_encryption = "AES256"
}
