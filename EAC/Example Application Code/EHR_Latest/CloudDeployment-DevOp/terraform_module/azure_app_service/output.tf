
output "hostname" { 
  value = "${azurerm_app_service.site.default_site_hostname}" 
} 

