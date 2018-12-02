variable "name" {
  type        = "string"
  description = "The name of the network interface. Changing this forces a new resource to be created."
  default     = "default"
}

variable "namespace" {
  type        = "string"
  description = "Name space for all azurerm_network_interface resources."
  default     = ""
}

variable "resource_group_name" {
  type        = "string"
  description = "The name of the resource group that this network interface should be a part of."
}

variable "location" {
  type        = "string"
  description = "Location defines which availability zone the resource should be created in."
  default     = "centralus"
}

variable "network_security_group_id" {
  type        = "string"
  description = "The ID of the Network Security Group to associate with the network interface."
  default     = ""
}

variable "internal_dns_name_label" {
  type        = "string"
  description = "Relative DNS name for this NIC used for internal communications between VMs in the same VNet."
  default     = "default"
}

variable "enable_ip_forwarding" {
  type        = "string"
  description = "Enables IP Forwarding on the NIC."
  default     = "false"
}

variable "enable_accelerated_networking" {
  type        = "string"
  description = "Enables Azure Accelerated Networking using SR-IOV. Only certain VM instance sizes are supported."
  default     = "false"
}

variable "dns_servers" {
  type        = "list"
  description = "List of DNS servers IP addresses to use for this NIC, overrides the VNet-level server list."
  default     = []
}

variable "tags" {
  type        = "map"
  description = "Tags for the network interface."
  default     = {}
}

// Variables for the ip_configuration block

variable "ip_configuration_name" {
  type        = "string"
  description = "User-defined name of the IP."
}

variable "ip_configuration_subnet_id" {
  type        = "string"
  description = "Reference to a subnet in which this NIC has been created."
}

variable "ip_configuration_private_ip_address" {
  type        = "string"
  description = "Static IP Address."
  default     = ""
}

variable "ip_configuration_private_ip_address_allocation" {
  type        = "string"
  description = "Defines how a private IP address is assigned."
}

variable "ip_configuration_public_ip_address_id" {
  type        = "string"
  description = "Reference to a Public IP Address to associate with this NIC."
  default     = ""
}

variable "ip_configuration_load_balancer_backend_address_pools_ids" {
  type        = "list"
  description = "List of Load Balancer Backend Address Pool IDs references to which this NIC belongs."
  default     = []
}

variable "ip_configuration_load_balancer_inbound_nat_rules_ids" {
  type        = "list"
  description = "List of Load Balancer Inbound Nat Rules IDs involving this NIC."
  default     = []
}