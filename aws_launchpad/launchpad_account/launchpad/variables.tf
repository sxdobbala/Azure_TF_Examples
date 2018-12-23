variable "aws_region" {
  description = "AWS region"
  default     = "us-east-1"
}

variable "cloudtrail_name" {
  description = "Name of the Cloudtrail"
  default     = ""
}

variable "cloudtrail_s3_bucket" {
  description = "The S3 bucket to store the cloudtrail logs"
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
