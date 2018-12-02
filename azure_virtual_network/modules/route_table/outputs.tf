output "id" {
  description = "The Route Table ID"
  value       = "${azurerm_route_table.route_table.id}"
}

output "name" {
  description = "The Route Table name"
  value       = "${azurerm_route_table.route_table.name}"
}

output "subnets" {
  description = "The collection of Subnets associated with this route table"
  value       = "${azurerm_route_table.route_table.subnets}"
}

output "route_ids" {
  description = "The collection of ids associated with the routes that were created"
  value       = "${azurerm_route.route.*.id}"
}
