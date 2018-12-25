# Create a bucket using tflife feature_flag
resource "aws_s3_bucket" "bucket" {
  bucket        = "${local.name}"
  acl           = "${var.acl}"
  force_destroy = "${var.force_destroy}"

  versioning {
    enabled = "${var.versioning_enabled}"
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${local.kms_master_key_id}"
        sse_algorithm     = "${local.sse_algorithm}"
      }
    }
  }

  lifecycle {
    prevent_destroy = true
  }

  tags = "${merge(var.global_tags, var.tags, map("s3_feature_flags", "tflife"), map("s3_encryption_type", local.sse_algorithm), local.version_tag)}"
}

locals {
  enable_cf_origin_policy       = false
  cf_origin_access_identity_arn = ""
}
