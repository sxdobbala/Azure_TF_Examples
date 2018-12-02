# This template file is here to handle interpolation so that the route resource doesn't have to .
# Since data is passed in as a list of maps any output values that are passed in would cause
# the code to break if it were passed directly to the route resource. (This is just one of 
# terraforms quirks). This turns the list of maps into a list of simple strings that can then
# be broken up and accessed individually at a later point in time
data "template_file" "map_interpolation" {
  count    = "${var.route_count}"
  template = "$${name}|$${address_prefix}|$${next_hop_type}|$${next_hop_in_ip_address}"

  vars {
    name                   = "${lookup(var.route_list[count.index], "name")}"
    address_prefix         = "${lookup(var.route_list[count.index], "address_prefix")}"
    next_hop_type          = "${lookup(var.route_list[count.index], "next_hop_type")}"
    next_hop_in_ip_address = "${lookup(var.route_list[count.index], "next_hop_in_ip_address", "")}"
  }
}

locals {
  # Re-assigning data to make code more readable
  route_list = "${data.template_file.map_interpolation.*.rendered}"

  version_tag = {
    "cc-eac_azure_virtual_network" = "v2.0.0"
  }
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

resource "azurerm_route_table" "route_table" {
  name                = "${module.namespace.name}"
  location            = "${var.location}"
  resource_group_name = "${var.resource_group_name}"
  tags                = "${merge(var.tags, var.global_tags, local.version_tag)}"
}

# Each of the split statements you see below split the string that is exported 
# by the template_file at the beginning of the script. It then accesses the 
# specified element from the resulting list.
resource "azurerm_route" "route" {
  count                  = "${var.route_count}"
  name                   = "${element(split("|", element(local.route_list, count.index)), 0)}"
  resource_group_name    = "${var.resource_group_name}"
  route_table_name       = "${azurerm_route_table.route_table.name}"
  address_prefix         = "${element(split("|", element(local.route_list, count.index)), 1)}"
  next_hop_type          = "${element(split("|", element(local.route_list, count.index)), 2)}"
  next_hop_in_ip_address = "${element(split("|", element(local.route_list, count.index)), 3)}"
}
