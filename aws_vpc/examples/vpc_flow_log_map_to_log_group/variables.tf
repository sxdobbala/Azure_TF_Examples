/*
* Define global vars and any other setup elements.
*/

variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "Classless Inter-Domain Routing (CIDR) block for the VPC"
  default     = "10.0.0.0/16"
}

variable "aws_azs" {
  type    = "list"
  default = ["no_value"]
}

variable "tag_name_identifier" {
   description = "tag name identifier for the aws_base"
   default     = "commercial-cloud"
}

variable "internet_gateway_enabled" {
  description = "Indicates whether the VPC requires Gateway for internet. When set to 'true', public subnets will be created"
  default     = true
}

variable "enable_dynamodb_endpoint" {
  description = "Indicates whether to provision an DynamoDB endpoint to the VPC"
  default     = false
}

variable "enable_s3_endpoint" {
  description = "Indicates whether to provision an S3 endpoint to the VPC"
  default     = false
}

variable "cw_log_group_name" {
  description = "name of the cloudwatch log group"
}

variable "subscription_filter_name" {
  description = "name of the subscription filter"
}

variable "subscription_filter_pattern" {
  description = "filter pattern for cloudwatch logs for subscribing to a filtered stream of log events"
}

variable "destination_arn" {
  description = "cloudwatch log destination arn to subscribe to"
}
