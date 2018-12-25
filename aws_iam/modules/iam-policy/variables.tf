# Default variables for the iam module
variable "name" {
  description = "Name of the policy"
  default     = ""
}

variable "path" {
  description = "Path in which to create the policy"
  default     = "/"
}

variable "description" {
  description = "Description of the policy"
  default     = ""
}

variable "document" {
  description = "IAM policy document"
  default     = "THIS IS REQUIRED, THERE IS NO DEFAULT"
}

variable "namespace" {
  description = "Name space for this terraform run"
  default     = ""
}

variable "aws_region" {
  default = "us-east-1"
}
