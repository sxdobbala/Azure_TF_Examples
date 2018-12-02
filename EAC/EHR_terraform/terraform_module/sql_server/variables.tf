variable "rg_name" {
  type    = "string"
  description="The name of the resource group in which to create the SQL Server."
}

variable "rg_location" {
  type    = "string"
  default = "central us"
  description="Specifies the supported Azure location where the server needs to be created."
}

variable "sqlserver_name" {
  type    = "string"
  description="The name of the SQL Server. This needs to be globally unique within Azure."
}

variable "sqlserver_version_types" {
  type="map"
  description = "version types for sql server"
  default = {
  "12.0"="12.0"
  "2.0"="2.0"
  }
  description="The version for the new server. Valid values are: 2.0 (for v11 server) and 12.0 (for v12 server)."
}

variable "sqlserver_version" {
  type    = "string"
  default = "12.0"
  description="The version for the new server. Valid values are: 2.0 (for v11 server) and 12.0 (for v12 server)."
}

variable "admin_id" {
  type    = "string"
  description="The administrator login name for the new server."
}

variable "admin_pwd" {
  type    = "string"
  description="The password associated with the administrator_login user. "
}

variable "audit_storage_account" {
  type = "string"
  description = "name of the storage account which stores the audit logs of the server"
}

variable "audit_event_types" {
  type = "string"
  description = "types of sql events to audit"
  default = "SUCCESSFUL_DATABASE_AUTHENTICATION_GROUP,FAILED_DATABASE_AUTHENTICATION_GROUP,BATCH_COMPLETED_GROUP"
}

variable "audit_days_to_retain" {
  type  = "string"
  description = "amount of days to store sql audit logs"
  default = 90
}
