variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "aws_region" {}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}
