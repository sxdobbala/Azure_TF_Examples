variable "name" {
  type = "string"
  description = "Specifies the name of the Key Vault Secret. Changing this forces a new resource to be created."
  default = ""
}

variable "value" {
  type = "string"
  description = "Specifies the value of the Key Vault Secret."
  default = ""
}

variable "vault_uri" {
  type = "string"
  description = "Specifies the URI used to access the Key Vault instance, available on the azurerm_key_vault resource."
  default = ""
}

variable "content_type" {
  type = "string"
  description = "Specifies the content type for the Key Vault Secret."
  default = "text/plain"
}

variable "namespace" {
  type = "string"
  description = "Name space prefix to prepend onto the key vault"
  default = ""
}

variable "tags" {
  description = "Map of tags to apply to key vault secret resource"
  type = "map"
  default = { }
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type = "map"
  default = { }
}
