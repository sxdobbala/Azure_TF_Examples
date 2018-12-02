variable "name" {
  description = "Key Vault Name"
  type = "string"
  default = ""
}

variable "location" {
  description = "Azure location where the Key Vault lives"
  type = "string"
  default = "centralus"
}

variable "resource_group_name" {
  description = "Name of the resource group where the Key Vault should be stored"
  type = "string"
  default = ""
}

variable "sku_name" {
  description = "SKU name to specify whether the key vault is a 'standard' or 'premium' vault."
  type = "string"
  default = "standard"
}

variable "sku_name_types" {
  description = ""
  type = "map"
  default = {
    "standard" = "standard"
    "Standard" = "standard"
    "premium" = "premium"
    "Premium" = "premium"
  }
}

variable "access_policy_certificate_permissions" {
  description = "List of certificate permissions, must be one or more from the following: create, delete, deleteissuers, get, getissuers, import, list, listissuers, managecontacts, manageissuers, purge, recover, setissuers and update."
  type = "list"
  default = ["get"]
}

variable "access_policy_key_permissions" {
  description = "List of key permissions, must be one or more from the following: backup, create, decrypt, delete, encrypt, get, import, list, purge, recover, restore, sign, unwrapKey, update, verify and wrapKey."
  type = "list"
  default = ["get"]
}

variable "access_policy_secret_permissions" {
  description = "List of secret permissions, must be one or more from the following: backup, delete, get, list, purge, recover, restore and set."
  type = "list"
  default = ["get"]
}

variable "enabled_for_deployment" {
  description = "Boolean flag to specify whether Azure Virtual Machines are permitted to retrieve certificates stored as secrets from the key vault. Defaults to false."
  type = "string"
  default = false
}

variable "enabled_for_disk_encryption" {
  description = "Boolean flag to specify whether Azure Disk Encryption is permitted to retrieve secrets from the vault and unwrap keys. Defaults to false."
  type = "string"
  default = false
}

variable "enabled_for_template_deployment" {
  description = "Boolean flag to specify whether Azure Resource Manager is permitted to retrieve secrets from the key vault. Defaults to false."
  type = "string"
  default = false
}

variable "namespace" {
  description = "Name space prefix to prepend onto the key vault"
  type = "string"
  default = ""
}

variable "tags" {
  description = "Map of tags to apply to key vault resource"
  type = "map"
  default = { }
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type = "map"
  default = { }
}

variable "tenant_id" {
  description = "Tenant ID for Key Vault and initial access policy. Defaults to creator's tenant_id."
  type = "string"
  default = ""
}

variable "object_id" {
  description = "Object ID for Key Vault and initial access policy. Defaults to creator's object_id."
  type = "string"
  default = ""
}


# The following are used to give an additional object (generally a group) permissions to use the key vault.

variable "aux_tenant_id" {
  description = "Tenant ID for optional additional group/user to be given access to the key vault being created."
  type = "string"
  default = ""
}
variable "aux_object_id" {
  description = "Object ID for optional additional group/user to be given access to the key vault being created."
  type = "string"
  default = ""
}
variable "aux_access_policy_certificate_permissions" {
  description = "List of certificate permissions for additional optional group/user; must be one or more from the following: create, delete, deleteissuers, get, getissuers, import, list, listissuers, managecontacts, manageissuers, purge, recover, setissuers and update."
  type = "list"
  default = []
}
variable "aux_access_policy_key_permissions" {
  description = "List of key permissions for optional additional group/user; must be one or more from the following: backup, create, decrypt, delete, encrypt, get, import, list, purge, recover, restore, sign, unwrapKey, update, verify and wrapKey."
  type = "list"
  default = []
}
variable "aux_access_policy_secret_permissions" {
  description = "List of secret permissions for optional additional group/user; must be one or more from the following: backup, delete, get, list, purge, recover, restore and set."
  type = "list"
  default = []
}
