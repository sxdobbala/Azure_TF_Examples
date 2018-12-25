data "aws_iam_policy_document" "managed-policy" {
  statement {
    effect = "Allow"

    actions = [
      "s3:GetBucketAcl",
      "s3:ListObjects",
      "s3:ListBucket",
      "s3:ListAllMyBuckets",
    ]

    resources = ["*"]
  }
}
