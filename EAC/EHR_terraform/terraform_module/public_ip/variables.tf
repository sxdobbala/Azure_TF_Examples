variable "name" {
  type = "string"
  description = "Specifies the name of the Public IP resource . Changing this forces a new resource to be created."
  default = "public_ip"
}

variable "namespace" {
  type = "string"
  description = "Namespace for the terraform run."
  default = ""
}

variable "resource_group_name" {
  type = "string"
  description = "The name of the resource group in which to create the public ip."
}

variable "location" {
  type = "string"
  description = "Location defines which availability zone the resource should be created in."
  default = "centralus"
}

variable "sku" {
  type = "string"
  description = "The SKU of the Public IP. Accepted values are Basic and Standard."
  default = "Basic"
}

variable "idle_timeout_in_minutes" {
  type = "string"
  description = "Specifies the timeout for the TCP idle connection. The value can be set between 4 and 30 minutes."
  default = "4"
}

variable "reverse_fqdn" {
  type = "string"
  description = "A fully qualified domain name that resolves to this public IP address. If the reverseFqdn is specified, then a PTR DNS record is created pointing from the IP address in the in-addr.arpa domain to the reverse FQDN."
  default = ""
}

variable "tags" {
  type = "map"
  description = "A mapping of tags to assign to the resource."
  default = {}
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters."
  type = "map"
  default = {}
}
