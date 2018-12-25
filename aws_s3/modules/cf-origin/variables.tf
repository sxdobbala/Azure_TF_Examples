variable "cf_origin_access_identity_arn" {
  description = "CloudFront origin access identity arn for bucket policy"
}

variable "cf_origin_cors_allowed_headers" {
  description = "Specifies which headers are allowed"
  type        = "list"
  default     = ["*"]
}

variable "cf_origin_cors_allowed_methods" {
  description = "Specifies which methods are allowed. Can be GET, PUT, POST, DELETE or HEAD"
  type        = "list"
  default     = ["GET", "PUT", "POST"]
}

variable "cf_origin_cors_allowed_origins" {
  description = "Specifies which origins are allowed"
  type        = "list"
  default     = ["*"]
}

variable "cf_origin_cors_expose_headers" {
  description = "Specifies expose header in the response"
  type        = "list"
  default     = ["ETag"]
}

variable "cf_origin_cors_max_age_seconds" {
  description = "Specifies time in seconds that browser can cache the response for a preflight request"
  default     = 3000
}

variable "cf_origin_logging_bucket_name" {
  description = "The name of the bucket that will receive the log objects"
}

variable "cf_origin_logging_bucket_target_prefix" {
  description = "The name of the key prefix for log objects"
}

variable "cf_origin_force_destroy" {
  description = "A boolean that indicates all objects should be deleted from the bucket so that the bucket can be destroyed without error"
  default     = false
}
