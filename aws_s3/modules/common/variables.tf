# Default variables for the s3 module
variable "name" {
  description = "Name of the S3 bucket; default = {name_prefix}-{name_suffix}"
  default     = ""
}

variable "name_prefix" {
  description = "Prefix of the S3 bucket; default = {aws_account_id}"
  default     = ""
}

variable "name_suffix" {
  description = "Suffix of the S3 bucket; default = {namespace}"
  default     = ""
}

variable "acl" {
  description = "The canned ACL to apply."
  default     = "private"
}

variable "custom_policy" {
  description = "IAM policy for the S3 bucket"
  default     = ""
}

variable "sse_algorithm" {
  description = "Encryption type to apply to the S3 bucket"
  default     = "AES256"
}

variable "valid_encryption_types" {
  description = "S3 bucket valid encryption types"
  type        = "map"

  default = {
    "AES256"  = "AES256"
    "aes256"  = "AES256"
    "AWS:KMS" = "aws:kms"
    "aws:kms" = "aws:kms"
  }
}

variable "kms_master_key_id" {
  description = "ARN referencing the KMS key if encryption type is aws:kms; ignored if not using aws:kms encryption"
  default     = ""
}

variable "roles" {
  description = "List of roles to assign to the bucket"
  type        = "map"

  default = {
    bucket_read_only    = false
    bucket_read_write   = false
    bucket_full_control = false
  }
}

variable "global_roles" {
  description = "List of global roles to assign to the bucket"
  type        = "map"

  default = {
    global_read_only    = []
    global_read_write   = []
    global_full_control = []
  }
}

variable "force_destroy" {
  description = "Force destroy S3 bucket when terraform deletes the bucket"
  default     = "false"
}

variable "versioning_enabled" {
  description = "Enable versioning on the S3 bucket"
  default     = false
}

variable "S3ssl_enforced" {
  description = "Enforce SSL data transfer on the S3 bucket"
  default     = true
}

variable "S3ReadOnly_role_names" {
  description = "Role names for S3 Read Only policies"
  type        = "list"
  default     = []
}

variable "AllReadOnly_role_name" {
  description = "Role name for All Read Only policies"
  default     = ""
}

variable "tags" {
  description = "Map of tags to apply to this single s3 module"
  type        = "map"
  default     = {}
}

variable "namespace" {
  description = "Name space for this terraform run"
  default     = ""
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}
