variable "location" {
  description = "Region/location where Centralized Logging objects live and are defined"
  type = "string"
  default = "centralus"
}

variable "global_tags" {
  type = "map"
  default = { env = "demo" }
}

variable "namespace" {
  description = "Allows creation of objects in an independent name space without conflicting with existing resources."
  type = "string"
  default = ""
}

variable "admin_username" {
  description = "Username for OS admin user (has abilty to log into console and sudo)"
  type = "string"
  default = "osadmin"
}

variable "vm_size" {
  description = "Size of VM"
  type = "string"
  default = "Standard_B1s"
}

variable "disk00_size_gb" {
  description = "OS disk (disk00) size, in GB.  Standard disks are billed at the following tiers:  32G, 64G, 128G, 256G, 512G, 1024G, 2048G, and 4096G"
  type = "string"
  default = "32"
}

variable "resource_group_name" {
  description = "Resource group to build demo within."
  type = "string"
}

variable "optum_addresses" {
  description = ""
  type = "list"
  default  = ["198.203.177.177/32", "198.208.175.175/32", "198.203.181.181/32", "168.183.84.12/32", "149.111.26.128/32", "149.111.28.128/32", "149.111.30.128/32", "220.227.15.70/32", "203.39.148.18/32", "161.249.192.14/32", "161.249.72.14/32", "161.249.80.14/32", "161.249.96.14/32", "161.249.144.14/32", "161.249.176.14/32", "161.249.16.0/23", "12.163.96.0/24"]
}

variable "email_address" {
  description = "Email address, used for LetsEncrypt"
  type = "string"
}

data "template_file" "install_apache_demo" {
  template                                             = "${file("${path.module}/scripts/install_apache_demo.sh.tpl")}"
  vars = {
    fqdn                                               = "${azurerm_public_ip.publicip01.fqdn}"
    email_address                                      = "${var.email_address}"
  }
}

data "template_cloudinit_config" "config" {
  gzip                                                 = true
  base64_encode                                        = true
  part {
    content_type                                       = "text/x-shellscript"
    content                                            = "${file("${path.module}/scripts/harden_os.sh")}"
  }
  part {
    filename                                           = "install_apache_demo.sh"
    content_type                                       = "text/x-shellscript"
    content                                            = "${data.template_file.install_apache_demo.rendered}"
  }
}

# random string used for any resource that must be globally unique
resource "random_id" "randomId" {
  byte_length                                          = 6
}

resource "random_string" "hostname" {
  length                                               = 8
  special                                              = false
  upper                                                = false
  number                                               = false
}

resource "random_string" "password" {
  length                                               = 16
  special                                              = true
  override_special                                     = "-_=+;:[]{}"
  min_special                                          = 1
  min_upper                                            = 1
  min_lower                                            = 1
  min_numeric                                          = 1
}

module "vnet01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//?ref=v2.0.0-beta1"
  name                                                 = "demo1-vnet01"
  location                                             = "${var.location}"
  namespace                                            = "${var.namespace}"
  address_space                                        = ["10.0.0.0/16"]
  resource_group_name                                  = "${var.resource_group_name}"
  tags                                                 = "${var.global_tags}"
}

module "subnet01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/subnet?ref=v2.0.0-beta1"
  name                                                 = "${module.vnet01.name}-subnet01"
  namespace                                            = "${var.namespace}"
  resource_group_name                                  = "${var.resource_group_name}"
  virtual_network_name                                 = "${module.vnet01.name}"
  address_prefix                                       = "10.0.1.0/24"
  network_security_group_name                          = "${module.vnet01.name}-subnet01_nsg01"
  network_security_group_location                      = "${var.location}"
  service_endpoints                                    = ["Microsoft.Storage"]
}

resource "azurerm_network_security_rule" "vnet_inbound" {
  name                                                 = "vnet_inbound"
  description                                          = "Allow VNET Inbound"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1001"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "http_in" {
  name                                                 = "https_in"
  description                                          = "Allow (http) in for LetsEncrypt verification"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1002"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "80"
}

resource "azurerm_network_security_rule" "https_in_optum" {
  name                                                 = "https_in_optum"
  description                                          = "Allow Optum networks to Splunk (https)"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1003"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  #source_address_prefixes                              = ["198.203.177.177/32", "198.208.175.175/32", "198.203.181.181/32", "168.183.84.12/32", "149.111.26.128/32", "149.111.28.128/32", "149.111.30.128/32", "220.227.15.70/32", "203.39.148.18/32", "161.249.192.14/32", "161.249.72.14/32", "161.249.80.14/32", "161.249.96.14/32", "161.249.144.14/32", "161.249.176.14/32", "161.249.16.0/23", "12.163.96.0/24"]
  source_port_range                                    = "*"
  destination_address_prefix                           = "10.0.1.11/32"
  destination_port_range                               = "443"
}

resource "azurerm_network_security_rule" "azure_autoconfig_in" {
  name                                                 = "azure_autoconfig_in"
  description                                          = "Allow inbound access from Azure's special config IP"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "1004"
  access                                               = "Allow"
  direction                                            = "Inbound"
  protocol                                             = "*"
  source_address_prefix                                = "168.63.129.16/32"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "vnet_outbound" {
  name                                                 = "vnet_outbound"
  description                                          = "Allow VNET Outbound"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "2000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "VirtualNetwork"
  source_port_range                                    = "*"
  destination_address_prefix                           = "VirtualNetwork"
  destination_port_range                               = "*"
}

resource "azurerm_network_security_rule" "azure_autoconfig_out" {
  name                                                 = "azure_autoconfig_out"
  description                                          = "Allow outbound access to Azure's special config IP"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "2002"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "*"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "168.63.129.16/32"
  destination_port_range                               = "*"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "http_out" {
  name                                                 = "http_out"
  description                                          = "Allow outbound http access"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "3000"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "80"
}

# Needed for OS updates and pulling some initial packages
resource "azurerm_network_security_rule" "https_out" {
  name                                                 = "https_out"
  description                                          = "Allow outbound https access"
  resource_group_name                                  = "${var.resource_group_name}"
  network_security_group_name                          = "${module.subnet01.network_security_group_name}"
  priority                                             = "3001"
  access                                               = "Allow"
  direction                                            = "Outbound"
  protocol                                             = "tcp"
  source_address_prefix                                = "*"
  source_port_range                                    = "*"
  destination_address_prefix                           = "*"
  destination_port_range                               = "443"
}

resource "azurerm_public_ip" "publicip01" {
  name                                                 = "demo1-publicip01"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  sku                                                  = "Standard"
  public_ip_address_allocation                         = "Static"
  domain_name_label                                    = "${random_string.hostname.result}"
  tags                                                 = "${var.global_tags}"
}

module "nic01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/network_interface/primary_ip_configuration?ref=v2.0.0-beta1"
  name                                                 = "demo1-nic01"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  ip_configuration_name                                = "primary_config"
  ip_configuration_subnet_id                           = "${module.subnet01.id}"
  ip_configuration_private_ip_address_allocation       = "static"
  ip_configuration_private_ip_address                  = "10.0.1.11"
  ip_configuration_public_ip_address_id                = "${azurerm_public_ip.publicip01.id}"
  tags                                                 = "${var.global_tags}"
}

# This block will allow serial console to work, but is not compliant with Optum security standards (no storage firewall, etc). Currently Azure
# doesn't support a serial console storage account with a firewall. :-(
resource "azurerm_storage_account" "storageacct01" {
  # name has to be unique, so add random bits at end
  name                                                 = "demo1${random_id.randomId.hex}"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  account_replication_type                             = "LRS"
  account_tier                                         = "Standard"
  account_kind                                         = "Storage"
  enable_https_traffic_only                            = true
}

resource "azurerm_virtual_machine" "vm01" {
  name                                                 = "demo1-vm01"
  location                                             = "${var.location}"
  resource_group_name                                  = "${var.resource_group_name}"
  network_interface_ids                                = ["${module.nic01.id}"]
  vm_size                                              = "${var.vm_size}"
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
    name                                               = "demo1-vm01_disk00"
    caching                                            = "ReadWrite"
    disk_size_gb                                       = "${var.disk00_size_gb}"
    managed_disk_type                                  = "Standard_LRS"
    create_option                                      = "FromImage"
  }
  os_profile {
    computer_name                                      = "vm01"
    admin_username                                     = "${var.admin_username}"
    admin_password                                     = "${random_string.password.result}"
    custom_data                                        = "${data.template_cloudinit_config.config.rendered}"
  }
  os_profile_linux_config {
    disable_password_authentication                    = false
  }
  boot_diagnostics {
    enabled                                            = true
    storage_uri                                        = "${azurerm_storage_account.storageacct01.primary_blob_endpoint}"
  }
}

output "fqdn" {
  value                                                = "https://${azurerm_public_ip.publicip01.fqdn}"
}
output "location" {
  value                                                = "${var.location}"
}
output "os_admin_password" {
  value                                                = "${random_string.password.result}"
}
output "os_admin_username" {
  value                                                = "${var.admin_username}"
}
