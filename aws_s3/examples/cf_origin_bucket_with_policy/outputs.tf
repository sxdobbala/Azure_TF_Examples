output "id" {
  value = "${module.cf-origin-bucket-with-policy.id}"
}

output "arn" {
  value = "${module.cf-origin-bucket-with-policy.arn}"
}

output "log_bucket_id" {
  value = "${module.log_bucket.id}"
}

output "log_bucket_arn" {
  value = "${module.log_bucket.arn}"
}

output "domain_name" {
  value = "${module.cf-origin-bucket-with-policy.cf_origin_bucket_domain_name}"
}
