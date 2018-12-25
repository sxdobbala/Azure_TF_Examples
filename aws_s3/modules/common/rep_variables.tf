variable "rep_bucket_name" {
  description = "S3 target bucket to write replication files"
  default     = ""
}

variable "rep_bucket_name_base" {
  description = "Base name to use for the replication bucket name; default = {S3-source-bucket-name}"
  default     = ""
}

variable "rep_bucket_name_suffix" {
  description = "Suffix to use for the replication bucket name; default = replication"
  default     = ""
}
variable "rep_bucket_acl" {
  description = "The canned ACL to apply."
  default     = "private"
}

variable "rep_bucket_key" {
  description = "S3 target prefix of the S3 replication bucket"
  default     = ""
}

variable "rep_bucket_custom_policy" {
  description = "IAM custom policy for the S3 replication bucket"
  default     = ""
}

variable "rep_bucket_roles" {
  description = "List of roles to assign to the replication bucket"
  type        = "map"

  default = {
    bucket_read_only    = false
    bucket_read_write   = false
    bucket_full_control = false
  }
}

variable "global_rep_bucket_roles" {
  description = "List of global roles to assign to the replication bucket"
  type        = "map"

  default = {
    global_read_only    = []
    global_read_write   = []
    global_full_control = []
  }
}
variable "rep_bucket_force_destroy" {
  description = "Force destroy S3 bucket when terraform deletes the replication bucket"
  default     = "false"
}
