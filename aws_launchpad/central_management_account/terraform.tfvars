# Kinesis data stream configurations
shard_count      = 1
retention_period = 48

# Kinesis Firehose configurations
namespace = "central-logging"
destination = "splunk"
splunk_conf_hec_endpoint = "https://http-inputs-optumscone.splunkcloud.com:443"
hec_token = "A4036873-4A16-4C96-A3BF-E3910688790E"
hec_endpoint_type = "Raw"
hec_acknowledgment_timeout = "180"
s3_backup_mode = "AllEvents"
s3_conf_buffer_interval = "60"
s3_conf_buffer_size = "16"
s3_conf_compression_format = "GZIP"
lambda_processor_function_name = "firehose_lambda_processor"

# Destination configurations
log_destination_name = "awslogs-destination-splunk"
# Lambda configuration
lambda_function_timeout=240
lambda_function_filename="lambda_functions/central_logging_lambda_cloudwatch_log_processor.zip"
lambda_runtime="python2.7"

aws_region = "us-east-1"

# Lambda function configuration to process the firehose logs
function_name = "lambda-logs-processor-function"
description = "Lambda function processes the logs stored in a bucket object and stores them to separate s3 bucket; automatically executed when new log is created in S3 bucket"
filename = "lambda_functions/store_processed_logs_to_s3.py.zip"
custom_inline_policy_count = 1
trigger_count = 1
runtime = "python3.6"
memory_size = "3008"
timeout = "300"
dead_letter_queue_name = "lambda-logs-processor-dead-letter-queue"
policy_enforced           = false
redrive_queue             = false
max_message_size          = 262144
message_retention_seconds = 1209600
fifo_queue                = false

# Lambda log group configuration 
log_group_retention_in_days = 365


# Customer master key for encryption of lambda dead letter queue and log group
cmk_alias_name = ["lambda-logs-processor-log-group-key"]