resource "azurerm_sql_database" "database" {
  name								= "${var.sqldb_name}"
  resource_group_name				= "${var.rg_name}"
  server_name						= "${var.sqlserver_name}"
  location							= "${var.rg_location}"
  edition							= "${var.sqldb_edition}"
  requested_service_objective_name	= "${var.requested_service_objective_name}"
  max_size_bytes					= "${var.max_size_bytes}"
  collation							= "${var.collation}"
}
