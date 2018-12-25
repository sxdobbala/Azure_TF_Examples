output "id" {
  value = "${module.bucket_with_replication_and_logging.id}"
}

output "arn" {
  value = "${module.bucket_with_replication_and_logging.arn}"
}

output "log_bucket_id" {
  value = "${module.bucket_with_replication_and_logging.log_bucket_id}"
}

output "log_bucket_arn" {
  value = "${module.bucket_with_replication_and_logging.log_bucket_arn}"
}

output "log_bucket_key" {
  value = "${module.bucket_with_replication_and_logging.log_bucket_key}"
}

output "rep_bucket_id" {
  value = "${module.bucket_with_replication_and_logging.rep_bucket_id}"
}

output "rep_bucket_arn" {
  value = "${module.bucket_with_replication_and_logging.rep_bucket_arn}"
}

output "rep_bucket_key" {
  value = "${module.bucket_with_replication_and_logging.rep_bucket_key}"
}
