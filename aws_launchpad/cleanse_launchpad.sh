#/bin/sh

#
# This is a simple script to deal with clean up of aws_config and aws_launchpad terraform template in a scorched earth way. YMMV.
# This script needs to be kept in sync with the resources generated in aws_config and in aws_launchpad in order to ensure that we 
# are deleting only those resources created by aws_config and aws_launchpad.
# expects $1 to be the account number.

# check if the account provided is valid format
if [ $# -ne 1 ] ; then
    echo "Need one argument: 12 Digit AWS Account ID to cleanse"
    exit 1
elif ! [[ "$1" =~ ^[0-9]{12}$ ]] ; then
    echo "$1 is an invalid 12 Digit AWS Account ID"
    exit 1
fi

# destroy the aws config recorder and delivery channel - this can't be done from GUI.
aws configservice delete-configuration-recorder --configuration-recorder-name $1-config-aws
aws configservice describe-configuration-recorders
aws configservice delete-delivery-channel --delivery-channel-name $1-config-aws #Account-Monitoring-rConfigDeliveryChannel-12FR0ODTNEFWU
aws configservice describe-delivery-channels #check this again after success.

# destroy all aws config rules.
aws configservice describe-config-rules
aws configservice delete-config-rule --config-rule-name VpcsMustHaveFlowLogs
aws configservice delete-config-rule --config-rule-name VpcPeeringRouteTablesMustBeLeastAccess
aws configservice delete-config-rule --config-rule-name VpcDefaultSecurityGroupsMustRestrictAllTraffic
aws configservice delete-config-rule --config-rule-name VolumesMustBeEncrypted
aws configservice delete-config-rule --config-rule-name UsersMustNotHaveAssociatedPolicies
aws configservice delete-config-rule --config-rule-name UsersMustHaveMfaEnabled
aws configservice delete-config-rule --config-rule-name SecurityGroupsMustRestrictSshTraffic
aws configservice delete-config-rule --config-rule-name SecurityGroupsMustDisallowTcpTraffic
aws configservice delete-config-rule --config-rule-name RootAccoutMustHaveMfaEnabled
aws configservice delete-config-rule --config-rule-name ResourcesMustBeTagged
aws configservice delete-config-rule --config-rule-name KmsCustomerKeysMustBeRotated
aws configservice delete-config-rule --config-rule-name IamPoliciesMustNotContainStarStar
aws configservice delete-config-rule --config-rule-name InstancesMustUseIamRoles
aws configservice delete-config-rule --config-rule-name IamPasswordPolicyMustMeetRequirements
aws configservice delete-config-rule --config-rule-name ConfigMustBeEnabledInAllRegions
aws configservice delete-config-rule --config-rule-name CloudTrailMustBeActive
aws configservice delete-config-rule --config-rule-name CloudTrailLogsMustBeValidatedAndEncrypted
aws configservice delete-config-rule --config-rule-name CloudTrailBucketMustBeSecure
aws configservice delete-config-rule --config-rule-name S3_BUCKET_VERSIONING_ENABLED
aws configservice delete-config-rule --config-rule-name S3_BUCKET_SSL_REQUESTS_ONLY
aws configservice delete-config-rule --config-rule-name S3_BUCKET_SERVER_SIDE_ENCRYPTION_ENABLED
aws configservice delete-config-rule --config-rule-name S3_BUCKET_REPLICATION_ENABLED
aws configservice delete-config-rule --config-rule-name S3_BUCKET_PUBLIC_WRITE_PROHIBITED
aws configservice delete-config-rule --config-rule-name S3_BUCKET_PUBLIC_READ_PROHIBITED
aws configservice delete-config-rule --config-rule-name S3_BUCKET_LOGGING_ENABLED
aws configservice delete-config-rule --config-rule-name ROOT_ACCOUNT_MFA_ENABLED
aws configservice delete-config-rule --config-rule-name RESTRICTED_INCOMING_TRAFFIC
aws configservice delete-config-rule --config-rule-name REDSHIFT_CLUSTER_MAINTENANCESETTINGS_CHECK
aws configservice delete-config-rule --config-rule-name REDSHIFT_CLUSTER_CONFIGURATION_CHECK
aws configservice delete-config-rule --config-rule-name RDS_STORAGE_ENCRYPTED
aws configservice delete-config-rule --config-rule-name RDS_MULTI_AZ_SUPPORT
aws configservice delete-config-rule --config-rule-name LAMBDA_FUNCTION_SETTINGS_CHECK
aws configservice delete-config-rule --config-rule-name LAMBDA_FUNCTION_PUBLIC_ACCESS_PROHIBITED
aws configservice delete-config-rule --config-rule-name INSTANCES_IN_VPC
aws configservice delete-config-rule --config-rule-name INCOMING_SSH_DISABLED
aws configservice delete-config-rule --config-rule-name IAM_USER_NO_POLICIES_CHECK
aws configservice delete-config-rule --config-rule-name IAM_USER_GROUP_MEMBERSHIP_CHECK
aws configservice delete-config-rule --config-rule-name IAM_POLICY_BLACKLISTED_CHECK
aws configservice delete-config-rule --config-rule-name IAM_PASSWORD_POLICY
aws configservice delete-config-rule --config-rule-name IAM_GROUP_HAS_USERS_CHECK
aws configservice delete-config-rule --config-rule-name ENCRYPTED_VOLUMES
aws configservice delete-config-rule --config-rule-name ELB_CUSTOM_SECURITY_POLICY_SSL_CHECK
aws configservice delete-config-rule --config-rule-name ELB_ACM_CERTIFICATE_REQUIRED
aws configservice delete-config-rule --config-rule-name EIP_ATTACHED
aws configservice delete-config-rule --config-rule-name EC2_VOLUME_INUSE_CHECK
aws configservice delete-config-rule --config-rule-name EC2_MANAGEDINSTANCE_PLATFORM_CHECK
aws configservice delete-config-rule --config-rule-name EC2_MANAGEDINSTANCE_INVENTORY_BLACKLISTED
aws configservice delete-config-rule --config-rule-name EC2_MANAGEDINSTANCE_APPLICATIONS_REQUIRED
aws configservice delete-config-rule --config-rule-name EC2_MANAGEDINSTANCE_APPLICATIONS_BLACKLISTED
aws configservice delete-config-rule --config-rule-name EC2_INSTANCE_DETAILED_MONITORING_ENABLED
aws configservice delete-config-rule --config-rule-name EBS_OPTIMIZED_INSTANCE
aws configservice delete-config-rule --config-rule-name DYNAMODB_THROUGHPUT_LIMIT_CHECK
aws configservice delete-config-rule --config-rule-name DYNAMODB_AUTOSCALING_ENABLED
aws configservice delete-config-rule --config-rule-name DESIRED_INSTANCE_TYPE
aws configservice delete-config-rule --config-rule-name DESIRED_INSTANCE_TENANCY
aws configservice delete-config-rule --config-rule-name DB_INSTANCE_BACKUP_ENABLED
aws configservice delete-config-rule --config-rule-name CODEBUILD_PROJECT_SOURCE_REPO_URL_CHECK
aws configservice delete-config-rule --config-rule-name CODEBUILD_PROJECT_ENVVAR_AWSCRED_CHECK
aws configservice delete-config-rule --config-rule-name CLOUD_TRAIL_ENABLED
aws configservice delete-config-rule --config-rule-name CLOUDWATCH_ALARM_SETTINGS_CHECK
aws configservice delete-config-rule --config-rule-name CLOUDWATCH_ALARM_RESOURCE_CHECK
aws configservice delete-config-rule --config-rule-name CLOUDWATCH_ALARM_ACTION_CHECK
aws configservice delete-config-rule --config-rule-name CLOUDFORMATION_STACK_NOTIFICATION_CHECK
aws configservice delete-config-rule --config-rule-name AUTOSCALING_GROUP_ELB_HEALTHCHECK_REQUIRED
aws configservice delete-config-rule --config-rule-name ACM_CERTIFICATE_EXPIRATION_CHECK
aws configservice delete-config-rule --config-rule-name ATTACHED_INTERNET_GATEWAY_CHECK
aws configservice delete-config-rule --config-rule-name OPEN_SECURITY_GROUP_CHECK
aws configservice delete-config-rule --config-rule-name ALL_OPEN_INBOUND_PORTS_SECURITY_GROUP_CHECK

#make sure all the rules are in the delete state!
aws configservice describe-config-rules | grep ConfigRuleState
aws configservice describe-config-rules --output text | grep CONFIGRULES | awk '{ print $4 }' | xargs -L1 aws configservice delete-config-rule --config-rule-name
aws configservice describe-configuration-recorders --output text | grep CONFIGURATIONRECORDERS | awk '{ print $2 }' | xargs -L1 aws configservice delete-configuration-recorder --configuration-recorder-name
aws configservice describe-delivery-channels --output text | grep DELIVERYCHANNELS | awk '{ print $2 }' | xargs -L1 aws configservice delete-delivery-channel --delivery-channel-name

#clean up IAM
aws iam detach-role-policy --role-name AWS-Config-FullControl-$1-config-aws --policy-arn arn:aws:iam::$1:policy/ConfigReadWriteS3Bucket
aws iam detach-role-policy --role-name AWS-Config-FullControl-$1-config-aws --policy-arn arn:aws:iam::aws:policy/service-role/AWSConfigRole
aws iam delete-policy --policy-arn arn:aws:iam::$1:policy/ConfigReadWriteS3Bucket
aws iam delete-role --role-name AWS-Config-FullControl-$1-config-aws
aws iam detach-role-policy --role-name FEYECloudTrailRole --policy-arn arn:aws:iam::$1:policy/FEYEAccessToCloudTrail
aws iam delete-role --role-name FEYECloudTrailRole
aws iam delete-policy --policy-arn arn:aws:iam::$1:policy/FEYEAccessToCloudTrail
aws iam detach-role-policy --role-name LambdaExec-open-security-group-compliance --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-open-security-group-compliance --policy-name AWSConfigOpenSecurityGroupCheck
aws iam delete-role --role-name LambdaExec-open-security-group-compliance
aws iam detach-role-policy --role-name LambdaExec-attached-internet-gateway-compliance --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-attached-internet-gateway-compliance --policy-name AWSConfigInternetGatewayCheck
aws iam delete-role --role-name LambdaExec-attached-internet-gateway-compliance
aws iam detach-role-policy --role-name LambdaExec-lambda_audit_ec2 --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-lambda_audit_ec2 --policy-name LambdaAuditEC2Policy
aws iam delete-role --role-name LambdaExec-lambda_audit_ec2
aws iam detach-role-policy --role-name LambdaExec-security-group-inbound-ports-compliance --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-security-group-inbound-ports-compliance --policy-name AWSConfigOpenPortsSecurityGroupCheck
aws iam delete-role --role-name LambdaExec-security-group-inbound-ports-compliance

# clean up the cloud CloudTrail
aws cloudtrail delete-trail --name Cloudtrail-central-logging-rCloudTrailLoggingRemote-$1
aws iam detach-role-policy --role-name cloudtrail-role  --policy-arn arn:aws:iam::$1:policy/cloudtrail-policy
aws iam delete-policy --policy-arn arn:aws:iam::$1:policy/cloudtrail-policy
aws iam delete-role --role-name cloudtrail-role 
# disassociate and clean up kms-aliases
 aws logs disassociate-kms-key --log-group-name vpc-flow-logs
 aws logs disassociate-kms-key --log-group-name launchpad-cloudtrail-log-group
 aws kms delete-alias --alias-name alias/loggroup_kms
# clean up the cloud Cloudwatch loggroup
aws logs delete-log-group --log-group-name cloudtrail-log-group
aws logs delete-log-group --log-group-name vpc-flow-logs
# cleanup subscription filter
aws logs delete-subscription-filter --log-group-name vpc-flow-logs --filter-name subscription_filter-vpc-flow-logs
aws logs delete-subscription-filter --log-group-name launchpad-cloudtrail-log-group --filter-name subscription-filter-launchpad-cloudtrail-log-group
# clean up the cloud Cloudwatch loggroup
aws logs delete-log-group --log-group-name launchpad-cloudtrail-log-group
aws logs delete-log-group --log-group-name vpc-flow-logs
#clean up the buckets
# note: versioned buckets can only be deleted *if* using boto3 :-/
aws s3 rm s3://launchpad-tfstate-$1 --recursive
#aws s3 rb s3://launchpad-tfstate-$1 --force
aws s3 rm s3://cloudtrail-$1-logg-rcloudtrailbucket --recursive
#aws s3 rb s3://cloudtrail-$1-logg-rcloudtrailbucket --force
aws s3 rm s3://cc-cfg-central-$1 --recursive
#aws s3 rb s3://cc-cfg-central-$1 --force
aws s3 rm s3://$1-tfstate-central-logging --recursive
#aws s3 rb s3://$1-tfstate-central-logging --force
aws s3 rm s3://$1-tfstate-central-eis --recursive
#aws s3 rb s3://$1-tfstate-central-eis --force
aws s3 rm s3://cc-lambda-central-$1 --recursive
#aws s3 rb s3://cc-lambda-central-$1 --force

#clean up dynamodb tables.
aws dynamodb delete-table --table-name $1-tflock
aws dynamodb delete-table --table-name $1-tflock-central-logging

# clean up sqs queues
aws sqs delete-queue --queue-url https://sqs.us-east-1.amazonaws.com/$1/deadletter-queue-$1
aws sqs delete-queue --queue-url https://sqs.us-east-1.amazonaws.com/$1/s3-config-event-notification-queue-$1

# clean up eis iam roles
aws iam detach-role-policy --role-name EIS_AWS_Read --policy-arn arn:aws:iam::$1:policy/EnterpriseSecurityReadOnlyAccessAddendum
aws iam detach-role-policy --role-name AWS_$1_EISRead --policy-arn arn:aws:iam::$1:policy/EnterpriseSecurityAssumeRolePolicy
aws iam detach-role-policy --role-name AWS_$1_EISBreakGlass --policy-arn arn:aws:iam::$1:policy/EnterpriseSecurityAssumeRolePolicy
aws iam detach-role-policy --role-name EIS_AWS_Read --policy-arn arn:aws:iam::aws:policy/ReadOnlyAccess
aws iam detach-role-policy --role-name EIS_AWS_BreakGlass --policy-arn arn:aws:iam::aws:policy/PowerUserAccess
aws iam delete-policy --policy-arn arn:aws:iam::$1:policy/EnterpriseSecurityReadOnlyAccessAddendum
aws iam delete-policy --policy-arn arn:aws:iam::$1:policy/EnterpriseSecurityAssumeRolePolicy
aws iam delete-role --role-name AWS_$1_EISRead
aws iam delete-role --role-name AWS_$1_EISBreakGlass
aws iam delete-role --role-name EIS_AWS_Read
aws iam delete-role --role-name EIS_AWS_BreakGlass

# clean up lambda
aws lambda delete-function --function-name attached-internet-gateway-compliance
aws lambda delete-function --function-name open-security-group-compliance
# clean up the Kinesis firehose central logging

# delete firehose delivery role
aws iam delete-role-policy --role-name firehose-$1-central-logging_firehose_delivery_role --policy-name firehose-$1-central-logging_firehose_delivery_role_policy
aws iam delete-role --role-name firehose-$1-central-logging_firehose_delivery_role

# delete lambda role
aws iam delete-role-policy --role-name firehose-$1-central-logging_lambda_firehose_role --policy-name Policy1
aws iam detach-role-policy --role-name firehose-$1-central-logging_lambda_firehose_role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role --role-name firehose-$1-central-logging_lambda_firehose_role

# delete lambda function
aws lambda delete-function --function-name firehose_lambda_processor-central-logging

# delete s3 bucket
aws s3 rm s3://firehose-$1-central-logging-bucket-central-logging --recursive

# delete log group 
aws logs delete-log-stream --log-group-name /aws/kinesisfirehose/firehose-$1-central-logging --log-stream-name SplunkDestination
aws logs delete-log-group --log-group-name /aws/kinesisfirehose/firehose-$1-central-logging 

aws logs delete-log-stream --log-group-name /aws/lambda/firehose-$1-central-logging_firehose_lambda_processor --log-stream-name lambda-firehose-$1-central-logging
aws logs delete-log-group --log-group-name /aws/lambda/firehose-$1-central-logging_firehose_lambda_processor

# delete Kinesis firehose delivery stream
aws firehose delete-delivery-stream --delivery-stream-name firehose-$1-central-logging

# delete destination
aws iam delete-role-policy --role-name awslogs-destination-splunk_role --policy-name awslogs-destination-splunk_permission_policy
aws iam delete-role --role-name awslogs-destination-splunk_role
aws logs delete-destination --destination-name awslogs-destination-splunk

# clean up lambda
aws lambda delete-function --function-name attached-internet-gateway-compliance
aws lambda delete-function --function-name open-security-group-compliance
aws lambda delete-function --function-name lambda_audit_ec2

# Clean up sns
aws sns delete-topic --topic-arn arn:aws:sns:us-east-1:$1:launchpad_lambda_topic

# delete kinesis data stream
aws kinesis delete-stream --stream-name kinesis-data-stream-$1-central-logging

# delete kinesis data stream role
aws iam delete-role-policy --role-name kinesis-data-stream-$1-central-logging_role --policy-name kinesis-data-stream-$1-central-logging_role_policy
aws iam delete-role --role-name kinesis-data-stream-$1-central-logging_role

# clean up the lambda logs processing resources

# delete lambda function
aws lambda delete-function --function-name lambda-logs-processor-function

# delete lambda role
aws iam delete-role-policy --role-name LambdaExec-lambda-logs-processor-function --policy-name lambda-logs-processor-function
aws iam detach-role-policy --role-name LambdaExec-lambda-logs-processor-function --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role --role-name LambdaExec-lambda-logs-processor-function

# delete log group 
aws logs delete-log-group --log-group-name /aws/lambda/lambda-logs-processor-function

# delete sqs queue
aws sqs delete-queue --queue-url https://queue.amazonaws.com/$1/lambda-logs-processor-dead-letter-queue

# disassociate and clean up kms-aliases
 aws logs disassociate-kms-key --log-group-name /aws/lambda/lambda-logs-processor-function
 aws kms delete-alias --alias-name alias/lambda-logs-processor-log-group-key

# delete s3 bucket
aws s3 rm s3://lambda-logs-processor-bucket-$1 --recursive


