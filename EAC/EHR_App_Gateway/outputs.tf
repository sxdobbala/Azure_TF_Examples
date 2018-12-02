output "application_gateway_configuration_id" {
  description = "configuration id of application gateway"

  value = "${
    join(
      "", 
      concat(
        azurerm_application_gateway.appgateway_publicip_with_waf_unlimited_ssl.*.id, 
        azurerm_application_gateway.appgateway_publicip_with_waf_limited_ssl.*.id,
        azurerm_application_gateway.appgateway_publicip_without_waf_unlimited_ssl.*.id, 
        azurerm_application_gateway.appgateway_publicip_without_waf_limited_ssl.*.id,
        azurerm_application_gateway.appgateway_privateip_with_waf_unlimited_ssl.*.id,
        azurerm_application_gateway.appgateway_privateip_with_waf_limited_ssl.*.id,
        azurerm_application_gateway.appgateway_privateip_without_waf_unlimited_ssl.*.id,
        azurerm_application_gateway.appgateway_privateip_without_waf_limited_ssl.*.id,
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_unlimited_ssl.*.id,
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_limited_ssl.*.id,
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_unlimited_ssl.*.id,
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_limited_ssl.*.id
      )
    )
  }"
}

output "application_gateway_name" {
  description = "Name of the application gateway"

  value = "${
    join(
      "", 
      concat(
        azurerm_application_gateway.appgateway_publicip_with_waf_unlimited_ssl.*.name, 
        azurerm_application_gateway.appgateway_publicip_with_waf_limited_ssl.*.name, 
        azurerm_application_gateway.appgateway_publicip_without_waf_unlimited_ssl.*.name,  
        azurerm_application_gateway.appgateway_publicip_without_waf_limited_ssl.*.name, 
        azurerm_application_gateway.appgateway_privateip_with_waf_unlimited_ssl.*.name, 
        azurerm_application_gateway.appgateway_privateip_with_waf_limited_ssl.*.name, 
        azurerm_application_gateway.appgateway_privateip_without_waf_unlimited_ssl.*.name, 
        azurerm_application_gateway.appgateway_privateip_without_waf_limited_ssl.*.name, 
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_unlimited_ssl.*.name, 
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_limited_ssl.*.name, 
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_unlimited_ssl.*.name, 
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_limited_ssl.*.name
      )
    )
  }"
}

output "application_gateway_rg" {
  description = "The resource group name of the application gateway"

  value = "${
    join(
      "", 
      concat(
        azurerm_application_gateway.appgateway_publicip_with_waf_unlimited_ssl.*.resource_group_name,
        azurerm_application_gateway.appgateway_publicip_with_waf_limited_ssl.*.resource_group_name,
        azurerm_application_gateway.appgateway_publicip_without_waf_unlimited_ssl.*.resource_group_name,  
        azurerm_application_gateway.appgateway_publicip_without_waf_limited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_privateip_with_waf_unlimited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_privateip_with_waf_limited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_privateip_without_waf_unlimited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_privateip_without_waf_limited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_unlimited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_limited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_unlimited_ssl.*.resource_group_name, 
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_limited_ssl.*.resource_group_name
      )
    )
  }"
}

output "application_gateway_location" {
  description = "The location/region where the application gateway is created"

  value = "${
    join(
      "", 
      concat(
        azurerm_application_gateway.appgateway_publicip_with_waf_unlimited_ssl.*.location, 
        azurerm_application_gateway.appgateway_publicip_with_waf_limited_ssl.*.location, 
        azurerm_application_gateway.appgateway_publicip_without_waf_unlimited_ssl.*.location,   
        azurerm_application_gateway.appgateway_publicip_without_waf_limited_ssl.*.location,  
        azurerm_application_gateway.appgateway_privateip_with_waf_unlimited_ssl.*.location,  
        azurerm_application_gateway.appgateway_privateip_with_waf_limited_ssl.*.location,  
        azurerm_application_gateway.appgateway_privateip_without_waf_unlimited_ssl.*.location, 
        azurerm_application_gateway.appgateway_privateip_without_waf_limited_ssl.*.location,  
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_unlimited_ssl.*.location, 
        azurerm_application_gateway.appgateway_both_private_publicip_with_waf_limited_ssl.*.location,  
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_unlimited_ssl.*.location, 
        azurerm_application_gateway.appgateway_both_private_publicip_without_waf_limited_ssl.*.location
      )
    )
  }"
}
