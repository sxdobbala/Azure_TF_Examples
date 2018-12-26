variable "location" {
  description = "Region/location where Centralized Logging objects live and are defined"
  type = "string"
  default = "centralus"
}

variable "group_uuid" {
  description = "UUID of AD group that is allowed to use this Event Hub (group contains SPs for each subscription)"
  type = "string"
}

variable "global_tags" {
  type = "map"
  default = { }
}

variable "namespace" {
  description = "Allows creation of objects in an independent name space without conflicting with existing resources."
  type = "string"
  default = ""
}
