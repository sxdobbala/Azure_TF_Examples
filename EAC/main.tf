module "resource_group" {
  source              = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//terraform_module?ref=v1.0.1"
  resource_group_name = "ehr_terr_resource-group"
  name_space          = "${var.global_name_space}"
  arm_location        = "${var.arm_location}"
}

// As mentioned in the readme, each NSG will also get a set of default rules specified here: 
// https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#default-security-rules.
// Additional rules are added below.

module "web_tier_subnet_nsg" {
  source                    = "../terraform_module/network_security_group"
  name_space                = "${var.global_name_space}"
  nsg_name                  = "web_tier_subnet_nsg"
  nsg_arm_location          = "${var.arm_location}"
  nsg_resource_group_name   = "${module.resource_group.resource_group_name}"
  nsg_tags                  = {}
  security_rules_list_count = 2
  security_rules_list       = [
                                {
                                  name                        = "allow-inbound-https"
                                  priority                    = "100"
                                  direction                   = "Inbound"
                                  access                      = "Allow"
                                  protocol                    = "tcp"
                                  source_port_range           = "*"  
                                  destination_port_range      = "443"
                                  source_address_prefix       = "Internet"
                                  destination_address_prefix  = "10.0.1.0/24"
                                  description                 = "Allow inbound traffic using https."
                                },
                                {
                                  name                        = "allow-outbound-to-business-tier"
                                  priority                    = "200"
                                  direction                   = "Outbound"
                                  access                      = "Allow"
                                  protocol                    = "tcp"
                                  source_port_range           = "*"  
                                  destination_port_range      = "8080"
                                  source_address_prefix       = "10.0.1.0/24"
                                  destination_address_prefix  = "10.0.2.0/24"
                                  description                 = "Allow outbound traffic to business tier."
                                }
                              ]
}

module "business_tier_subnet_nsg" {
  source                    = "../terraform_module/network_security_group"
  name_space                = "${var.global_name_space}"
  nsg_name                  = "business_tier_subnet_nsg"
  nsg_arm_location          = "${var.arm_location}"
  nsg_resource_group_name   = "${module.resource_group.resource_group_name}"
  nsg_tags                  = {}
  security_rules_list_count = 2
  security_rules_list       = [
                                {
                                  name                        = "allow-inbound-from-web-tier"
                                  priority                    = "100"
                                  direction                   = "Inbound"
                                  access                      = "Allow"
                                  protocol                    = "tcp"
                                  source_port_range           = "*"  
                                  destination_port_range      = "8080"
                                  source_address_prefix       = "10.0.1.0/24"
                                  destination_address_prefix  = "10.0.2.0/24"
                                  description                 = "Allow inbound traffic from web tier."
                                },
                                {
                                  name                        = "allow-outbound-to-data-tier"
                                  priority                    = "200"
                                  direction                   = "Outbound"
                                  access                      = "Allow"
                                  protocol                    = "tcp"
                                  source_port_range           = "*"  
                                  destination_port_range      = "3306"
                                  source_address_prefix       = "10.0.2.0/24"
                                  destination_address_prefix  = "10.0.3.0/24"
                                  description                 = "Allow outbound traffic to data tier."
                                }
                              ]
}

module "data_tier_subnet_nsg" {
  source                    = "../terraform_module/network_security_group"
  name_space                = "${var.global_name_space}"
  nsg_name                  = "data_tier_subnet_nsg"
  nsg_arm_location          = "${var.arm_location}"
  nsg_resource_group_name   = "${module.resource_group.resource_group_name}"
  nsg_tags                  = {}
  security_rules_list_count = 1
  security_rules_list       = [
                                {
                                  name                        = "allow-inbound-from-business-tier"
                                  priority                    = "100"
                                  direction                   = "Inbound"
                                  access                      = "Allow"
                                  protocol                    = "tcp"
                                  source_port_range           = "*"  
                                  destination_port_range      = "3306"
                                  source_address_prefix       = "10.0.2.0/24"
                                  destination_address_prefix  = "10.0.3.0/24"
                                  description                 = "Allow inbound traffic from business tier."
                                }
                              ]
}

module "virtual_network" {
  source                     = "../terraform_module"
  vnet_name                  = "three_tier_virtual_network" 
  name_space                 = "${var.global_name_space}"
  vnet_address_space         = ["10.0.0.0/16"]
  vnet_resource_group_name   = "${module.resource_group.resource_group_name}"
}

module "subnet" {
  source                                = "../terraform_module/subnet"
  virtual_network_name                  = "${module.virtual_network.virtual_network_name}"
  subnet_resource_group_name            = "${module.resource_group.resource_group_name}" 
  name_space                            = "${var.global_name_space}"
  subnet_name_list                      = ["WebTier", "BusinessTier", "DataTier"]
  subnet_address_prefix_list            = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  subnet_network_security_group_id_list = ["${module.web_tier_subnet_nsg.network_security_group_id}", "${module.business_tier_subnet_nsg.network_security_group_id}", "${module.data_tier_subnet_nsg.network_security_group_id}"]
  subnet_count                          = 3
}
