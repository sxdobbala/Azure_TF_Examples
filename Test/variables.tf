variable "resource_group_name" {
  description = "The name of the redbox developer account resource group."
}

variable "location" {
  description = "The location to deploy resources to."
}

variable "virtual_network_name" {
  description = "The name of the virtual network."
}

variable "virtual_network_address_space" {
  description = "The address space that is used the virtual network."
  type        = "list"
}

variable "subnet_name" {
  description = "The name of the subnet to create in the virtual network."
}

variable "address_prefix" {
  description = "The address prefix of the subnet."
}

variable "redbox_nsg_name" {
  description = "The name of the network security group that is attached to the subnet."
}

variable "nsg_rule_name" {
  description = "The name of the network security rule."
}
