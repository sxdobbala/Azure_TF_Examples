variable "secondary_ip_configuration_name" {
  type        = "string"
  description = "User-defined name of the IP."
}

variable "secondary_ip_configuration_subnet_id" {
  type        = "string"
  description = "Reference to a subnet in which this NIC has been created."
}

variable "secondary_ip_configuration_private_ip_address" {
  type        = "string"
  description = "Static IP Address."
  default     = ""
}

variable "secondary_ip_configuration_private_ip_address_allocation" {
  type        = "string"
  description = "Defines how a private IP address is assigned."
}

variable "secondary_ip_configuration_public_ip_address_id" {
  type        = "string"
  description = "Reference to a Public IP Address to associate with this NIC."
  default     = ""
}

variable "secondary_ip_configuration_load_balancer_backend_address_pools_ids" {
  type        = "list"
  description = "List of Load Balancer Backend Address Pool IDs references to which this NIC belongs."
  default     = []
}

variable "secondary_ip_configuration_load_balancer_inbound_nat_rules_ids" {
  type        = "list"
  description = "List of Load Balancer Inbound Nat Rules IDs involving this NIC."
  default     = []
}
