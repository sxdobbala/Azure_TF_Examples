variable "name" {
  description = "Specifies the name of the Key Vault Key. Changing this forces a new resource to be created."
  type = "string"
  default = ""
}

variable "vault_uri" {
  description = "Specifies the URI used to access the Key Vault instance, available on the azurerm_key_vault resource."
  type = "string"
  default = ""
}

variable "key_type" {
  description = "Key Type - one of 'EC', 'Oct' (Octet), 'RSA' or 'RSA-HSM'"
  type = "string"
  default = "RSA"
}

variable "key_size" {
  description = "Key size - one of '2048', '3072', or '4096'"
  type = "string"
  default = "2048"
}

variable "key_opts" {
  description = "A list of JSON web key operations. Possible values include 'decrypt', 'enable', 'sign', 'unwrapKey', 'verify', and 'wrapKey'. Note case-sensitivity."
  type = "list"
  default = [ ]
}

variable "namespace" {
  description = "Name space prefix to prepend onto the key vault"
  type = "string"
  default = ""
}

variable "tags" {
  description = "Map of tags to apply to key vault key resource"
  type = "map"
  default = { }
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type = "map"
  default = { }
}
