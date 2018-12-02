locals {
  # name must contain only alphanumeric characters and dashes, and not start with a number
  app_name = "${format("%s", length(var.name_space) == 0 ?
                        format("%s-%s", var.name, var.env)
                        :
                        format("%s-%s-%s", var.name, var.env, var.name_space)
                        )
                    }"
}

resource "azurerm_app_service" "webapp" {
  name = "${local.app_name}"
  location = "${var.location}"
  resource_group_name = "${var.rg_name}"
  app_service_plan_id = "${var.app_service_plan_id}"
}

resource "azurerm_app_service_slot" "slot" {
  count = "${length(var.app_slots)}"
  name = "${var.app_slots[count.index]}"
  app_service_name = "${azurerm_app_service.webapp.name}"
  resource_group_name = "${var.rg_name}"
  location = "${var.location}"
  app_service_plan_id = "${var.app_service_plan_id}"
}

resource "azurerm_app_service_active_slot" "active_slot" {
  app_service_name = "${local.app_name}"
  app_service_slot_name = "${var.active_slot}"
  resource_group_name = "${var.rg_name}"
}