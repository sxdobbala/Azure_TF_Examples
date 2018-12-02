/*
locals{
     namespaced_project_name = "${format("%s-%s", var.PROJECT_NAME, local.namespace)}"
}
*/
resource "azurerm_sql_virtual_network_rule" "sqlvnetrule" {
  name							= "${var.vnet_rule_name}"
  resource_group_name           = "${var.rg_name}"
  server_name					= "${var.sqlserver_name}"
  subnet_id                     = "${var.subnet_id}"
  ignore_missing_vnet_service_endpoint = "${var.ignore_missing_vnet_service_endpoint}"
}