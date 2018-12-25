# Create a bucket using rep bucket feature_flags
resource "aws_s3_bucket" "bucket" {
  bucket        = "${local.name}"
  acl           = "${var.acl}"
  force_destroy = "${var.force_destroy}"

  versioning {
    enabled = "true"
  }

  replication_configuration {
    role = "${aws_iam_role.replication.arn}"

    rules {
      id     = "${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}-rule"
      prefix = ""
      status = "Enabled"

      destination {
        bucket = "${aws_s3_bucket.rep_bucket.arn}"
      }
    }
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${local.kms_master_key_id}"
        sse_algorithm     = "${local.sse_algorithm}"
      }
    }
  }

  tags = "${merge(var.global_tags, var.tags, map("s3_feature_flags", "rep"), map("s3_encryption_type", local.sse_algorithm), local.version_tag)}"
}

resource "aws_s3_bucket" "rep_bucket" {
  bucket        = "${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
  acl           = "${data.template_file.aws_s3_rep_bucket.vars.rep_acl}"
  force_destroy = "${data.template_file.aws_s3_rep_bucket.vars.rep_force_destroy}"
  provider      = "aws.replication"

  versioning {
    enabled = "true"
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${local.kms_master_key_id}"
        sse_algorithm     = "${local.sse_algorithm}"
      }
    }
  }

  tags = "${merge(var.global_tags, var.tags, map("s3_feature_flags", "rep"), map("s3_encryption_type", local.sse_algorithm), local.version_tag)}"
}

data "aws_iam_policy_document" "replication_role" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["s3.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "replication" {
  assume_role_policy = "${data.aws_iam_policy_document.replication_role.json}"
}

data "aws_iam_policy_document" "replication_policy" {
  statement {
    effect = "Allow"

    actions = [
      "s3:GetReplicationConfiguration",
      "s3:ListBucket",
    ]

    resources = ["${aws_s3_bucket.bucket.arn}"]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:GetObjectVersion",
      "s3:GetObjectVersionAcl",
    ]

    resources = ["${aws_s3_bucket.bucket.arn}/*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:ReplicateObject",
      "s3:ReplicateDelete",
    ]

    resources = ["${aws_s3_bucket.rep_bucket.arn}/*"]
  }
}

resource "aws_iam_policy" "replication" {
  policy = "${data.aws_iam_policy_document.replication_policy.json}"
}

resource "aws_iam_policy_attachment" "replication" {
  name       = "tf-iam-role-attachment-replication-12345"
  roles      = ["${aws_iam_role.replication.name}"]
  policy_arn = "${aws_iam_policy.replication.arn}"
}

locals {
  enable_cf_origin_policy       = false
  cf_origin_access_identity_arn = ""
}
