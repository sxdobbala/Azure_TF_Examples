data "aws_iam_policy_document" "bucket_policy" {
  statement {
    sid     = "AWSS3Example"
    actions = ["s3:*"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }

    resources = ["arn:aws:s3:::${var.s3_bucket_name_prefix}-${data.aws_caller_identity.current.account_id}"]
  }
}

module "s3" {
  source                   = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module//existing-log?ref=v1.4.0"
  bucket_name              = "${var.s3_bucket_name_prefix}-${data.aws_caller_identity.current.account_id}"
  bucket_custom_policy     = "${data.aws_iam_policy_document.bucket_policy.json}"
  bucket_force_destroy     = true
  log_bucket_name          = "${var.s3_log_bucket_name}"
  log_bucket_custom_policy = ""
  s3_tags                  = "${merge(map("Name", "${local.proxy_name}"),var.global_tags,local.version_tag)}"
}

resource "aws_s3_bucket_object" "proxy_url" {
  bucket                 = "${module.s3.bucket_id}"
  key                    = "squid_proxy.txt"
  content                = "http://${aws_elb.proxy.dns_name}:3128"
  content_type           = "application/text"
  server_side_encryption = "AES256"
  depends_on             = ["aws_elb.proxy"]
}
