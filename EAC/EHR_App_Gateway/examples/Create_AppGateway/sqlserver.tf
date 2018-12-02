resource "azurerm_sql_server" "sql"{
    resource_group_name =  "${module.create-resource-group.name}"
     name               = "${var.name}-sqlserver"
    location 			= "centralus"
    version = "12.0"
    administrator_login = "${var.SQL_LOGIN}"
    administrator_login_password = "${var.SQL_PASSWORD}"
}

resource "azurerm_sql_database" "sql" {
    resource_group_name =  "${module.create-resource-group.name}"
    location = "centralus"
    //The namespace should not be applied to the db name
    name = "${var.name}-sqldb"
    server_name = "${azurerm_sql_server.sql.name}"
    requested_service_objective_name = "S1"
}

//Allow connections from any azure resource (including those not owned by Optum)
resource "azurerm_sql_firewall_rule" "allowazure" {
  name                = "allow_azure"
  resource_group_name =  "${module.create-resource-group.name}"
  server_name         = "${azurerm_sql_server.sql.name}"
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}

