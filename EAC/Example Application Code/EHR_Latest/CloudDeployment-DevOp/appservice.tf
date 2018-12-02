module "app_service_uradvisor" {
	source					= "terraform_module/azure_app_service"
	plan_name				= "${local.namespaced_project_name}"
	site_name				= "${local.namespaced_project_name}"
	rg_name					= "${module.resource_group.name}"
	rg_location				= "${var.LOCATION}"
	sqlserver_name			= "${module.sql_server.server_fqdn}"
	sqldb_name			    = "${module.sql_database.name[0]}"
	sql_login				= "${var.SQL_LOGIN}"
	sql_pw					= "${var.SQL_PASSWORD}"
}
module "app_service_uradvisor-loggging" {
	source					= "terraform_module/azure_app_service"
	plan_name				= "${local.namespaced_project_name}-logging"
	site_name				= "${local.namespaced_project_name}-logging"
	rg_name					= "${module.resource_group.name}"
	rg_location				= "${var.LOCATION}"
	sqlserver_name			= "${module.sql_server.server_fqdn}"
	sqldb_name			    = "${module.sql_database.name[0]}"
	sql_login				= "${var.SQL_LOGIN}"
	sql_pw					= "${var.SQL_PASSWORD}"
}