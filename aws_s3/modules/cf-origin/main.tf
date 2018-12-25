locals {
 enable_cf_origin_policy = true
 cf_origin_access_identity_arn = "${var.cf_origin_access_identity_arn}" 
}

# Create a bucket using cf-origin feature_flag

resource "aws_s3_bucket" "bucket" {
  bucket        = "${local.name}"
  acl           = "private"
  force_destroy = "${var.force_destroy}"

  cors_rule {
    allowed_headers = "${var.cf_origin_cors_allowed_headers}"
    allowed_methods = "${var.cf_origin_cors_allowed_methods}"
    allowed_origins = "${var.cf_origin_cors_allowed_origins}"
    expose_headers  = "${var.cf_origin_cors_expose_headers}"
    max_age_seconds = "${var.cf_origin_cors_max_age_seconds}"
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${local.kms_master_key_id}"
        sse_algorithm     = "${local.sse_algorithm}"
      }
    }
  }

  logging {
    target_bucket = "${var.cf_origin_logging_bucket_name}"
    target_prefix = "${var.cf_origin_logging_bucket_target_prefix}"
  }

  versioning {
    enabled = "${var.versioning_enabled}"
  }

  force_destroy = "${var.cf_origin_force_destroy}"

  tags = "${merge(var.global_tags, var.tags, map("s3_feature_flags", "cf-origin"), map("s3_encryption_type", local.sse_algorithm), local.version_tag)}"
}
