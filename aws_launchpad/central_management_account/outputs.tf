# Outputs for Backend

output "backend_bucket_id" {
  value = "${module.bootstrap_aws.tfstate_s3_bucket_id}"
}

output "backend_table_id" {
  value = "${module.bootstrap_aws.tflock_dynamodb_table_id}"
}

# Outputs for Config S3

output "cloudtrail_bucket_id" {
  value = "${module.s3-central-cloudtrail.bucket_id}"
}

output "cloudtrail_bucket_arn" {
  value = "${module.s3-central-cloudtrail.bucket_arn}"
}

# Outputs for Config S3

output "config_bucket_id" {
  value = "${module.s3-central-config.bucket_id}"
}

output "config_bucket_arn" {
  value = "${module.s3-central-config.bucket_arn}"
}

output "config_queue_id" {
  value = "${aws_sqs_queue.config_queue.id}"
}

output "config_queue_arn" {
  value = "${aws_sqs_queue.config_queue.arn}"
}

output "deadletter_queue_id" {
  value = "${aws_sqs_queue.deadletter_queue.id}"
}

output "deadletter_queue_arn" {
  value = "${aws_sqs_queue.deadletter_queue.arn}"
}

# Outputs for Lambda S3

output "lambda_bucket_id" {
  value = "${module.s3-central-lambda.bucket_id}"
}

output "lambda_bucket_arn" {
  value = "${module.s3-central-lambda.bucket_arn}"
}

# Ouptuts for Lambda Audit EC2

output "lambda_audit_ec2_function_name" {
  value = "${module.lambda_audit_ec2.lambda_function_name}"
}

output "lambda_audit_ec2_function_arn" {
  value = "${module.lambda_audit_ec2.lambda_arn}"
}

output "launchpad_lambda_topic_id" {
  value = "${aws_sns_topic.launchpad_lambda_topic.id}"
}

output "launchpad_lambda_topic_arn" {
  value = "${aws_sns_topic.launchpad_lambda_topic.arn}"
}

# Outputs for Fire Eye

output "fire_eye_forwarder_role_arn" {
  value = "${module.fire_eye.cloudtrail_forwarder_role_arn}"
}

# Outputs for Central logging Firehose
output "firehose_stream_arn" {
  value = "${module.central_logging_firehose_to_splunk.arn}"
}

output "firehose_destination" {
  value = "${module.central_logging_firehose_to_splunk.destination}"
}

output "firehose_destination_id" {
  value = "${module.central_logging_firehose_to_splunk.destination_id}"
}

output "firehose_name" {
  value = "${module.central_logging_firehose_to_splunk.name}"
}

# Outputs for Data Stream

output "data_stream_id" {
  value = "${module.aws_kinesis_data_stream.id}"
}

output "data_stream_name" {
  value = "${module.aws_kinesis_data_stream.name}"
}

output "data_stream_shard_count" {
  value = "${module.aws_kinesis_data_stream.shard_count}"
}

output "data_stream_arn" {
  value = "${module.aws_kinesis_data_stream.arn}"
}

# Outputs for Lambda Logs Processor
output "lambda_name" {
  value = "${module.lambda-with-s3-trigger.function_name}"
}

output "lambda_arn" {
  value = "${module.lambda-with-s3-trigger.arn}"
}

output "source_s3_bucket_id" {
  value = "${module.central_logging_firehose_to_splunk.bucket_id}"
}

output "destination_s3_bucket_id" {
  value = "${module.destination-s3-bucket.id}"
}

output "log_group_arn" {
  value = "${module.log_group.arn}"
}

output "log_group_name" {
  value = "${module.log_group.name}"
}

output "sqs_id" {
  value = "${module.sqs.id}"
}

output "sqs_arn" {
  value = "${module.sqs.arn}"
}

# Outputs for KMS 
output "kms_id" {
  value = "${module.create-kms-key.id}"
}

output "kms_arn" {
  value = "${module.create-kms-key.arn}"
}

output "kms_alias_name" {
  value = "${module.create-kms-key.alias_name}"
}

output "kms_alias_arn" {
  value = "${module.create-kms-key.alias_arn}"
}
