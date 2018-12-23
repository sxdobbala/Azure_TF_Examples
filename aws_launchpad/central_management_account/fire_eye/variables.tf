variable "cloudtrail_bucket" {
  description = "Bucket containing CloudTrail logs"
}

variable "cloudtrail_bucket_id" {
  description = "Bucket containing CloudTrail logs"
}

variable "external_id" {
  description = "Unique External ID provided by FireEye"
  default     = ""
}

variable "log_file_prefix" {
  description = "Log file prefix formatted as <prefix>/AWSLogs/*"
  default     = ""
}
