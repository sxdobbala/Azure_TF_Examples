module "generate-random-name" { 
   source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.1.0" 
   //Note: Once a random name has been generated, it is not regenerated unless the state is "taint"ed. This could be useful for avoiding naming conflicts
   //       but also requires that additional step if we are trying to force a regeneration (eg. for migrations or transient environments)
}

locals { 
    name_space = "${terraform.workspace}" 
    //TODO: Implement and use global tags
    global_tags = {environment="${terraform.workspace}", project="${var.PROJECT_NAME}" }

    //ARM templates don't support namespaces, so implement it manually for now
    namespaced_project_name = "${format("%s-%s", var.PROJECT_NAME, local.name_space)}"
}

module "resource_group"{
    source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//terraform_module"
    providers = {
        "azurerm"= "azurerm"
    }
    resource_group_name = "${var.PROJECT_NAME}"
    arm_location = "${var.LOCATION}"
    name_space = "${local.name_space}"
    //Syntax is wrong- need to fix later
    //global_tags = ${local.global_tags}
}



//TODO: Once we have a working module, need to re-write this
resource "azurerm_app_service_plan" "siteplan"{
    resource_group_name = "${module.resource_group.resource_group_name}"
    name = "${local.namespaced_project_name}"
    location = "${var.LOCATION}"
    kind = "app"
    sku = {
        size = "S1"
        tier = "Standard"
    }
}

resource "azurerm_app_service" "site" {
    resource_group_name = "${module.resource_group.resource_group_name}"
    name = "${local.namespaced_project_name}"
    location = "${var.LOCATION}"
    app_service_plan_id = "${azurerm_app_service_plan.siteplan.id}"
    https_only = "true"
    connection_string = {
        name = "Master"
        type = "SqlAzure"
        value = "Encrypt=True; Data Source=${azurerm_sql_server.sql.fully_qualified_domain_name};Initial Catalog=${azurerm_sql_database.sql.name};User Id=${var.SQL_LOGIN};Password=${var.SQL_PASSWORD}"
    }
}

resource "azurerm_sql_server" "sql"{
    resource_group_name = "${module.resource_group.resource_group_name}"
    name = "${lower(local.namespaced_project_name)}${lower(var.USE_TEMPORARY_DATABASE == "true" ? "-${module.generate-random-name.name}" :"")}"
    location = "${var.LOCATION}"
    
    version = "12.0"
    administrator_login = "${var.SQL_LOGIN}"
    administrator_login_password = "${var.SQL_PASSWORD}"
}

resource "azurerm_sql_database" "sql" {
    resource_group_name = "${module.resource_group.resource_group_name}"
    location = "${var.LOCATION}"
    //The namespace should not be applied to the db name
    name = "${var.PROJECT_NAME}"
    server_name = "${azurerm_sql_server.sql.name}"
    requested_service_objective_name = "S1"
}

//Allow connections from any azure resource (including those not owned by Optum)
resource "azurerm_sql_firewall_rule" "allowazure" {
  name                = "allow_azure"
  resource_group_name = "${module.resource_group.resource_group_name}"
  server_name         = "${azurerm_sql_server.sql.name}"
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}


//Modules below either don't work or aren't implemented yet
/*
module "app_service_plan" "siteplan"{
    source = "git::https://github.optum.com/CommercialCloud-EAC/azure_app_service//terraform_module/plan?ref=v1.0"
    app_service_plan_resource_group_name = "${module.resource_group.name}"
    app_service_plan_name = "${var.PROJECT_NAME}"
    arm_location = "${var.LOCATION}"
    name_space = "${local.name_space}"
    //global_tags = ${local.global_tags}
}
*/
/*
module "app_service_site" "site"{
    
}
*/
/*
module "sql_server" "sqlvm"{

}
*/
/*
module "sql_database" "sqldb"{

}
*/