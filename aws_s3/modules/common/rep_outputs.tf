output "rep_bucket_id" {
  value = "${aws_s3_bucket.rep_bucket.id}"
}

output "rep_bucket_arn" {
  value = "${aws_s3_bucket.rep_bucket.arn}"
}

output "rep_bucket_key" {
  value = "${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_key}"
}
