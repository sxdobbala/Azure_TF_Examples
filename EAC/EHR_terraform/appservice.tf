locals {
# Used for connection string in app service
  sqlservername = "${module.sql_server.server_fqdn}"
  sqldbname="${module.sql_database.name}"
}

resource "azurerm_app_service_plan" "test" {
  name                = "${var.name}-appserviceplan"
  location            = "centralus"
  resource_group_name = "${module.create-resource-group.name}"

  sku {
    tier = "Standard"
    size = "S1"
  }
}

resource "azurerm_app_service" "test" {
  name 		      = "${var.name}-appservice"
  location            = "centralus"
  resource_group_name = "${module.create-resource-group.name}"
  app_service_plan_id = "${azurerm_app_service_plan.test.id}"
  https_only = "true"
  connection_string = {
        name = "Master"
        type = "SqlAzure"
        value = "Encrypt=True; Data Source=loc.sqlservername;Initial Catalog=loc.sqldbname;User Id=${var.SQL_LOGIN};Password=${var.SQL_PASSWORD}"
    }

}

