/*
* Define global vars and any other setup elements.
*/

variable "aws_account" {}

variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-2"
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
  default     = "common_tech"
}

variable "type_of_service" {
  description = "Type of endpoint interface service. 'aws' or 'custom'"
  default     = "aws"
}

variable "endpoint_service_name" {
  description = "Endpoint interface service name. if aws, one of 'kms','ec2','ec2messages','elasticloadbalancing','kinesis-streams','servicecatalog','sns' and 'ssm' OR Custom service name"
}

variable "private_dns_enabled" {
  description = "Whether or not to associate a private hosted zone with the specified VPC"
  default     = false
}

variable "internet_gateway_enabled" {
  description = "Indicates whether the VPC requires Gateway for internet. When set to 'true', public subnets will be created"
  default     = false
}

variable "enable_dynamodb_endpoint" {
  description = "Indicates whether to provision an DynamoDB endpoint to the VPC"
  default     = false
}

variable "enable_s3_endpoint" {
  description = "Indicates whether to provision an S3 endpoint to the VPC"
  default     = false
}
