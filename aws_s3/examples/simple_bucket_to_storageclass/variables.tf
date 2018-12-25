variable "aws_region" {}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}

variable "prefix" {}

variable "id" {}

variable "enabled"  {}

variable "initial_storage_class" {}

variable "final_storage_class"  {}

variable "days_initial_storage_class" {}

variable "days_final_storage_class" {}

variable "expiration_days" {}

variable "noncurrent_storage_class" {}

variable "days_noncurrent_storage_class" {}