data "azurerm_subscription" "current" {}
data "azurerm_client_config" "current" {}

locals {
  subscription_substring = "${substr("${data.azurerm_subscription.current.subscription_id}", 0, 8)}"
}

module "random" {
  source                                              = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
  total_length                                        = 6
}

module "logging_resource_group" {
  source                                              = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//?ref=v2.0.0"
  name                                                = "LoggingEventHub-${var.location}"
  namespace                                           = "${var.namespace}"
  location                                            = "${var.location}"
}

resource "azurerm_management_lock" "eventhub_rg_lock" {
  name                                                 = "TF_eventhub_rg_lock"
  scope                                                = "${module.logging_resource_group.id}"
  lock_level                                           = "CanNotDelete"
}

# create KV for EH secrets
module "logging_key_vault" {
  source                                              = "git::https://github.optum.com/CommercialCloud-EAC/azure_key_vault//?ref=v2.0.0-beta1"
  name                                                = "LogKV-${module.random.name}"
  namespace                                           = "${var.namespace}"
  location                                            = "${var.location}"
  resource_group_name                                 = "${module.logging_resource_group.name}"
  sku_name                                            = "standard"
  enabled_for_deployment                              = "false"
  enabled_for_disk_encryption                         = "false"
  enabled_for_template_deployment                     = "false"

  # Permissions for self (i.e. the service principal that is creating the key_vault) - example of ALL privs
  access_policy_certificate_permissions               = ["get", "list", "update", "create", "import", "delete", "managecontacts", "manageissuers", "getissuers", "listissuers", "setissuers", "deleteissuers"]
  access_policy_key_permissions                       = ["get", "list", "update", "create", "import", "delete", "recover", "backup", "restore", "decrypt", "encrypt", "wrapkey", "unwrapkey", "verify", "sign", "purge"]
  access_policy_secret_permissions                    = ["get", "list", "set", "delete", "recover", "backup", "restore", "purge"]

  # Permissions for a user/group that can read/use this key vault
  # Note: you must know the object_id - the azurerm provider has no current way to retrieve this. Comment
  # out all of these to not use this functionality.  Tenant_id and object_id below will work for an example,
  # but are not what is generally desired.
  aux_tenant_id                                       = "${data.azurerm_client_config.current.tenant_id}"
  aux_object_id                                       = "${var.group_uuid}"
  aux_access_policy_certificate_permissions           = []
  aux_access_policy_key_permissions                   = []
  aux_access_policy_secret_permissions                = ["get", "list"]
}

module "logging_event_hub_namespace" {
  source                                              = "git::https://github.optum.com/CommercialCloud-EAC/azure_event_hub//modules/event_hub_namespace?ref=v2.0.0-beta1"
  type                                                = "without_auto_inflate"
  name                                                = "${var.location}-${local.subscription_substring}"
  resource_group_name                                 = "${module.logging_resource_group.name}"
  location                                            = "${var.location}"
  sku                                                 = "Basic"
  capacity                                            = "1"
  namespace                                           = "${var.namespace}"
}

module "event_hub_secret" {
  source                                              = "git::https://github.optum.com/CommercialCloud-EAC/azure_key_vault//modules/key_vault_secret?ref=v2.0.0-beta1"
  name                                                = "${module.logging_event_hub_namespace.name}-secret"
  value                                               = "${module.logging_event_hub_namespace.default_primary_key}"
  tags                                                = { file-encoding = "utf-8" }
  vault_uri                                           = "${module.logging_key_vault.vault_uri}"
  content_type                                        = "RootManageSharedAccessKey"
  namespace                                           = "${var.namespace}"
}
