data "azurerm_client_config" "current" {}

module "create-key-vault" {
  source                                          = "terraform_module/key_vault/"
  name                                            = "devops-keyvault"
  namespace                                       = "${module.generate-random-name.name}"
  location                                        = "centralus"
  resource_group_name                             = "${module.create-resource-group.name}"
  tags                                            = { }
  sku_name                                        = "standard"
  enabled_for_deployment                          = "true"
  enabled_for_disk_encryption                     = "true"
  enabled_for_template_deployment                 = "true"
  global_tags                                     = "${var.global_tags}"

  # Permissions for self (i.e. the service principal that is creating the key_vault) - example of ALL privs
  access_policy_certificate_permissions           = ["get", "list", "update", "create", "import", "delete", "managecontacts", "manageissuers", "getissuers", "listissuers", "setissuers", "deleteissuers"]
  access_policy_key_permissions                   = ["get", "list", "update", "create", "import", "delete", "recover", "backup", "restore", "decrypt", "encrypt", "wrapkey", "unwrapkey", "verify", "sign", "purge"]
  access_policy_secret_permissions                = ["get", "list", "set", "delete", "recover", "backup", "restore", "purge"]

  # Permissions for a user/group that can read/use this key vault
  # Note: you must know the object_id - the azurerm provider has no current way to retrieve this. Comment
  # out all of these to not use this functionality.  Tenant_id and object_id below will work for an example,
  # but are not what is generally desired.
  aux_tenant_id                                   = "${data.azurerm_client_config.current.tenant_id}"
  aux_object_id                                   = "00000000-0000-0000-0000-000000000000"
  aux_access_policy_certificate_permissions       = ["get", "list", "getissuers", "listissuers"]
  aux_access_policy_key_permissions               = ["get", "list", "wrapkey", "unwrapkey"]
  aux_access_policy_secret_permissions            = ["get", "list"]
}

module "db-password" {
  source                                          = "terraform_module/key_vault_secret"
  name                                            = "devops-dbpassword"
  value                                           = "Administrator@1234"
  tags                                            = { }
  vault_uri                                       = "${module.create-key-vault.vault_uri}"
  content_type                                    = "text/plain"
  namespace                                       = "${module.generate-random-name.name}"
  global_tags                                     = "${var.global_tags}"
}

/* module "app-gw-pwd" {
  source                                          = "terraform_module/key_vault_secret"
  name                                            = "devops-appgwpwd"
  value                                           = "Test@1234"
  tags                                            = { }
  vault_uri                                       = "${module.create-key-vault.vault_uri}"
  content_type                                    = "text/plain"
  namespace                                       = "${module.generate-random-name.name}"
  global_tags                                     = "${var.global_tags}"
}*/
