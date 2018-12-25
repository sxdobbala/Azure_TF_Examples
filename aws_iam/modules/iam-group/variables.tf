# Default variables for the iam module
variable "name" {
  description = "Name of the IAM group"
  default     = ""
}

variable "path" {
  description = "The path to the group"
  default     = "/"
}

variable "custom_inline_policy_count" {
  description = "Count of inline policies to attach to this group"
  default     = 0
}

variable "custom_inline_policy" {
  description = "List of inline policies to attach to this group; group_inline_name, group_inline_policy_statement"
  default     = []
}

variable "custom_managed_policy_count" {
  description = "Count of managed policies to attach to this group"
  default     = 0
}

variable "custom_managed_policy" {
  description = "List of managed policies to attach to this group"
  default     = []
}

variable "users" {
  description = "List of users to attach to this group"
  default     = []
}

variable "namespace" {
  description = "Namespace for this terraform run"
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
