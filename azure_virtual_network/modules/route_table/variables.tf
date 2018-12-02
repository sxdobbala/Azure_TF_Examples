variable "name" {
  type        = "string"
  description = "Name of the route table to be created"
  default     = ""
}

variable "namespace" {
  type        = "string"
  description = "Name space for all Azure route table resources"
  default     = ""
}

variable "resource_group_name" {
  type        = "string"
  description = "The name of the resource group that this route table should be a part of"
}

variable "location" {
  type        = "string"
  description = "The location / zone in which this route table will be located"
  default     = "Central US"
}

variable "route_count" {
  description = "Number of routes to create. This is in place to circumvent a terraform shortcoming with passing counts to modules"
  default     = 0
}

variable "route_list" {
  type        = "list"
  description = "List of user defined routes that will be contained in the route table"
  default     = []
}

variable "tags" {
  description = "Map of tags to apply to this route table."
  type        = "map"
  default     = {}
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type        = "map"
  default     = {}
}
