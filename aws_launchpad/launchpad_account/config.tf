module "config-initialize" {
  source               = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/initialize_existing_s3?ref=v1.4.1"
  config_name          = "${var.config_name}"
  config_s3_bucket     = "${var.config_s3_bucket}"
  config_force_destroy = true
  global_tags          = "${var.global_tags}"
  config_tags          = "${var.config_tags}"
}

module "config-managed-rule-autoscaling_group_elb_healthcheck_required" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "AUTOSCALING_GROUP_ELB_HEALTHCHECK_REQUIRED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-desired_instance_tenancy" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "DESIRED_INSTANCE_TENANCY"
      config_rule_input_parameters = "\"tenancy\":\"DEFAULT\""
    },
  ]
}

module "config-managed-rule-ec2_instance_detailed_monitoring_enabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "EC2_INSTANCE_DETAILED_MONITORING_ENABLED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-instances_in_vpc" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "INSTANCES_IN_VPC"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-ec2_managedinstance_inventory_blacklisted" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "EC2_MANAGEDINSTANCE_INVENTORY_BLACKLISTED"
      config_rule_input_parameters = "\"inventoryNames\":\"AWS:Network\""
    },
  ]
}

module "config-managed-rule-ec2_volume_inuse_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "EC2_VOLUME_INUSE_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-eip_attached" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "EIP_ATTACHED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-encrypted_volumes" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "ENCRYPTED_VOLUMES"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-elb_acm_certificate_required" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "ELB_ACM_CERTIFICATE_REQUIRED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-elb_custom_security_policy_ssl_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "ELB_CUSTOM_SECURITY_POLICY_SSL_CHECK"
      config_rule_input_parameters = "\"sslProtocolsAndCiphers\":\"Protocol-TLSv1.2\""
    },
  ]
}

module "config-managed-rule-restricted_incoming_traffic" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "RESTRICTED_INCOMING_TRAFFIC"
      config_rule_input_parameters = "\"blockedPort1\":\"3389\", \"blockedPort2\":\"3306\", \"blockedPort3\":\"1433\", \"blockedPort4\":\"22\""
    },
  ]
}

module "config-managed-rule-incoming_ssh_disabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "INCOMING_SSH_DISABLED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-db_instance_backup_enabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "DB_INSTANCE_BACKUP_ENABLED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-rds_storage_encrypted" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "RDS_STORAGE_ENCRYPTED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-redshift_cluster_configuration_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "REDSHIFT_CLUSTER_CONFIGURATION_CHECK"
      config_rule_input_parameters = "\"clusterDbEncrypted\":\"true\", \"loggingEnabled\":\"true\", \"nodeTypes\":\"dc1.large\""
    },
  ]
}

module "config-managed-rule-redshift_cluster_maintenancesettings_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "REDSHIFT_CLUSTER_MAINTENANCESETTINGS_CHECK"
      config_rule_input_parameters = "\"allowVersionUpgrade\":\"true\", \"automatedSnapshotRetentionPeriod\":\"1\""
    },
  ]
}

module "config-managed-rule-cloudwatch_alarm_settings_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "CLOUDWATCH_ALARM_SETTINGS_CHECK"
      config_rule_input_parameters = "\"metricName\":\"CPUUtilization\", \"period\":\"300\""
    },
  ]
}

module "config-managed-rule-codebuild_project_envar_awscred_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "CODEBUILD_PROJECT_ENVVAR_AWSCRED_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-codebuild_project_source_repo_url_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "CODEBUILD_PROJECT_SOURCE_REPO_URL_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-iam_group_has_users_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "IAM_GROUP_HAS_USERS_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-iam_user_group_membership_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "IAM_USER_GROUP_MEMBERSHIP_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-iam_user_no_policies_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "IAM_USER_NO_POLICIES_CHECK"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-s3_bucket_public_read_prohibited" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "S3_BUCKET_PUBLIC_READ_PROHIBITED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-s3_bucket_public_write_prohibited" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "S3_BUCKET_PUBLIC_WRITE_PROHIBITED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-s3_bucket_ssl_requests_only" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "S3_BUCKET_SSL_REQUESTS_ONLY"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-s3_bucket_logging_enabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "S3_BUCKET_LOGGING_ENABLED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-lambda_function_public_access_prohibited" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_rule_count = 1

  config_aws_managed_rule_activate_list = [
    {
      config_rule_name             = "LAMBDA_FUNCTION_PUBLIC_ACCESS_PROHIBITED"
      config_rule_input_parameters = ""
    },
  ]
}

module "config-managed-rule-cloud_trail_enabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_periodic_rule_count = 1

  config_aws_managed_periodic_rule_activate_list = [
    {
      config_rule_name                        = "CLOUD_TRAIL_ENABLED"
      config_rule_maximum_execution_frequency = "TwentyFour_Hours"
      config_rule_input_parameters            = ""
    },
  ]
}

module "config-managed-rule-dynamodb_throughput_limit_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_periodic_rule_count = 1

  config_aws_managed_periodic_rule_activate_list = [
    {
      config_rule_name                        = "DYNAMODB_THROUGHPUT_LIMIT_CHECK"
      config_rule_maximum_execution_frequency = "TwentyFour_Hours"
      config_rule_input_parameters            = "\"accountRCUThresholdPercentage\":\"80\", \"accountWCUThresholdPercentage\":\"80\""
    },
  ]
}

module "config-managed-rule-iam_password_policy" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_periodic_rule_count = 1

  config_aws_managed_periodic_rule_activate_list = [
    {
      config_rule_name                        = "IAM_PASSWORD_POLICY"
      config_rule_maximum_execution_frequency = "TwentyFour_Hours"
      config_rule_input_parameters            = "\"RequireUppercaseCharacters\":\"true\", \"RequireLowercaseCharacters\":\"true\", \"RequireSymbols\":\"true\", \"RequireNumbers\":\"true\", \"MinimumPasswordLength\":\"14\", \"PasswordReusePrevention\":\"24\", \"MaxPasswordAge\":\"90\""
    },
  ]
}

module "config-managed-rule-root_account_mfa_enabled" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Non-Periodic Rules
  config_aws_managed_periodic_rule_count = 1

  config_aws_managed_periodic_rule_activate_list = [
    {
      config_rule_name                        = "ROOT_ACCOUNT_MFA_ENABLED"
      config_rule_maximum_execution_frequency = "TwentyFour_Hours"
      config_rule_input_parameters            = ""
    },
  ]
}

module "config-managed-rule-acm_certificate_expiration_check" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/aws_managed_rule?ref=v1.4.1"

  config_is_enabled = "${module.config-initialize.config_recorder_is_enabled}"

  # Managed Periodic Scoped Rules
  config_aws_managed_periodic_scoped_rule_count = 1

  config_aws_managed_periodic_scoped_rule_activate_list = [
    {
      config_rule_name                        = "ACM_CERTIFICATE_EXPIRATION_CHECK"
      config_rule_maximum_execution_frequency = "TwentyFour_Hours"
      config_rule_input_parameters            = "\"daysToExpiration\":\"14\""
    },
  ]
}

# Additional policies required for the Internet Gateway Compliance Lambda Function to execute properly
data "aws_iam_policy_document" "lambda_attached_internet_gateway_compliance_exec_policy" {
  statement {
    effect = "Allow"

    actions = ["config:Put*",
      "config:Get*",
      "config:List*",
      "config:Describe*",
    ]

    resources = ["*"]
  }
}

# Create the custom Config Rule to check Internet Gateway attachments to VPCs
module "config_custom_internet_gateway_rule" {
  source            = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/custom_lambda_rule?ref=v1.4.1"
  config_is_enabled = true
  config_has_scopes = true

  config_custom_compliance_types                = ["AWS::EC2::VPC"]
  config_custom_rule_name                       = "ATTACHED_INTERNET_GATEWAY_CHECK"
  config_custom_rule_description                = "Attached Internet Gateway Compliance Rule: Flags VPCs that have an Internet Gateway attached."
  config_custom_rule_lambda_function_name       = "attached-internet-gateway-compliance"
  config_custom_rule_lambda_filename            = "lambda_functions/attached_internet_gateway_compliance.py"
  config_custom_rule_source_detail_message_type = "ConfigurationItemChangeNotification"
  config_custom_rule_exec_custom_policy_count   = 1

  config_custom_rule_exec_custom_policy = [
    {
      role_custom_inline_policy_name = "AWSConfigInternetGatewayCheck"
      role_custom_inline_policy      = "${data.aws_iam_policy_document.lambda_attached_internet_gateway_compliance_exec_policy.json}"
    },
  ]
}

# Additional policies required for the Open Security Group Compliance Lambda Function to execute properly
data "aws_iam_policy_document" "lambda_open_security_group_compliance_exec_policy" {
  statement {
    effect = "Allow"

    actions = ["config:Put*",
      "config:Get*",
      "config:List*",
      "config:Describe*",
      "ec2:DescribeSecurityGroups",
    ]

    resources = ["*"]
  }
}

# Create the custom Config Rule to check if a Security Group has an open Cidr
module "config_custom_open_security_group_rule" {
  source            = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/custom_lambda_rule?ref=v1.4.1"
  config_is_enabled = true
  config_has_scopes = true

  config_custom_compliance_types                = ["AWS::EC2::SecurityGroup"]
  config_custom_rule_name                       = "OPEN_SECURITY_GROUP_CHECK"
  config_custom_rule_description                = "Open Security Group Compliance Rule: Checks if a Security Group has a Cidr of IPv4 0.0.0.0/0 or IPv6 ::/0"
  config_custom_rule_lambda_function_name       = "open-security-group-compliance"
  config_custom_rule_lambda_filename            = "lambda_functions/open_security_group_compliance.py"
  config_custom_rule_source_detail_message_type = "ConfigurationItemChangeNotification"
  config_custom_rule_exec_custom_policy_count   = 1

  config_custom_rule_exec_custom_policy = [
    {
      role_custom_inline_policy_name = "AWSConfigOpenSecurityGroupCheck"
      role_custom_inline_policy      = "${data.aws_iam_policy_document.lambda_attached_internet_gateway_compliance_exec_policy.json}"
    },
  ]
}

# Create the custom Config Rule to check if a Security Group has all inbound TCP port open
# If more restrictive port range is desired then a change to the RANGELIMIT variable is needed. 
data "aws_iam_policy_document" "lambda_all_open_ports_security_group_compliance_exec_policy" {
  statement {
    effect = "Allow"

    actions = [
      "config:Put*",
      "sts:Get*"
    ]

    resources = ["*"]
    }

    statement {
    effect = "Allow"
    actions = [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
    ]
    resources = [ "arn:aws:logs:*:*:*"]
    }
}
module "config_custom_all_open_inbound_security_group_rule" {
  source            = "git::https://github.optum.com/CommercialCloud-EAC/aws_config.git//terraform_module/custom_lambda_rule?ref=v1.4.1"
  config_is_enabled = true
  config_has_scopes = true

  config_custom_compliance_types                = ["AWS::EC2::SecurityGroup"]
  config_custom_rule_name                       = "ALL_OPEN_INBOUND_PORTS_SECURITY_GROUP_CHECK"
  config_custom_rule_description                = "Open Inbound Ports Security Group Compliance Rule: Checks if a Security Group has all inbound TCP ports open"
  config_custom_rule_lambda_function_name       = "security-group-inbound-ports-compliance"
  config_custom_rule_lambda_filename            = "lambda_functions/security_group_ports_config.py"
  config_custom_rule_source_detail_message_type = "ConfigurationItemChangeNotification"
  config_custom_rule_exec_custom_policy_count   = 1
  config_custom_rule_input_parameters   = {
       LOGLEVEL  = "${var.env_loglevel}" 
      CONFIGTEST = "${var.env_config_put_test_flag}" 
      RANGELIMIT = "${var.env_rangelimit}" 
      }
  config_custom_rule_exec_custom_policy = [
    {
      role_custom_inline_policy_name = "AWSConfigOpenPortsSecurityGroupCheck"
      role_custom_inline_policy      = "${data.aws_iam_policy_document.lambda_all_open_ports_security_group_compliance_exec_policy.json}"
    },
  ]
  global_tags = "${var.global_tags}"
}