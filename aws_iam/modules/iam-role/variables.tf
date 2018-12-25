# Default variables for the iam module
variable "name" {
  description = "Name of the role"
  default     = ""
}

variable "description" {
  description = "Description of the role"
  default     = ""
}

variable "path" {
  description = "The path to the role"
  default     = "/"
}

variable "assume_role_aws_principals" {
  description = "List of AWS principals allowed to assume this role"
  default     = []
}

variable "assume_role_service_principals" {
  description = "List of Service principals allowed to assume this role"
  default     = []
}

variable "assume_role_federated_principals" {
  description = "List of Federated principals allowed to assume this role"
  default     = []
}

variable "custom_inline_policy_count" {
  description = "Count of inline policies to attach to this role"
  default     = 0
}

variable "custom_inline_policy" {
  description = "List of inline policies to attach to this role; inline_name, inline_policy_statement"

  default = [{
    custom_inline_name   = "null"
    custom_inline_policy = "{\"Version\": \"2012-10-17\",\"Statement\":[]}"
  }]
}

variable "custom_managed_policy_count" {
  description = "Count of managed policies to attach to this role"
  default     = 0
}

variable "custom_managed_policy" {
  description = "List of managed policies to attach to this role"
  default     = []
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
