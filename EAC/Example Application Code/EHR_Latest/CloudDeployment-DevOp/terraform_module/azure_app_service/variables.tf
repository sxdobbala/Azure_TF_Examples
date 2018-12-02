variable "plan_name" {
  description = "Name of app service plan"
  default=""
}
variable "site_name" {
  description = "Name of app service"
  default=""
}

variable "rg_name" {
  description = "Namespace for applicaiton"
  default = ""
}
variable "rg_location" {
    type="string"
    default="East US"
}
variable "sqlserver_name" {
    type="string"
	default=""
} 
variable "sqldb_name" {
  type="string"
  description = ""
  default=""
}
variable "sql_login" {
   type="string"
	description = ""
	default=""
}
variable "sql_pw" {
   type="string"
   description = ""
   default=""
}

