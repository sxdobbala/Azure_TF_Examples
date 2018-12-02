variable "name" {
  description = "Name of application"
}

variable "name_space" {
  description = "Namespace for applicaiton"
  default = ""
}

variable "env" {
  description = "Environment app is being deployed in"
}

variable "location" {
  description = "Location to deploy application"
  default = "centralus"
}

variable "rg_name" {
  description = "Name of resource group to deploy application"
}

variable "app_service_plan_id" {
  description = "ID of the ASE to deploy application"
}

variable "app_slots" {
  description = "Slots for the application"
  type = "list"
}

variable "active_slot" {
  description = "Active slot for application"
}