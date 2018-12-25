# Create policies for S3 bucket

data "aws_iam_policy_document" "S3_ReadOnly" {
  statement {
    effect = "Allow"

    actions = ["s3:GetBucketAcl",
      "s3:ListObjects",
      "s3:ListBucket",
      "s3:GetObject",
      "s3:ListAllMyBuckets",
    ]

    resources = ["arn:aws:s3:::${local.name}",
      "arn:aws:s3:::${local.name}/*",
    ]
  }
}

data "aws_iam_policy_document" "S3_ReadWrite" {
  statement {
    effect = "Allow"

    actions = ["s3:GetBucketAcl",
      "s3:ListObjects",
      "s3:ListBucket",
      "s3:ListAllMyBuckets",
      "s3:PutObject",
      "s3:GetObject",
    ]

    resources = ["arn:aws:s3:::${local.name}",
      "arn:aws:s3:::${local.name}/*",
    ]
  }
}

data "aws_iam_policy_document" "S3_FullControl" {
  statement {
    effect  = "Allow"
    actions = ["s3:*"]

    resources = ["arn:aws:s3:::${local.name}",
      "arn:aws:s3:::${local.name}/*",
    ]
  }

  statement {
    effect = "Deny"

    actions = ["s3:CreateBucket",
      "s3:DeleteBucket",
      "s3:DeleteBucketPolicy",
      "s3:DeleteBucketWebsite",
    ]

    resources = ["arn:aws:s3:::${local.name}",
      "arn:aws:s3:::${local.name}/*",
    ]
  }
}

# When creating a role via terraform, the "assume_role_policy" parameter is required.
# By default we set the assume_role_policy to the "root" account
data "aws_iam_policy_document" "default_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }
  }
}

## For CloudFront Origin access control
data "aws_iam_policy_document" "bucket_enforce_cf_origin_only_access" {
  statement {
    actions   = ["s3:GetObject"]
    resources = ["arn:aws:s3:::${local.name}/*"]

    principals {
      type        = "AWS"
      identifiers = ["${local.cf_origin_access_identity_arn}"]
    }
  }

  statement {
    actions   = ["s3:ListBucket"]
    resources = ["arn:aws:s3:::${local.name}"]

    principals {
      type        = "AWS"
      identifiers = ["${local.cf_origin_access_identity_arn}"]
    }
  }
}
