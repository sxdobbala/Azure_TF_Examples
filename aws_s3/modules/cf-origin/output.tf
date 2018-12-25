output "cf_origin_bucket_domain_name" {
  description = "The domain name of the static content bucket"
  value       = "${aws_s3_bucket.bucket.bucket_domain_name}"
}
