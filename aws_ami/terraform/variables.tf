variable "aws_profile" {
  description = "AWS credential profile"
  default     = "saml"
}

variable "aws_account" {
  description = "AWS account name"
  default     = "packer-builder"
}

variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-1"
}

variable "tag_name_identifier" {
  description = "tag name identifier for the aws_base"
  default     = "utility"
}
