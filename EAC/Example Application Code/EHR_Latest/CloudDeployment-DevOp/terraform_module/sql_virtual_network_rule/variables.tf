variable "vnet_rule_name" {
  type    = "string"
  default = "defaultvnet"
  description="The name of the SQL virtual network rule. Cannot be empty and must only contain alphanumeric characters and hyphens. Cannot start with a number, and cannot start or end with a hyphen."
}
variable "subnet_id" {
  type    = "string"
  default = ""
  description="The ID of the subnet that the SQL server will be connected to."
}

variable "ignore_missing_vnet_service_endpoint" {
  type = "string"
  default = "false"
  description="Create the virtual network rule before the subnet has the virtual network service endpoint enabled."
}
variable "rg_name" {
  type    = "string"
  default = ""
  description="The name of the resource group where the SQL db resides."
}
variable "sqlserver_name" {
  type    = "string"
  default = ""  
}