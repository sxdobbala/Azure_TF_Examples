data "template_file" "get_splunk" {
  template = "${file("${path.module}/scripts/get_splunk.sh.tpl")}"
  vars = {
    sas_token = "${var.splunk_sas_token}"
  }
}

data "template_file" "install_apache_proxy" {
  template = "${file("${path.module}/scripts/install_apache_proxy.sh.tpl")}"
  vars = {
    fqdn = "${azurerm_public_ip.splunkvm_splunk01_publicip01.fqdn}"
  }
}

data "template_file" "install_splunk" {
  template = "${file("${path.module}/scripts/install_splunk.sh.tpl")}"
  vars = {
    spadmuser = "${var.spadmuser}"
    spadmpass = "${random_string.spadmpass.result}"
  }
}

data "template_cloudinit_config" "config" {
  gzip                                                 = true
  base64_encode                                        = true
  part {
    filename     = "get_splunk.sh"
    content_type = "text/x-shellscript"
    content      = "${data.template_file.get_splunk.rendered}"
  }
  part {
    content_type = "text/x-shellscript"
    content      = "${file("${path.module}/scripts/harden_os.sh")}"
  }
  part {
    filename     = "install_apache_proxy.sh"
    content_type = "text/x-shellscript"
    content      = "${data.template_file.install_apache_proxy.rendered}"
  }
  part {
    filename     = "install_splunk.sh"
    content_type = "text/x-shellscript"
    content      = "${data.template_file.install_splunk.rendered}"
  }
}

resource "random_id" "randomId" {
  # only generate a new random id when the resource group changes
  keepers = {
    resource_group                                     = "${module.splunkvm_resource_group.name}"
  }
  byte_length                                          = 4
}

resource "random_string" "hostname" {
  length                                               = 18
  special                                              = false
  upper                                                = false
  number                                               = false
}

resource "random_string" "password" {
  length                                               = 16
  special                                              = true
  override_special                                     = "-_=+[]{}"
  min_special                                          = 1
  min_upper                                            = 1
  min_lower                                            = 1
  min_numeric                                          = 1
}

resource "random_string" "spadmpass" {
  length                                               = 16
  special                                              = false
  min_upper                                            = 1
  min_lower                                            = 1
  min_numeric                                          = 1
}

module "splunkvm_resource_group" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//?ref=v2.0.0"
  name                                                 = "LoggingSplunk-${var.location}"
  namespace                                            = "${var.namespace}"
  location                                             = "${var.location}"
}

resource "azurerm_management_lock" "splunk_rg_lock" {
  name                                                 = "TF_splunk_rg_lock"
  scope                                                = "${module.splunkvm_resource_group.id}"
  lock_level                                           = "CanNotDelete"
}

module "splunkvm_virtual_network" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//?ref=v2.0.0-beta1"
  name                                                 = "splunkvm_virtnet"
  location                                             = "${var.location}"
  namespace                                            = "${var.namespace}"
  address_space                                        = ["10.0.0.0/16"]
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  global_tags                                          = "${var.global_tags}"
}

resource "azurerm_network_security_group" "splunkvm_nsg" {
  name                                                 = "splunk_nsg${var.namespace}"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  location                                             = "${var.location}"
  tags                                                 = "${var.global_tags}"
}

resource "azurerm_subnet" "splunkvm_subnet" {
  name                                                 = "splunkvm_subnet${var.namespace}"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  virtual_network_name                                 = "${module.splunkvm_virtual_network.name}"
  address_prefix                                       = "10.0.1.0/24"
  network_security_group_id                            = "${azurerm_network_security_group.splunkvm_nsg.id}"
  service_endpoints                                    = ["Microsoft.Storage"]
}

resource "azurerm_network_security_rule" "splunkvm_vnet_inbound" {
  name                                                 = "splunkvm_vnet_inbound"
  description                                          = "Allow VNET Inbound"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "1000"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "splunkvm_http_in" {
  name                                                 = "splunkvm_http_in"
  description                                          = "Allow (http) in for LetsEncrypt verification"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "1001"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "80"
}

resource "azurerm_network_security_rule" "splunkvm_https_optum_networks" {
  name                                                 = "splunkvm_https_optum"
  description                                          = "Allow Optum networks to Splunk (https)"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "1002"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  #source_address_prefixes                              = ["198.203.177.177/32", "198.208.175.175/32", "198.203.181.181/32", "168.183.84.12/32", "149.111.26.128/32", "149.111.28.128/32", "149.111.30.128/32", "220.227.15.70/32", "203.39.148.18/32", "161.249.192.14/32", "161.249.72.14/32", "161.249.80.14/32", "161.249.96.14/32", "161.249.144.14/32", "161.249.176.14/32", "161.249.16.0/23", "12.163.96.0/24"]
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "443"
}

resource "azurerm_network_security_rule" "splunkvm_azure_autoconfig_in" {
  name                                                 = "splunkvm_azure_autoconfig_in"
  description                                          = "Allow inbound access from Azure's special config IP"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "1003"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "168.63.129.16/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "splunkvm_vnet_outbound" {
  name                                                 = "splunkvm_vnet_outbound"
  description                                          = "Allow VNET Outbound"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "splunkcloud" {
  name                                                 = "splunkcloud"
  description                                          = "Allow Splunk protocol to splunk cloud"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2001"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "9997"
}

resource "azurerm_network_security_rule" "splunkvm_azure_autoconfig_out" {
  name                                                 = "splunkvm_azure_autoconfig_out"
  description                                          = "Allow outbound access to Azure's special config IP"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2002"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "168.63.129.16/32"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "splunkvm_azure_eventhub_amqps" {
  name                                                 = "splunkvm_azure_eventhub_amqps"
  description                                          = "Allow outbound amqps (ssl) access to Azure Event Hub"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2003"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "EventHub"
  destination_port_range                               = "5671"
}

resource "azurerm_network_security_rule" "splunkvm_azure_keyvault" {
  name                                                 = "splunkvm_azure_keyvault"
  description                                          = "Allow outbound https access to Azure Keyvault"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2004"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "AzureKeyVault"
  destination_port_range                               = "443"
}

resource "azurerm_network_security_rule" "splunkvm_azure_storage_https" {
  name                                                 = "splunkvm_azure_storage_https"
  description                                          = "Allow outbound https access to Azure Storage"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "2005"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "Storage"
  destination_port_range                               = "443"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "splunkvm_http_out" {
  name                                                 = "splunkvm_http_out"
  description                                          = "Allow outbound http access"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "3000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "80"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "splunkvm_https_out" {
  name                                                 = "splunkvm_https_out"
  description                                          = "Allow outbound https access"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "3001"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "10.0.1.11/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "443"
}

resource "azurerm_network_security_rule" "explicitDeny" {
  name                                                 = "explicitDeny"
  description                                          = "Explicit deny of everything else"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_security_group_name                          = "${azurerm_network_security_group.splunkvm_nsg.name}"
  priority                                             = "4096"
  access                                               = "Deny"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "*"
}


#############################################################
# VM splunk01 - heavy forwarder and license server
#############################################################

resource "azurerm_public_ip" "splunkvm_splunk01_publicip01" {
  name                                                 = "splunk01_publicip01"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  location                                             = "${var.location}"
  sku                                                  = "Basic"
  public_ip_address_allocation                         = "Static"
  idle_timeout_in_minutes                              = "4"
  domain_name_label                                    = "${random_string.hostname.result}"
  tags                                                 = "${var.global_tags}"
}

module "splunkvm_splunk01_nic01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/network_interface/primary_ip_configuration?ref=v2.0.0-beta1"
  name                                                 = "splunk01_nic01"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  location                                             = "${var.location}"
  ip_configuration_name                                = "primary_config"
  #ip_configuration_subnet_id                           = "${module.splunkvm_subnet.id}"
  ip_configuration_subnet_id                           = "${azurerm_subnet.splunkvm_subnet.id}"
  ip_configuration_private_ip_address_allocation       = "static"
  ip_configuration_private_ip_address                  = "10.0.1.11"
  ip_configuration_public_ip_address_id                = "${azurerm_public_ip.splunkvm_splunk01_publicip01.id}"
}

# This block will allow serial console to work, but is not compliant with Optum security standards (no storage firewall)
resource "azurerm_storage_account" "splunkvm_storage_account" {
  # name has to be globally unique, so make random
  name                                                 = "splunk01${random_id.randomId.hex}"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  location                                             = "${var.location}"
  account_replication_type                             = "LRS"
  account_tier                                         = "Standard"
  account_kind                                         = "StorageV2"
  access_tier                                          = "Hot"
  enable_https_traffic_only                            = "true"
}

resource "azurerm_virtual_machine" "splunk01" {
  name                                                 = "splunk01"
  location                                             = "${var.location}"
  resource_group_name                                  = "${module.splunkvm_resource_group.name}"
  network_interface_ids                                = ["${module.splunkvm_splunk01_nic01.id}"]
  vm_size                                              = "${var.forwarder_vm_size}"
  tags                                                 = "${var.global_tags}"
  delete_os_disk_on_termination                        = true
  delete_data_disks_on_termination                     = true
  storage_image_reference {
    publisher                                          = "Canonical"
    offer                                              = "UbuntuServer"
    sku                                                = "18.04-LTS"
    version                                            = "latest"
  }
  storage_os_disk {
    name                                               = "splunk01_disk00"
    caching                                            = "ReadWrite"
    disk_size_gb                                       = "${var.disk00_size_gb}"
    managed_disk_type                                  = "Standard_LRS"
    create_option                                      = "FromImage"
  }
  os_profile {
    computer_name                                      = "splunk01"
    admin_username                                     = "${var.admin_username}"
    admin_password                                     = "${random_string.password.result}"
    custom_data                                        = "${data.template_cloudinit_config.config.rendered}"
  }
  os_profile_linux_config {
    disable_password_authentication                    = false
  }
  boot_diagnostics {
    enabled                                            = true
    storage_uri                                        = "${azurerm_storage_account.splunkvm_storage_account.primary_blob_endpoint}"
  }
}
