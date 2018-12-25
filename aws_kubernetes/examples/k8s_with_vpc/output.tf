output "vpc_properties" {
  value = {
    vpc_name              = "${module.vpc.vpc_name}"
    vpc_id                = "${module.vpc.vpc_id}"
    vpc_cidr              = "${module.vpc.vpc_cidr_block}"
    vpc_public_subnet_az  = "${module.vpc.vpc_public_subnets_az}"
    vpc_private_subnet_az = "${module.vpc.vpc_private_subnets_az}"
    public_cidr           = "${module.vpc.vpc_public_subnet_cidrs}"
    private_cidr          = "${module.vpc.vpc_private_subnet_cidrs}"
  }
}

output "egress_proxy_properties" {
  value = {
    egress_proxy_url                              = "${module.egress_proxy.egress_proxy_url}"
    egress_proxy_security_group                   = "${module.egress_proxy.egress_proxy_security_group}"
    egress_proxy_s3_bucket_with_proxy_info        = "${module.egress_proxy.egress_proxy_s3_bucket_with_proxy_info}"
    egress_proxy_codedeploy_deployment_app_name   = "${module.egress_proxy.egress_proxy_codedeploy_deployment_app_name}"
    egress_proxy_codedeploy_deployment_group_name = "${module.egress_proxy.egress_proxy_codedeploy_deployment_group_name}"
  }
}

output "k8s_properties" {
  value = {
    cluster_name              = "${module.k8s.k8s_cluster_name}"
    master_security_group_ids = "${module.k8s.k8s_master_security_group_ids}"
    masters_role_arn          = "${module.k8s.k8s_masters_role_arn}"
    masters_role_name         = "${module.k8s.k8s_masters_role_name}"
    k8s_subnet_ids            = "${module.vpc.vpc_private_subnet_ids}"
    node_security_group_ids   = "${module.k8s.k8s_node_security_group_ids}"
    nodes_role_arn            = "${module.k8s.k8s_nodes_role_arn}"
    nodes_role_name           = "${module.k8s.k8s_nodes_role_name}"
  }
}

output "flow_logs_properties" {
  value = {
    flow_log_id                = "${module.flow_logs.flow_log_id}"
    flow_log_role_arn          = "${module.flow_logs.flow_log_role_arn}"
    flow_log_cw_log_group_name = "${module.flow_logs.flow_log_cw_log_group_name}"
    flow_log_traffic_type      = "${module.flow_logs.flow_log_traffic_type}"
  }
}
