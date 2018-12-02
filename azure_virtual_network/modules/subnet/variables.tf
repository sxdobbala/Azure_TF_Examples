variable "name" {
  type        = "string"
  description = "The name of the subnet. Changing this forces a new resource to be created."
  default     = "subnet"
}

variable "namespace" {
  type        = "string"
  description = "Namespace for the terraform run."
  default     = ""
}

variable "resource_group_name" {
  type        = "string"
  description = "The name of the resource group in which to create the subnet. Changing this forces a new resource to be created."
}

variable "virtual_network_name" {
  type        = "string"
  description = "The name of the virtual network to which to attach the subnet. Changing this forces a new resource to be created."
}

variable "address_prefix" {
  type        = "string"
  description = "The address prefix to use for the subnet."
}

variable "route_table_id" {
  type        = "string"
  description = "The ID of the Route Table to associate with the subnet."
  default     = ""
}

variable "service_endpoints" {
  type        = "list"
  description = "The list of Service endpoints to associate with the subnet. Possible values include: Microsoft.Storage, Microsoft.Sql."
  default     = []
}

variable "network_security_group_name" {
  type        = "string"
  description = "The name of the network security group that is attached to the subnet."
}

variable "network_security_group_location" {
  type        = "string"
  description = "Specifies the supported Azure location where the subnet's network security group exists. Changing this forces a new resource to be created."
  default     = "centralus"
}
