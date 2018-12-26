variable "group_uuid" {
  description = "Object ID that has privileges on Event Hub Keyvault Key.  Can be a group objectID or an application objectID."
  type = "string"  
}

variable "location" {
  description = "Region/location where Centralized Logging objects live and are defined."
  type = "string"  
}

variable "splunk_sas_token" {
  description = "SAS Token used to access splunk binaries in storage account commercial_cloud_storage/optumcc, in the OptumPOC tenant.  It must be periodically updated, as it expires.  It is also a bit confidential, because files in that data store have credentials used for Splunk Cloud."
  type = "string"  
}
