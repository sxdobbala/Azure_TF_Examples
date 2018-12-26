variable "location" {
  description = "Region/location where Centralized Logging objects live and are defined"
  type = "string"
  default = "centralus"
}

variable "global_tags" {
  type = "map"
  default = { }
}

variable "namespace" {
  description = "Allows creation of objects in an independent name space without conflicting with existing resources."
  type = "string"
  default = ""
}

variable "admin_username" {
  description = "Username for OS admin user (has abilty to log into console and sudo)"
  type = "string"
  default = "osadmin"
}

variable "forwarder_vm_size" {
  description = "Size of VM for forwarder"
  type = "string"
  default = "Standard_D4s_v3"
}

variable "disk00_size_gb" {
  description = "OS disk (disk00) size, in GB.  Standard disks are billed at the following tiers:  32G, 64G, 128G, 256G, 512G, 1024G, 2048G, and 4096G"
  type = "string"
  default = "32"
}

variable "splunk_sas_token" {
  description = "(Required) SAS Token used to access splunk binaries in storage account commercial_cloud_storage/optumcc, in the OptumPOC tenant.  It must be periodically updated, as it expires.  It is also a bit confidential, because files in that data store have credentials used for Splunk Cloud."
  type = "string"
}

variable "spadmuser" {
  description = "Admin username for Splunk Forwarder Web GUI"
  type = "string"
  default = "spadmin"
}
