variable "log_bucket_name" {
  description = "S3 target bucket to write logging files"
  default     = ""
}

variable "log_bucket_name_base" {
  description = "Base name to use for the log bucket name; default = {S3-source-bucket-name}"
  default     = ""
}

variable "log_bucket_name_suffix" {
  description = "Suffix to use for the log bucket name; default = logs"
  default     = ""
}

variable "log_bucket_key" {
  description = "S3 target prefix of the S3 logging bucket"
  default     = ""
}

variable "log_bucket_custom_policy" {
  description = "IAM custom policy for the S3 log bucket"
  default     = ""
}

variable "log_bucket_roles" {
  description = "List of roles to assign to the log bucket"
  type        = "map"

  default = {
    bucket_read_only    = false
    bucket_read_write   = false
    bucket_full_control = false
  }
}

variable "global_log_bucket_roles" {
  description = "List of global roles to assign to the log bucket"
  type        = "map"

  default = {
    global_read_only    = []
    global_read_write   = []
    global_full_control = []
  }
}

variable "log_bucket_force_destroy" {
  description = "Force destroy S3 bucket when terraform deletes the log bucket"
  default     = "false"
}

variable "log_bucket_versioning_enabled" {
  description = "Enable versioning on the S3 log bucket"
  default     = false
}
