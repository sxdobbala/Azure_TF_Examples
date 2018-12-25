# Bucket Policy

#
# Here we want to construct a bucket name using the same logic as we use internally when creating a bucket.
# why? to avoid a circular dependency when we would normally try to use the bucket_arn - this arn can't be computed
# because this policy would be dependent on it, and the count could not be generated properly.
# the construct it: <aws account_id>-<name_suffix>-<namespace>
#

locals {
  aws_account_id = "${data.aws_caller_identity.current.account_id}"

  name = "arn:aws:s3:::${local.aws_account_id}-cf-origin-bucket-example"
}

data "aws_iam_policy_document" "custom_policy" {
  statement {
    sid     = "AWSS3Example"
    actions = ["s3:*"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${local.aws_account_id}"]
    }

    resources = ["${local.name}"]
  }
}
