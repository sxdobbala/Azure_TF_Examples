output "log_bucket_id" {
  value = "${aws_s3_bucket.log_bucket.id}"
}

output "log_bucket_arn" {
  value = "${aws_s3_bucket.log_bucket.arn}"
}

output "log_bucket_key" {
  value = "${data.template_file.aws_s3_log_bucket.vars.log_bucket_key}"
}
