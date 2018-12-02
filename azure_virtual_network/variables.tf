variable "name" {
  type = "string"
  description = "The name of the virtual network. Changing this forces a new resource to be created."
  default = "virtual_network"
}

variable "namespace" {
  type = "string"
  description = "Namespace for the terraform run."
  default = ""
}

variable "resource_group_name" {
  type = "string"
  description = "The name of the resource group in which to create the virtual network."
}

variable "address_space" {
  type = "list"
  description = "The address space that is used the virtual network. You can supply more than one address space. Changing this forces a new resource to be created."
  default = ["10.0.0.0/16"]
}

variable "location" {
  type = "string"
  description = "The location/region where the virtual network is created. Changing this forces a new resource to be created."
  default = "centralus"
}

variable "dns_servers" {
  type = "list"
  description = "List of IP addresses of DNS servers."
  default = []
}

variable "tags" {
  description = "A mapping of tags to assign to the resource."
  type = "map"
  default = {}
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters."
  type = "map"
  default = {}
}
