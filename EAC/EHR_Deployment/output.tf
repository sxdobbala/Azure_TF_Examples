output "hostname" { 
  value = "${azurerm_app_service.site.default_site_hostname}" 
} 
output "servicename"{
    value = "${local.namespaced_project_name}"
}