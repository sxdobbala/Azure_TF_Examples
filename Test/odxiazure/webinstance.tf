resource "azurerm_public_ip" "publicip01" {
  name                                                 = "${module.random_name.name}-web"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  sku                                                  = "Standard"
  public_ip_address_allocation                         = "Static"
  domain_name_label                                    = "${module.random_name.name}"
  tags                                                 = "${var.global_tags}"
}

module "nic01" {
  source                                               = "git::https://github.optum.com/CommercialCloud-EAC/azure_virtual_network//modules/network_interface/primary_ip_configuration?ref=v2.0.0-beta1"
  name                                                 = "${module.random_name.name}-web"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  ip_configuration_name                                = "primary_config"
  ip_configuration_subnet_id                           = "${module.subnet01.id}"
  ip_configuration_private_ip_address_allocation       = "static"
  ip_configuration_private_ip_address                  = "10.0.1.11"
  ip_configuration_public_ip_address_id                = "${azurerm_public_ip.publicip01.id}"
  tags                                                 = "${var.global_tags}"
}

resource "azurerm_storage_account" "storageacct01" {
  # name has to be unique, so add random bits at end
  name                                                 = "${module.random_name.name}"
  resource_group_name                                  = "${var.resource_group_name}"
  location                                             = "${var.location}"
  account_replication_type                             = "LRS"
  account_tier                                         = "Standard"
  account_kind                                         = "Storage"
  enable_https_traffic_only                            = true
}

resource "azurerm_virtual_machine" "vm01" {
  name                                                 = "${module.random_name.name}-web"
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
    name                                               = "${module.random_name.name}-web"
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
