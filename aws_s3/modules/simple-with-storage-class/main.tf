# Create a bucket using simple feature_flag
resource "aws_s3_bucket" "bucket" {
  bucket        = "${local.name}"
  acl           = "${var.acl}"
  force_destroy = "${var.force_destroy}"

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${local.kms_master_key_id}"
        sse_algorithm     = "${local.sse_algorithm}"
      }
    }
  }

  versioning {
    enabled = "${var.versioning_enabled}"
  }

  lifecycle_rule {
    id      = "${var.id}"
    enabled = "${var.enabled}"
    prefix  = "${var.prefix}"

    transition {
      days          = "${var.days_initial_storage_class}"
      storage_class = "${var.initial_storage_class}"
    }

    transition {
      days          = "${var.days_final_storage_class}"
      storage_class = "${var.final_storage_class}"
    }

   noncurrent_version_transition {
      days          = "${var.days_noncurrent_storage_class}"
      storage_class = "${var.noncurrent_storage_class}"
    }

    expiration {
      days = "${var.expiration_days}"
    }
  }

  tags = "${merge(var.global_tags, var.tags, map("s3_feature_flags", "simple"), map("s3_encryption_type", local.sse_algorithm), local.version_tag)}"
}

locals {
  enable_cf_origin_policy       = false
  cf_origin_access_identity_arn = ""
}
