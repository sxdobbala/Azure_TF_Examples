################################################################################
# VARIABLES
################################################################################

variable "location" {
  description                                         = "Region/location where Centralized Logging objects live and are defined"
  type                                                = "string"
  default                                             = "centralus"
}

variable "global_tags" {
  type                                                = "map"
  default                                             = { env = "demo" }
}

variable "namespace" {
  description                                         = "Allows creation of objects in an independent name space without conflicting with existing resources."
  type                                                = "string"
  default                                             = ""
}

variable "admin_username" {
  description                                         = "Username for OS admin user (has abilty to log into console and sudo)"
  type                                                = "string"
  default                                             = "osadmin"
}

variable "vm_size" {
  description                                         = "Size of VM"
  type                                                = "string"
  default                                             = "Standard_A1_v2"
}

variable "disk00_size_gb" {
  description                                         = "OS disk (disk00) size, in GB.  Standard disks are billed at the following tiers:  32G, 64G, 128G, 256G, 512G, 1024G, 2048G, and 4096G"
  type                                                = "string"
  default                                             = "32"
}

variable "resource_group_name" {
  description                                         = "Resource group to build demo within."
  type                                                = "string"
}

variable "optum_addresses" {
  description                                          = ""
  type                                                 = "list"
  default                                              = ["198.203.177.177/32", "198.208.175.175/32", "198.203.181.181/32", "168.183.84.12/32", "149.111.26.128/32", "149.111.28.128/32", "149.111.30.128/32", "220.227.15.70/32", "203.39.148.18/32", "161.249.192.14/32", "161.249.72.14/32", "161.249.80.14/32", "161.249.96.14/32", "161.249.144.14/32", "161.249.176.14/32", "161.249.16.0/23", "12.163.96.0/24"]
}

variable "email_address" {
  description                                          = "Email address, used for LetsEncrypt"
  type                                                 = "string"
  default                                              = "noreply@optum.com"
}
