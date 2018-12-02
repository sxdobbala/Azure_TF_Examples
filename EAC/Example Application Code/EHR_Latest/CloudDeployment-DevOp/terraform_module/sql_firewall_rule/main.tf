
resource "azurerm_sql_firewall_rule" "firewall" {
  name					= "${var.firewall_rule_name}"
  resource_group_name   = "${var.rg_name}"
  server_name			= "${var.sqlserver_name}"
  start_ip_address		= "${var.start_ip_address}"
  end_ip_address		= "${var.end_ip_address}"
}