# Common inputs used across modules
aws_region = "us-east-1"
prefix = ""
enabled = "true"
id = "lifecycle-rule"
initial_storage_class = "INTELLIGENT_TIERING"
final_storage_class = "GLACIER"
days_initial_storage_class = "30"
days_final_storage_class = "60"
expiration_days = "365"
noncurrent_storage_class = "GLACIER"
days_noncurrent_storage_class = "30"