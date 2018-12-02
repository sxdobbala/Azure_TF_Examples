output "slot_ids" {
  value = "${azurerm_app_service_slot.slot.*.id}"
}

output "app_id" {
  value = "${azurerm_app_service.webapp.id}"
}

output "outbound_ip_addresses" {
  value = "${azurerm_app_service.webapp.outbound_ip_addresses}"
}

output "app_name" {
  value = "${local.app_name}"
}