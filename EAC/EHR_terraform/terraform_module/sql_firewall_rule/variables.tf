
variable "rg_name" {
  type    = "string"
  default = "default_rg"
  description="The name of the resource group where the SQL server resides."
}

variable "firewall_rule_name" {
  type    = "string"
  default = "defaultFirewall"
  description="The name of Firewall rule being created."
}

variable "sqlserver_name" {
  type    = "string"
  default = "adw-server"
  description="The name of the SQL Server on which to create the firewall rule."
}
 
 variable "start_ip_address" { 
   type        = "string" 
   default     = "149.111.28.128" 
   description = "The starting IP address to allow through the firewall for this rule."
 } 

 variable "end_ip_address" { 
   type        = "string" 
   default     = "149.111.28.128" 
   description = "The ending IP address to allow through the firewall for this rule."
 } 



