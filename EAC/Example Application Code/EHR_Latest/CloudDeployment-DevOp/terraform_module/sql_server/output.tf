output "server_fqdn" {
  value = "${azurerm_sql_server.server.fully_qualified_domain_name}"
}

output "server_name" {
  value = "${azurerm_sql_server.server.name}"
}

