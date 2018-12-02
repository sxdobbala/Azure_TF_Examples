locals {
  sql_server_version = "${lookup(var.sqlserver_version_types, var.sqlserver_version)}"
}

#  name = "${lower(local.namespaced_project_name)}${lower(var.USE_TEMPORARY_DATABASE == "true" ? "-${module.generate-random-name.name}" :"")}"

resource "azurerm_sql_server" "server" {
  name                         = "${var.sqlserver_name}"
  resource_group_name          = "${var.rg_name}"
  location					   = "${var.rg_location}"
  version                      = "${local.sql_server_version}"
  administrator_login          = "${var.admin_id}"
  administrator_login_password = "${var.admin_pwd}"
}


