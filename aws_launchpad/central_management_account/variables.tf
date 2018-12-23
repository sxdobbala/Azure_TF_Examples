variable "central_account_id" {
  description = "The Central AWS Account ID"
}

variable "bucket_name" {
  description = "Name for the bucket"
  default     = ""
}

variable "bootstrap_launchpad_bucket" {
  description = "Name of the launchpad backend bucket"
  default     = ""
}

variable "bootstrap_launchpad_table" {
  description = "Name of the launchpad backend table"
  default     = ""
}

variable "config_s3_bucket" {
  description = "The S3 bucket to store the config logs"
  default     = ""
}

variable "lambda_s3_bucket" {
  description = "The S3 bucket to store the lambda zips"
  default     = ""
}

variable "name_space" {
  description = "Name space for this terraform run"
  default     = ""
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}

variable "bootstrap_launchpad_tags" {
  description = "Map of tags to apply to this bootstrap modules"
  type        = "map"
  default     = {}
}

variable "bootstrap_tags" {
  description = "Map of tags to apply to the bootstrap module"
  type        = "map"
  default     = {}
}

variable "s3_tags" {
  description = "Map of tags to apply to this single s3 module"
  type        = "map"
  default     = {}
}

variable "lambda_audit_ec2_tags" {
  description = "Map of tags to apply to the lambda_audit_ec2 module."
  type        = "map"
  default     = {}
}

variable "cloudtrail_client_accounts" {
  description = "List of AWS accounts to be allowed to log to the central Cloudtrail buckets, ex: arn:aws:s3:::ctBucketName/AWSLogs/123456789012"
  type        = "list"
}

variable "launchpad_backend_accounts" {
  description = "List of AWS accounts to be use the backend buckets, ex: arn:aws:s3:::lpBucketName/bootstrap-launchpad-123456789012/*"
  type        = "list"
}

variable "launchpad_backend_principals" {
  description = "List of AWS accounts to use the backend buckets, ex: arn:aws:iam::123456789012:role/OrganizationAccountAccessRole"
  type        = "list"
}

variable "config_client_accounts" {
  description = "List of AWS accounts to be allowed to log to the central Config buckets, ex: arn:aws:s3:::configBucketName/AWSLogs/123456789012"
  type        = "list"
}

variable "lambda_client_accounts" {
  description = "List of AWS accounts to be allowed to access the lambda account, ex: 123456789012"
  type        = "list"
}

variable aws_region {
  description = "AWS region to deploy the DynamoDB table"
  default     = "us-east-1"
}

variable "central_logging_account" {}

variable "tags" {
  description = "Map of tags to apply to the kms module"
  type        = "map"
  default     = {}
}

variable "namespace" {
  description = "Namespace for this terraform run"
  default     = ""
}

variable "name" {
  description = "Name of the firehose delivery stream"
  default     = "default"
}

variable "data_stream_name" {
  description = "Name of the kinesis data stream"
  default     = "default"
}

variable "shard_count" {
  description = "The number of shards that the stream will use."
  default     = ""
}

variable "retention_period" {
  description = "Length of time data records are accessible after they are added to the stream."
}

variable "destination" {
  description = "Destination type for Firehose delivery Stream"
  default     = "splunk"
}

variable "splunk_conf_hec_endpoint" {
  description = "(Required) The HTTP Event Collector (HEC) endpoint to which Kinesis Firehose sends your data"
  default     = ""
}

variable "hec_token" {
  description = "The GUID that you obtain from your Splunk cluster when you create a new HEC endpoint"
  default     = ""
}

variable "hec_acknowledgment_timeout" {
  description = " (Optional) Time in seconds (between 180 and 600), that Kinesis Firehose waits to receive an acknowledgment from Splunk after it sends it data"
  default     = "180"
}

variable "hec_endpoint_type" {
  description = "(Optional) The HEC endpoint type. Valid values are Raw or Event. The default value is Raw"
  default     = "Raw"
}

variable "s3_backup_mode" {
  description = " (Optional) Defines how documents should be delivered to Amazon S3. Valid values are FailedEventsOnly and AllEvents"
  default     = "FailedEventsOnly"
}

variable "s3_conf_buffer_interval" {
  description = "Backup S3 bucket buffer interval"
  default     = "300"
}

variable "s3_conf_buffer_size" {
  description = "Backup S3 bucket buffer size"
  default     = "15"
}

variable "s3_conf_compression_format" {
  description = "Backup S3 bucket log data compression format"
  default     = "GZIP"
}

variable "lambda_processor_function_name" {
  description = "Name of the function for the firehose lambda processor"
  default     = ""
}

variable "firehose_lambda_log_group" {
  description = "Log group to hold the logs from the firehose lambda functions"
  default     = "default"
}

variable "firehose_log_stream" {
  description = "log group to hold logs from firehose"
  default     = "default"
}

variable "lambda_function_timeout" {
  description = "Timeout for the lambda function"
  default     = "240"
}

variable "lambda_function_filename" {
  description = "Filename for the lambda function"
  default     = ""
}

variable "lambda_runtime" {
  description = "Runtime of the lambda function"
  default     = "python2.7"
}

variable "log_destination_name" {
  description = "Destination name"
  default     = ""
}

variable "allowed_accounts" {
  description = "Allowed accounts"
  default     = []
}

variable "cw_log_group_retention_in_days" {
  description = "Log retention in days"
  default     = 30
}

variable "versioning_enabled" {
  description = "Versioning for the backup s3 bucket"
  default     = false
}

variable "function_name" {
  description = "Name of the Lambda function"
  default     = ""
}

variable "description" {
  description = "Description of what the Lambda function does"
  default     = ""
}

variable "filename" {
  description = "Filename containing the function source code; must be .zip format"
  default     = ""
}

variable "runtime" {
  description = "Runtime environment for the Lambda function"
  default     = ""
}

variable "memory_size" {
  description = "Amount of memory in MB to use at runtime; in 64MB increments"
  default     = ""
}

variable "timeout" {
  description = "Amount of time (in seconds) to allow the Lambda function to run (maximum 300)"
  default     = ""
}

variable "custom_inline_policy_count" {
  description = "Number of custom policies to apply to the lambda exec role"
}

variable "trigger_count" {
  description = "Number of execution triggers to apply to the lambda function"
}

variable "dead_letter_queue_name" {
  description = "The queue name for the SQS queue. The queue name should be appended with .fifo for a fifo queue."
}

variable "policy_enforced" {
  description = "Boolean Flag to enable queue policy for the SQS queue."
}

variable "redrive_queue" {
  description = "Boolean of the SQS queue for redrive queue."
}

variable "message_retention_seconds" {
  description = "The queue message retention time in seconds for the SQS queue."
}

variable "max_message_size" {
  description = "The queue max message size in bytes for the SQS queue."
}

variable "fifo_queue" {
  description = "Boolean designating a FIFO queue for the SQS queue."
}

variable "log_group_retention_in_days" {
  description = "Number of days log events will be retained in the log group."
}

variable "cmk_alias_name" {
  description = "Alias names of the kms key; can be 1 or more alias for eack kms key"
  default     = []
}
