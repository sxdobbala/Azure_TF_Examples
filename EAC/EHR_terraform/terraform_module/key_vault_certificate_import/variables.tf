variable "name" {
  description = "Specifies the name of the Key Vault Certificate. Changing this forces a new resource to be created."
  type = "string"
  default = ""
}

variable "vault_uri" {
  description = "Specifies the URI used to access the Key Vault instance, available on the azurerm_key_vault resource."
  type = "string"
  default = ""
}

variable "issuer_name" {
  description = "Name of certificate issuer. Possible values are 'Self', or the name of a CA supported by Azure (such as 'DigiCert' or 'GlobalSign')."
  type = "string"
  default = "Self"
}

variable "private_key_is_exportable"  {
  description = "Should private key be exportable?"
  default = false
}

variable "reuse_private_key_for_renewal" {
  description = "Should private key be reused upon certificate renewal?"
  default = true
}

variable "key_size" {
  description = "Possible values are '2048', '3072', and '4096'"
  default = 2048
}

variable "key_type" {
  description = "Either 'RSA' or 'RSA-HSM'.  Private key export is not possible with RSA-HSM."
  type = "string"
  default = "RSA"
}

variable "content_type" {
  description = "Content-Type of the certificate. Can be 'application/x-pkcs12' for PFX, or 'application/x-pem-file' for PEM."
  type = "string"
  default = "application/x-pkcs12"
}

# lifetime_action is optional - if set to "", no action will be taken
variable "lifetime_action_type" {
  description = "Action performed when expiration action is triggered. Can be '', 'AutoRenew' or 'EmailContacts'."
  type = "string"
  default = ""
}
variable "lifetime_action_trigger_days_before_expiry" {
  description = "Number of days before certificate expiration that the lifetime action (see above) should be triggered"
  default = 30
}
# incompatible with days_before_expiry - not currently implemented
#variable "lifetime_action_trigger_percentage" {
#  description = ""
#  default = ""
#}

variable "contents" {
  description = "Base64-encoded certificate to import (as string). If this is a non-empty string, key_vault_certificate_filename is ignored."
  type = "string"
  default = ""
}

variable "filename" {
  description = "File name of certificate to import"
  type = "string"
  default = ""
}

variable "password" {
  description = "Password for certificate to import"
  type = "string"
  default = ""
}

variable  "namespace" {
  type = "string"
  description = "Name space prefix to prepend onto the key vault certificate"
  default = ""
}

variable "tags" {
  description = "Map of tags to apply to key vault certificate resource"
  type = "map"
  default = { }
}

variable "global_tags" {
  description = "Map of tags to apply to all resources that have tags parameters"
  type = "map"
  default = { }
}
