variable "cloudtrail_name" {
  description = "Name of the Cloudtrail"
  default     = ""
}

variable "cloudtrail_s3_bucket" {
  description = "The S3 bucket to store the cloudtrail logs"
  default     = ""
}

variable "config_name" {
  description = "Name of the Config"
  default     = ""
}

variable "config_s3_bucket" {
  description = "The S3 bucket to store the config logs"
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

variable "cloudtrail_tags" {
  description = "Map of tags to apply to the cloudtrail"
  type        = "map"
  default     = {}
}

variable "config_tags" {
  description = "Map of tags to apply to the config"
  type        = "map"
  default     = {}
}

variable "cloudwatch_tags" {
  description = "Map of tags to apply to the cloudwatch module"
  type        = "map"
  default     = {}
}

variable "eis_central_security_id" {
  description = "The central eis security AWS account id"
  default     = ""
}

variable "aws_region" {
  description = "AWS region"
  default     = "us-east-1"
}

variable "cw_log_group_name" {
  description = "name of the cloudwatch vpc flow log group"
  default     = "vpc-flow-logs"
}

variable "cloudtrail_log_group_name" {
  description = "name of the log group for cloudtrail logs"
  default     = "launchpad-cloudtrail-log-group"
}

variable "log_destination_name" {
  description = "cloudwatch log destination name"
}

variable "subscription_filter_pattern" {
  description = "filter pattern for cloudwatch logs for subscribing to a filtered stream of log events"
  default     = ""
}

variable "role_arn" {
  description = "ARN of the IAM role that has permissions to send logs to the destination resource."
  default     = ""
}

variable "central_logging_account" {
  description = "Central logging account details"
  default     = ""
}

# "env" variables below are for single AWS custom config Lambda function
variable "env_loglevel" {
  description = "Log level of information for logging from the Lambda function"
  default     = "INFO"
}

variable "env_config_put_test_flag" {
  description = "Environment variable flag used in SG lambda to switch to test mode"
  default     = "FALSE"
}

variable "env_rangelimit" {
  description = "Environment variable for maximum allowed port range"
  default     = "65535"
}
variable "launchpad_account" {
  description = "launchpad run account"
  default     = ""
}
