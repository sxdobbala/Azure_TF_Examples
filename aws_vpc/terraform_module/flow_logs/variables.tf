variable "aws_region" {
  description = "AWS region to create resources"
}

variable "vpc_id" {
  description = "VPC id to associate Flow logs. VPC ID must be provided."
}

variable "traffic_type" {
  description = "The type of traffic to capture. Valid values: ACCEPT,REJECT, ALL"
  default     = "ALL"
}

variable "flow_log_group_name" {
  description = "Cloud watch log group name for flow logs"
  default     = "vpc-flow-logs"
}

variable "flow_log_group_retention_in_days" {
  description = "Number of days to retain the logs"
  default     = 7
}

variable "tag_name_identifier" {
  description = "tag name identifier for the aws_base"
}
