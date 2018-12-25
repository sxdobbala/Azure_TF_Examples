/*
* Define global vars and any other setup elements.
*/

variable "aws_profile" {}

variable "aws_account" {}

variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-2"
}

variable "tag_name_identifier" {
  description = "tag name identifier for the aws_base"
  default     = "poc"
}
