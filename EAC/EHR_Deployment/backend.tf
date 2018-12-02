terraform {
    backend "azurerm" {
        storage_account_name = "b4b8d5dcnonprod"
        container_name       = "tfstate"
        key                  = "uradvisor.terraform.tfstate"
        resource_group_name = "terraform-state"
    }
}