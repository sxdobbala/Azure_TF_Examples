variable "name" {
  type        = "string"
  description = "Specifies the name of the network security group. Changing this forces a new resource to be created."
}

variable "namespace" {
  type        = "string"
  description = "Namespace for the terraform run."
  default     = ""
}

variable "resource_group_name" {
  type        = "string"
  description = "The name of the resource group in which to create the network security group. Changing this forces a new resource to be created."
}

variable "location" {
  type        = "string"
  description = "Specifies the supported Azure location where the resource exists. Changing this forces a new resource to be created."
  default     = "centralus"
}

variable "tags" {
  type        = "map"
  description = "A mapping of tags to assign to the resource."
  default     = {}
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters."
  type        = "map"
  default     = {}
}
