resource "azurerm_sql_server" "ehr" {
    name = "ehrmysqlserver" 
    resource_group_name = "${module.resource_group.resource_group_name}"
    location = "${var.arm_location}"
    version = "12.0"
    administrator_login = "4dm1n157r470r"
    administrator_login_password = "4-v3ry-53cr37-p455w0rd"
}

resource "azurerm_sql_database" "ehr" {
  name                = "ehrmysqldatabase"
  resource_group_name = "${module.resource_group.resource_group_name}"
    location = "${var.arm_location}"
    server_name = "${azurerm_sql_server.ehr.name}"

  tags {
    environment = "production"
  }
}

