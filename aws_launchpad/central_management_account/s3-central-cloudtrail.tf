data "aws_iam_policy_document" "bucket_custom_policy" {
  statement {
    sid     = "AWSCloudTrailAclCheck"
    actions = ["s3:GetBucketAcl"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logs.amazonaws.com", "cloudtrail.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${var.bucket_name}"]
  }

  statement {
    sid     = "AWSCloudTrailWrite"
    actions = ["s3:PutObject"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logs.amazonaws.com", "cloudtrail.amazonaws.com"]
    }

    resources = "${var.cloudtrail_client_accounts}"
  }

  statement {
    sid     = "AWSCloudTrailWriteCondition"
    actions = ["s3:PutObject"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logs.amazonaws.com", "cloudtrail.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${var.bucket_name}/*"]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = ["bucket-owner-read"]
    }
  }

  statement {
    sid     = "FEYEReadOnly"
    actions = ["s3:GetObject"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${module.fire_eye.cloudtrail_forwarder_role_arn}"]
    }

    resources = ["arn:aws:s3:::${var.bucket_name}/*"]
  }
}

module "s3-central-cloudtrail" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module/tflife?ref=v1.2.1"
  bucket_name               = "${var.bucket_name}"
  bucket_custom_policy      = "${data.aws_iam_policy_document.bucket_custom_policy.json}"
  bucket_versioning_enabled = true
  global_tags               = "${var.global_tags}"
  s3_tags                   = "${var.s3_tags}"
}
