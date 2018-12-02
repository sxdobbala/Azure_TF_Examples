variable "app_gateway_name" {
  type        = "string"
  description = "Name of the application gateway"
  default     = ""
}

variable "app_gateway_name_space" {
  type        = "string"
  description = "Name space of the application gateway"
  default     = ""
}

variable "app_gateway_resource_group_name" {
  type        = "string"
  description = "The name of the resource group that this application gateway should be a part of"
}

variable "app_gateway_arm_location" {
  type        = "string"
  description = "Location defines which availability zone the resource should be created in."
  default     = "centralus"
}

variable "app_gateway_subnet_id" {
  type        = "string"
  description = "the subnet id in which app gateway should reside"
}

variable "app_gateway_virtual_network_name" {
  type        = "string"
  description = "Virtual network that the application gateway will be a part of"
}

variable "app_gateway_frontend_public_ip_address_id" {
  type        = "string"
  description = "front end public ip adddress id of app gateway"
  default     = ""
}

variable "app_gateway_frontend_private_ip_address" {
  type        = "string"
  description = "front end private ip address of app gateway"
  default     = ""
}

variable "app_gateway_frontend_private_ip_address_allocation" {
  type        = "string"
  description = "front end private ip address allocation type of app gateway"
  default     = "dynamic"
}

variable "app_gateway_frontend_ip_address_type" {
  type        = "string"
  description = "the type of front end ip address, it should be any of these 12 values (publicip_with_waf_unlimited_ssl, publicip_with_waf_limited_ssl, publicip_without_waf_unlimited_ssl, publicip_without_waf_limited_ssl, privateip_with_waf_unlimited_ssl, privateip_with_waf_limited_ssl, privateip_without_waf_unlimited_ssl, privateip_without_waf_limited_ssl, both_private_publicip_with_waf_unlimited_ssl,both_private_public_ip_with_waf_limited_ssl, both_private_publicip_without_waf_unlimited_ssl, both_private_publicip_without_waf_limited_ssl)"
}

variable "app_gateway_sku" {
  type        = "string"
  description = "SKU name of application gateway"
  default     = "Standard_Medium"
}

variable "app_gateway_capacity" {
  type        = "string"
  description = "capacity of application gateway"
  default     = 2
}

variable "frontend_ip_configuration_name" {
  type        = "string"
  description = "frontend_ip_configuration_name of app gateway"
  default     = "default-app-gateway-feip"
}

variable "app_gateway_ip_configuration_name" {
  type        = "string"
  description = "name of the gateway ip configuration"
  default     = "gateway-ip-configuration"
}

variable "app_gateway_fortend_port_name" {
  type        = "string"
  description = "front end port name of app gateway"
  default     = "default-app-gatewayfeport"
}

/*unlimited ssl related variables)*/
variable "app_gateway_ssl_cert" {
  type        = "list"
  description = "ssl certificate that need to be uploaded into the application gateway for https protocol"
  default     = []
}

variable "app_gateway_authentication_cert" {
  type        = "list"
  description = "list of authentication certificates that need to be uploaded into the application gateway"
  default     = []
}

/*first ssl certifcate (limited ssl related variables)*/
variable "ssl_cert_name_1" {
  type        = "string"
  description = "name of the ssl certificate"
  default     = ""
}

variable "pfx_file_path_1" {
  type        = "string"
  description = "path of .pfx file"
  default     = ""
}

variable "ssl_cert_password_1" {
  type        = "string"
  description = "ssl certificate password"
  default     = ""
}

variable "auth_cert_name_1" {
  type        = "string"
  description = "name of the authentication certificate"
  default     = ""
}

variable "cer_file_path_1" {
  type        = "string"
  description = "file path of .cer file"
  default     = ""
}

/*second ssl certifcate (limited ssl related variables)*/
variable "ssl_cert_name_2" {
  type        = "string"
  description = "name of the ssl certificate"
  default     = ""
}

variable "pfx_file_path_2" {
  type        = "string"
  description = "path of .pfx file"
  default     = ""
}

variable "ssl_cert_password_2" {
  type        = "string"
  description = "ssl certificate password"
  default     = ""
}

variable "auth_cert_name_2" {
  type        = "string"
  description = "name of the authentication certificate"
  default     = ""
}

variable "cer_file_path_2" {
  type        = "string"
  description = "file path of .cer file"
  default     = ""
}

variable "app_gateway_backend_http_settings" {
  type        = "list"
  description = "backend http settings details of app gateway"

  default = [{
    name                  = "default-app-gateway-be-htst"
    cookie_based_affinity = "Disabled"
    port                  = 80
    protocol              = "Http"
    request_timeout       = 40
    probe_name            = ""
  }]
}

variable "app_gateway_backend_address_pool" {
  type        = "list"
  description = "backend address pool of app gateway, it can be either ip addresses list or fqdn list"

  default = [{
    name = "default-app-gateway-beap"
    "fqdn_list" = ""
  }]
}

variable "app_gateway_http_listener_details" {
  type        = "list"
  description = "app gatewapp http listener details"

  default = [{
    name                           = "default-app-gateway-httplstn"
    frontend_ip_configuration_name = "default-app-gateway-feip"
    frontend_port_name             = "default-app-gateway-feport"
    protocol                       = "Http"
  }]
}

variable "app_gateway_routing_request_rule_details" {
  type        = "list"
  description = "application gateway routing request rule details"

  default = [{
    name                       = "dafault-app-gateway-rqrt"
    rule_type                  = "Basic"
    http_listener_name         = "default-app-gateway-httplstn"
    backend_address_pool_name  = "default-app-gateway-beap"
    backend_http_settings_name = "default-app-gateway-be-htst"
  }]
}

variable "app_gateway_waf_configuration_firewall_mode" {
  type        = "string"
  description = "Waf mode of application gateway"
  default     = "Detection"
}

variable "app_gateway_waf_configuration_rule_set_type" {
  type        = "string"
  description = "Waf rule set type of application gateway"
  default     = "OWASP"
}

variable "app_gateway_waf_configuration_rule_set_version" {
  type        = "string"
  description = "Waf rule set version of application gateway"
  default     = "2.2.9"
}

variable "app_gateway_waf_configuration_enabled" {
  type        = "string"
  description = "Waf enable flag of application gateway"
  default     = "true"
}

variable "app_gateway_health_probe_list" {
  type        = "list"
  description = "contains list of health probes"

  default = [{
    name                = "default-health-probe"
    protocol            = "http"
    path                = "/"
    interval            = 5
    timeout             = 4
    unhealthy_threshold = 3
    host                = "127.0.0.1"
  }]
}

variable "app_gateway_url_path_map_name" {
  type        = "string"
  description = "name of the url_path_map property of application gateway"
  default     = "default-path-map"
}

variable "app_gateway_default_backend_address_pool_name" {
  type        = "string"
  description = "default backend address pool name of application gateway in property url_path_map"
  default     = "default-app-gateway-beap"
}

variable "app_gateway_default_backend_http_settings_name" {
  type        = "string"
  description = "default backend address pool name of application gateway in property url_path_map"
  default     = "default-app-gateway-be-htst"
}

variable "app_gateway_path_rule_details" {
  type        = "list"
  description = "contains the list of path rules"

  default = [{
    name                       = "vm-0"
    paths                      = ["/vm-0/"]
    backend_address_pool_name  = "default-app-gateway-beap"
    backend_http_settings_name = "default-app-gateway-be-htst"
  },
    {
      name                       = "vm-1"
      paths                      = ["/vm-1/"]
      backend_address_pool_name  = "default-app-gateway-beap"
      backend_http_settings_name = "default-app-gateway-be-htst"
    },
  ]
}
