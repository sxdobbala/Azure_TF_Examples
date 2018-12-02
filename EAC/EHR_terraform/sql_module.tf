module "db_subnet" {
  source                                = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network.git//modules/subnet?ref=v2.0.0-beta1"
  namespace                             = "${module.generate-random-name.name}"
  virtual_network_name                  = "${module.create_virtual_network.name}"
  resource_group_name                   = "${module.create-resource-group.name}"
  name                                  = "${var.name}-dbsubnet"
  address_prefix                        = "10.0.2.0/24"
  network_security_group_name           = "${var.name}-dbnsg"
  network_security_group_location       = "CentralUS"
  service_endpoints                      = ["Microsoft.KeyVault","Microsoft.Sql"]
}

module "audit_storage_account" {
  source                                      = "git::https://github.optum.com/CommercialCloud-EAC/azure_storage.git//terraform_module/account?ref=v1.1.6"
  storage_account_name                        = "auditstorage"
  name_space                                  = "${module.generate-random-name.name}"
  storage_account_resource_group_name         = "${module.create-resource-group.name}"
  arm_location                                = "CentralUS"
  storage_account_kind                        = "StorageV2"
  storage_account_tier                        = "Standard"
  storage_account_replication_type            = "LRS"
  storage_account_encryption_source           = "Microsoft.Storage"
}

module "sql_server" {
  source              = "terraform_module/sql_server"
  sqlserver_name      = "${var.name}-sqlserver"
  rg_name             = "${module.create-resource-group.name}"
  rg_location         = "CentralUS"
  admin_id            = "${var.SQL_LOGIN}"
  # admin_pwd           = "${var.SQL_PASSWORD}"
  admin_pwd            = "${module.db-password.id}"
  audit_storage_account = "${module.audit_storage_account.storage_account_name}"
}

/* module "sql_server_use_external_encryption_key" {
  source = "../../modules/sql_server_byok_support"
  keyvault_subscription_id = "${data.azurerm_client_config.current.subscription_id}"
  keyvault_name = "${azurerm_key_vault.keyvault.name}"
  sqlserver_name = "${module.sql_server.server_name}"
  rg_name = "${module.create-resource-group.name}"
  key_uri = "${azurerm_key_vault_key.sql_key.id}"
}*/

module "sql_server_firewall" {
  source             = "terraform_module/sql_firewall_rule"
  firewall_rule_name = "${module.sql_server.server_name}-firewall-rule"
  rg_name            = "${module.create-resource-group.name}"
  sqlserver_name     = "${module.sql_server.server_name}"
  start_ip_address   = "10.0.0.0"
  end_ip_address     = "10.0.1.0"
}

module "sql_vnet_rules" {
  source                               = "terraform_module/sql_virtual_network_rule"
  vnet_rule_name                       = "${module.sql_server.server_name}-vnet-rule"
  rg_name                              = "${module.create-resource-group.name}"
  sqlserver_name                       = "${module.sql_server.server_name}"
  subnet_id                            = "${module.db_subnet.id}"
  ignore_missing_vnet_service_endpoint = "true"
}

module "sql_database" {
  source                           = "terraform_module/sql_database"
  rg_name                          = "${module.create-resource-group.name}"
  rg_location                      = "CentralUS"
  requested_service_objective_name = "Basic"
  sqlserver_name                   = "${module.sql_server.server_name}"
  sqldb_name                       = "${var.name}-sqldb"
  sqldb_edition                    = "Basic"
}
