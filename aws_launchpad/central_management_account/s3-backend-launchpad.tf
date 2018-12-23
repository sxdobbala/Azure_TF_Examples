# Call bootstrap module to set up the backend resources to use 
data "aws_iam_policy_document" "bootstrap_bucket_custom_policy" {
  statement {
    sid     = "BackendListObjects"
    actions = ["s3:ListBucket"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = "${var.launchpad_backend_principals}"
    }

    resources = ["arn:aws:s3:::${var.bootstrap_launchpad_bucket}"]
  }

  statement {
    sid     = "BackendPutObjects"
    actions = ["s3:PutObject", "s3:GetObject"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = "${var.launchpad_backend_principals}"
    }

    resources = ["arn:aws:s3:::${var.bootstrap_launchpad_bucket}/*"]
  }
}

# Apply the bootstrap module
module "bootstrap_aws" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_bootstrap.git//terraform_module?ref=s3-custom-policy"
  bootstrap_tfstate_s3_bucket = "${var.bootstrap_launchpad_bucket}"
  bootstrap_tflock_dynamodb_table = "${var.bootstrap_launchpad_table}"
  bootstrap_bucket_custom_policy = "${data.aws_iam_policy_document.bootstrap_bucket_custom_policy.json}"
  name_space = "${var.name_space}"
  global_tags = "${var.global_tags}"
  bootstrap_tags = "${var.bootstrap_tags}"
  aws_region = "${var.aws_region}"
  }
