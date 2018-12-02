output "hostname" { 
  value = "${module.app_service_uradvisor.hostname}" 
} 


output "servicename"{
    value = "${local.namespaced_project_name}"
}
