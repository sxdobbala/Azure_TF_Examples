# Policy to enforce SSL data transfer
data "aws_iam_policy_document" "bucket_enforce_ssl" {
  statement {
    sid     = "DenyInsecureCommunications"
    actions = ["s3:*"]
    effect  = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    resources = ["arn:aws:s3:::${local.name}/*"]

    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }
}
