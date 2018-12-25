# Bucket Policy
data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "bucket_custom_policy" {
  statement {
    sid     = "AWSS3GetBucketAcl"
    actions = ["s3:GetBucketAcl"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${format("%s-simple-bucket-with-encryption-example", data.aws_caller_identity.current.account_id)}"]
  }

  statement {
    sid     = "AWSS3PutObject"
    actions = ["s3:PutObject"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${format("%s-simple-bucket-with-encryption-example", data.aws_caller_identity.current.account_id)}/*"]
  }
}
