resource "azurerm_app_service_plan" "siteplan"{
    resource_group_name = "${var.rg_name}"
    name				= "${var.plan_name}"
    location			= "${var.rg_location}"
    kind = "app"
    sku = {
        size = "S1"
        tier = "Standard"
    }
}
resource "azurerm_app_service" "site" {
    resource_group_name		= "${var.rg_name}"
	name					= "${var.site_name}"
    location				= "${var.rg_location}"
    app_service_plan_id		= "${azurerm_app_service_plan.siteplan.id}"
    https_only				= "true"
    connection_string		= {
        name = "Master"
        type = "SqlAzure"
        value = "Encrypt=True; Data Source=${var.sqlserver_name};Initial Catalog=${var.sqldb_name};User Id=${var.sql_login};Password=${var.sql_pw}"
    }
}