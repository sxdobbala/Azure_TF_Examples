locals {
  sku_tier = "${var.app_gateway_sku=="WAF_Medium" ? 
"WAF"
:
var.app_gateway_sku=="WAF_Large" ?
"WAF"
:
"Standard" }"
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.app_gateway_name}"
  namespace   = "${var.app_gateway_name_space}"
  name_format = "%s-%s"
}

resource "azurerm_application_gateway" "appgateway_publicip_with_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="publicip_with_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

resource "azurerm_application_gateway" "appgateway_publicip_with_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="publicip_with_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

resource "azurerm_application_gateway" "appgateway_publicip_without_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="publicip_without_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_publicip_without_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="publicip_without_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_privateip_with_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="privateip_with_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

resource "azurerm_application_gateway" "appgateway_privateip_with_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="privateip_with_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

resource "azurerm_application_gateway" "appgateway_privateip_without_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="privateip_without_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_privateip_without_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="privateip_without_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_both_private_publicip_without_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="both_private_publicip_without_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}-1"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_both_private_publicip_without_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="both_private_publicip_without_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}-1"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]
}

resource "azurerm_application_gateway" "appgateway_both_private_publicip_with_waf_unlimited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="both_private_publicip_with_waf_unlimited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}-1"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate            = "${var.app_gateway_ssl_cert}"
  authentication_certificate = "${var.app_gateway_authentication_cert}"
  backend_address_pool       = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

resource "azurerm_application_gateway" "appgateway_both_private_publicip_with_waf_limited_ssl" {
  count               = "${var.app_gateway_frontend_ip_address_type=="both_private_publicip_with_waf_limited_ssl" ? 1 : 0}"
  name                = "${module.namespace.name}"
  resource_group_name = "${var.app_gateway_resource_group_name}"
  location            = "${var.app_gateway_arm_location}"

  sku {
    name     = "${var.app_gateway_sku}"
    tier     = "${local.sku_tier}"
    capacity = "${var.app_gateway_capacity}"
  }

  gateway_ip_configuration {
    name      = "${var.app_gateway_ip_configuration_name}"
    subnet_id = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                          = "${var.frontend_ip_configuration_name}"
    private_ip_address            = "${var.app_gateway_frontend_private_ip_address}"
    private_ip_address_allocation = "${var.app_gateway_frontend_private_ip_address_allocation}"
    subnet_id                     = "${var.app_gateway_subnet_id}"
  }

  frontend_ip_configuration {
    name                 = "${var.frontend_ip_configuration_name}-1"
    public_ip_address_id = "${var.app_gateway_frontend_public_ip_address_id}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_1}"
    data     = "${var.pfx_file_path_1}"
    password = "${var.ssl_cert_password_1}"
  }

  ssl_certificate {
    name     = "${var.ssl_cert_name_2}"
    data     = "${var.pfx_file_path_2}"
    password = "${var.ssl_cert_password_2}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_1}"
    data = "${var.cer_file_path_1}"
  }

  authentication_certificate {
    name = "${var.auth_cert_name_2}"
    data = "${var.cer_file_path_2}"
  }

  backend_address_pool = "${var.app_gateway_backend_address_pool}"

  frontend_port {
    name = "${var.app_gateway_fortend_port_name}"
    port = 443
  }

  probe                 = "${var.app_gateway_health_probe_list}"
  backend_http_settings = "${var.app_gateway_backend_http_settings}"
  http_listener         = "${var.app_gateway_http_listener_details}"
  request_routing_rule  = "${var.app_gateway_routing_request_rule_details}"

  url_path_map = [{
    name                               = "${var.app_gateway_url_path_map_name}"
    default_backend_address_pool_name  = "${var.app_gateway_default_backend_address_pool_name}"
    default_backend_http_settings_name = "${var.app_gateway_default_backend_http_settings_name}"
    path_rule                          = "${var.app_gateway_path_rule_details}"
  }]

  waf_configuration {
    firewall_mode    = "${var.app_gateway_waf_configuration_firewall_mode}"
    rule_set_type    = "${var.app_gateway_waf_configuration_rule_set_type}"
    rule_set_version = "${var.app_gateway_waf_configuration_rule_set_version}"
    enabled          = "${var.app_gateway_waf_configuration_enabled}"
  }
}

