output "cloudtrail_id" {
  value = "${module.cloudtrail.id}"
}

output "config_name" {
  value = "${module.config-initialize.config_name}"
}

output "config_role_arn" {
  value = "${module.config-initialize.config_role_arn}"
}

output "config_recorder_id" {
  value = "${module.config-initialize.config_recorder_id}"
}

output "config_recorder_is_enabled" {
  value = "${module.config-initialize.config_recorder_is_enabled}"
}

output "config_delivery_channel_id" {
  value = "${module.config-initialize.config_delivery_channel_id}"
}

output "config_delivery_channel_bucket_name" {
  value = "${module.config-initialize.config_delivery_channel_bucket_name}"
}

output "config_aws_managed_rule_arns" {
  value = "${join(",", 
    "${list(
      module.config-managed-rule-autoscaling_group_elb_healthcheck_required.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-desired_instance_tenancy.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-ec2_instance_detailed_monitoring_enabled.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-instances_in_vpc.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-ec2_managedinstance_inventory_blacklisted.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-ec2_volume_inuse_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-eip_attached.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-encrypted_volumes.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-elb_acm_certificate_required.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-elb_custom_security_policy_ssl_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-restricted_incoming_traffic.aws_config_aws_managed_rule_arns, 
      module.config-managed-rule-incoming_ssh_disabled.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-db_instance_backup_enabled.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-rds_storage_encrypted.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-redshift_cluster_configuration_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-redshift_cluster_maintenancesettings_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-cloudwatch_alarm_settings_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-codebuild_project_envar_awscred_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-codebuild_project_source_repo_url_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-iam_group_has_users_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-iam_user_group_membership_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-iam_user_no_policies_check.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-s3_bucket_public_read_prohibited.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-s3_bucket_public_write_prohibited.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-s3_bucket_ssl_requests_only.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-s3_bucket_logging_enabled.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-lambda_function_public_access_prohibited.aws_config_aws_managed_rule_arns,
      module.config-managed-rule-cloud_trail_enabled.aws_config_aws_managed_periodic_rule_arns,
      module.config-managed-rule-dynamodb_throughput_limit_check.aws_config_aws_managed_periodic_rule_arns,
      module.config-managed-rule-iam_password_policy.aws_config_aws_managed_periodic_rule_arns,
      module.config-managed-rule-root_account_mfa_enabled.aws_config_aws_managed_periodic_rule_arns,
      module.config-managed-rule-acm_certificate_expiration_check.aws_config_aws_managed_periodic_scoped_rule_arns,
    )}")}"
}

output "config_aws_managed_rule_ids" {
  value = "${join(",", 
    "${list(
      module.config-managed-rule-autoscaling_group_elb_healthcheck_required.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-desired_instance_tenancy.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-ec2_instance_detailed_monitoring_enabled.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-instances_in_vpc.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-ec2_managedinstance_inventory_blacklisted.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-ec2_volume_inuse_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-eip_attached.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-encrypted_volumes.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-elb_acm_certificate_required.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-elb_custom_security_policy_ssl_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-restricted_incoming_traffic.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-incoming_ssh_disabled.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-db_instance_backup_enabled.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-rds_storage_encrypted.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-redshift_cluster_configuration_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-redshift_cluster_maintenancesettings_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-cloudwatch_alarm_settings_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-codebuild_project_envar_awscred_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-codebuild_project_source_repo_url_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-iam_group_has_users_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-iam_user_group_membership_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-iam_user_no_policies_check.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-s3_bucket_public_read_prohibited.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-s3_bucket_public_write_prohibited.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-s3_bucket_ssl_requests_only.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-s3_bucket_logging_enabled.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-lambda_function_public_access_prohibited.aws_config_aws_managed_rule_ids,
      module.config-managed-rule-cloud_trail_enabled.aws_config_aws_managed_periodic_rule_ids,
      module.config-managed-rule-dynamodb_throughput_limit_check.aws_config_aws_managed_periodic_rule_ids,
      module.config-managed-rule-iam_password_policy.aws_config_aws_managed_periodic_rule_ids,
      module.config-managed-rule-root_account_mfa_enabled.aws_config_aws_managed_periodic_rule_ids,
      module.config-managed-rule-acm_certificate_expiration_check.aws_config_aws_managed_periodic_scoped_rule_ids,
    )}")}"
}

# Custom config rules - AWS Lambda targets below.  Update the list to be included into the automated testing.
output "config_aws_custom_scoped_rule_ids" {
  value = "${module.config_custom_internet_gateway_rule.config_custom_rule_id},${module.config_custom_open_security_group_rule.config_custom_rule_id},${module.config_custom_all_open_inbound_security_group_rule.config_custom_rule_id}"
}

output "config_aws_custom_scoped_rule_arns" {
  value = "${module.config_custom_internet_gateway_rule.config_custom_rule_arn},${module.config_custom_open_security_group_rule.config_custom_rule_arn},${module.config_custom_all_open_inbound_security_group_rule.config_custom_rule_arn}"
}

# Member security account outputs
output "member_security_ro_role" {
  value = "${module.eis-member-ro-access-role.role_name}"
}

output "member_security_ro_managed_role_policies" {
  value = ["${module.eis-member-ro-access-role.federated_principals_managed_role_policies}"]
}

output "member_security_bg_role" {
  value = "${module.eis-member-bg-access-role.role_name}"
}

output "member_security_bg_managed_role_policies" {
  value = ["${module.eis-member-bg-access-role.federated_principals_managed_role_policies}"]
}

output "flow_log_group_name" {
  value = "${module.vpc_flow_loggroup.name}"
}

output "flow_loggroup_arn" {
  value = "${module.vpc_flow_loggroup.arn}"
}

output "flow_log_subscription_filter_name" {
  value = "${module.flow_log_subscription_filter.name}"
}

output "flow_log_subscription_filter_id" {
  value = "${module.flow_log_subscription_filter.id}"
}

output "cloudtrail_loggroup_name" {
  value = "${module.cloudtrail_loggroup.name}"
}

output "cloudtrail_loggroup_arn" {
  value = "${module.cloudtrail_loggroup.arn}"
}

output "cloudtrail_subscription_filter_name" {
  value = "${module.cloudtrail_subscription_filter.name}"
}

output "cloudtrail_subscription_filter_id" {
  value = "${module.cloudtrail_subscription_filter.id}"
}

 output "kms_id" {
   value = "${module.create_kms_key_with_alias.id}"
 }


 output "kms_arn" {
   value = "${module.create_kms_key_with_alias.arn}"
 }


 output "kms_alias_name" {
   value = "${module.create_kms_key_with_alias.alias_name}"
 }


 output "kms_alias_arn" {
   value = "${module.create_kms_key_with_alias.alias_arn}"
 }