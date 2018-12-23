module "fire_eye" {
  source               = "./fire_eye"
  cloudtrail_bucket    = "${var.bucket_name}"
  cloudtrail_bucket_id = "${module.s3-central-cloudtrail.bucket_id}"
  external_id          = "55cc8aecbc"
}
