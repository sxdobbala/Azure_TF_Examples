variable "aws_region" {
  description = "The AWS region to apply resources to."
  default     = "us-east-1"
}

variable "lambda_zip_bucket" {
  description = "The S3 Bucket that contains the zip of the Lambda Function implementation."
}

variable "lambda_zip_path" {
  description = "The path in the S3 Bucket that contains the zip of the Lambda Function implementation."
}

variable "lambda_topic_name" {
  description = "The SNS Topic name that is created for the Lambda Function."
}

variable "namespace" {
  description = "The namespace for the Lambda function."
  default     = ""
}

variable "env_loglevel" {
  description = "The LOGLEVEL environment variable to be set for the Lambda Function."
}

variable "env_assume_role_arn" {
  description = "The ASSUME_ROLE_ARN environment variable to be set for the Lambda Function."
}

variable "env_lambda_arn_list" {
  description = "The LAMBDA_ARN_LIST environment variable to be set for the Lambda Function."
}

variable "env_sns_topic_arn" {
  description = "The SNS_TOPIC_ARN environment variable to be set for the Lambda Function."
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters."
  type        = "map"
  default     = {}
}

variable "lambda_tags" {
  description = "Map of tags to apply to the Lambda Function."
  type        = "map"
  default     = {}
}

variable "schedule_expression" {
  description = "Event scheduler's scheduling expression in the form of cron(0 20 * * ? *) or rate(5 minutes)"
  default     = "rate(1 hour)"
}
