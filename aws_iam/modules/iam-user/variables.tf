# Default variables for the iam module
variable "name" {
  description = "Name of the user"
  default     = ""
}

variable "force_destroy" {
  description = "Force destroy user even if it has non-Terraform-managed IAM keys, login profile, MFA devices"
  default     = "false"
}

variable "path" {
  description = "Path in which to create the user"
  default     = "/"
}

variable "create_iam_access_key" {
  description = "If required, generate API keys"
  default     = ""
}

variable "enforce_mfa" {
  description = "Enforces multi factor authentication (MFA) when the user logs in via AWS console"
  default     = true
}

variable "namespace" {
  description = "Name space for this terraform run"
  default     = ""
}

variable "aws_region" {
  default = "us-east-1"
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}
