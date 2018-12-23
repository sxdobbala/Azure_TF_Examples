# AWS Launchpad 1.5.6
## Features
Creates a custom AWS config rule that checks security groups for use of inbound networking port ranges. This is defined as security risk by enterprise security. Violations are logged to Splunk for Security Incident Response follow up. This additive change poses no risk to the account, it is audit only. Please see [this documentation](https://commercialcloud.optum.com/docs/HOWTO-RemovePortsFromSG.html) for port remediation steps. Changes to the range of ports can be made in the "variables.tf" with in the launchpad_account subdirectory. US501575.
- Updated README.md with detail on individual AWS Config rules.


# AWS Launchpad 1.5.5
## Features
- Implemented KMS Key id creation for log groups and attached the same to VPC Flow log group and CloudTrail log group.
- Cloudwatch log group and CloudTrail log groups are enabled with encryption.
- Organisation role is enclosed on policy creation for KMS and aws log service principle take encrypt, decrypt, etc. to allow those action to perform.
- Enable Data Encryption on Lambda's Dead Letter Queue (SQS) and its log group.
- Add Kinesis Data Stream for Central logging solution to enable encryption.
- This helps in streaming of data. It also enables encryption of the data streamed.


# AWS Launchpad 1.5.4
## Features
- Updated AWS config managed rules to leverage the RESTRICTED_INCOMING_TRAFFIC rule, watching for security groups containin ports 22, 3389, 1433 and 3306 inbound. US494227
- Removed managed config rule IAM_POLICY_BLACKLISTED_CHECK per defect DE169374


# AWS Launchpad 1.5.3
## Features
- Added the Kinesis Firehose to stream log data to splunk.
- All launchpad accounts are allowed to put subscription filter to the logdestination in CentralManagementAccount.
- This pipeline handles log data from VPC flow logs and cloudTrail logs via the cloudwatch log group configured in individual launchpad accounts.
- Logs from cloudwatch loggroup are sent to logdestination in CentralManagementAccount through subscription filter.  
- This log events that are failed to be processed or delivered are stored in a s3 bucket for further analysis.
- Added Lambda function to process the log data coming from Kinesis Firehose and store it to a separate s3 bucket.
- This function will process the logs and store them in the format for **"Account-Owner-Id"/"Log-Group-Type"/"Logs-File"**
- Name of the log file will be according to the **Date(yyyy-mm-dd)**. All the logs of that day will be stored in that file.
- In case of failure of lambda function, a dead letter queue(SQS) will be triggererd to store the failure messages.


# AWS Launchpad 1.5.2

## Features

- Added CloudWatch event scheduler to trigger the invoker lambda.
- Add an SNS Topic in the Central Management Account for the Lambda Invoker and Lambda Audit EC2.

# AWS Launchpad 1.5.1

## Features

- Apply the Lambda Invoker function to the Master Account.
  - Invokes a list of Lambda Functions provided in the Central Management Account for future Compute Auditing. 
  - __NOTE: Invocation asynchronously with AWS CloudWatch Events and SNS notifications will be implemented in the next release.__
- Add an S3 Bucket for Lambda Functions and a Placeholder Lambda Function in the Central Management Account.
  - The S3 Bucket will contain the zip files for the __lambda_invoker.zip__ and the __lambda_audit_ec2.zip__ used for the Lambda Functions in the Master Account and the Central Management Account.
  - The Placeholder Lambda Function is for the Lambda Audit EC2 function that will be used to audit all EC2 Instances in the provided AWS Account under the Master. __NOTE: This function will be fully implemented in the next release.__
- Update the list of Config Rules applied to Launchpad accounts. Explicit list of changes are below.
  - Refactored the use of the __aws_config__ to separate the Config Rules in to their own separate module use. 
    - This is to allow changes to be made in the list of AWS Managed Config rules being applied by Launchpad, preventing the destruction and recreation of existing Config Rules.
    - Previous implementation had Config Rules defined in the order they are listed, any change to that order (Rule is now in a different index in the list) will be treated as a Terraform Change and they will have to be destroyed and recreated. The destruction and recreation of Config Rules may take some time to execute and can hit the 5 minute limit that Terraform allows for application of resources. This may lead to non-parity in Config Rules applied in all accounts, forcing multiple Launchpad applications on accounts to ensure all Launchpad Config Rules have been applied.
  - Changed Rules
    - __IAM_POLICY_BLACKLISTED_CHECK__ : Removed the AdminstratorAccess policy since the default AWS\_\*\_Admins uses the policy. __Possible future enhancement would be to create a Custom Rule that no other Role can use the AdmnistratorAccess policy besides the AWS\_\*\_Admins.__
  - Removed Rules
    - __LAMBDA_FUNCTION_SETTINGS_CHECK__ : Can't restrict all Lambda Functions, varied to each use-case. __Possible future enhancement for general Lambda checks would be to inspect the execution role to make sure it doesn't allow any wildcard actions.__
    - __EC2_MANAGEDINSTANCE_PLATFORM_CHECK__ : Don't need to restrict SSM only being applied to one of Linux or Windows, but not both. SSM should be able to be applied to all Platforms.
    - __EC2_MANAGEDINSTANCE_APPLICATIONS_BLACKLISTED__ : Needs SSM on instances and Systems Manager setup. Don't have list of blacklisted applications.
    - __EC2_MANAGEDINSTANCE_APPLICATIONS_REQUIRED__ : Needs SSM on instances and Systems Manager setup. Don't have a list of required applications.
    - __DESIRED_INSTANCE_TYPE__ : Don't have a list of desired instance types.
    - __CLOUDFORMATION_STACK_NOTIFICATION_CHECK__ : CloudFormation stack from the Provisioning process does not meet this requirement.
    - __S3_BUCKET_SERVER_SIDE_ENCRYPTION_ENABLED__ : As of v1.3.0 of the [aws_s3 module](https://github.optum.com/CommercialCloud-EAC/aws_s3), this rule will be violated by default with the switch to the default S3 Server Side Encryption. __Possible future enhancement would be to create a Custom Rule that verify that the default encryption is enabled for all buckets.__
    - __S3_BUCKET_SSL_REQUESTS_ONLY__ : The log and replication buckets created by the [aws_s3 module](https://github.optum.com/CommercialCloud-EAC/aws_s3) does not meet this requirement by default. Some AWS Services may not be compatible with SSL transfer. 
    - __S3_BUCKET_VERSIONING_ENABLED__ : Not all aws_s3 sub-modules meet this requirement.
    - __S3_BUCKET_REPLICATION_ENABLED__ : [Bootstrap](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap)'s S3 doesn't use replication. Not all aws_s3 sub-modules meet this requirement.
    - __DYNAMODB_AUTOSCALING_ENABLED__ : [Bootstrap](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap)'s DynamoDB doesn't meet this requirement.
    - __RDS_MULTI_AZ_SUPPORT__ : An application case to case requirement for Multi Availability Zone for RDS.
    - __CLOUDWATCH_ALARM_RESOURCE_CHECK__ : An application case to case requirement whether CloudWatch is enabled as the Metric Alert service for an EC2 instance. __Possible future enhancement would be to verify if a Metric Alert service is used/attached to an instance.__
    - __CLOUDWATCH_ALARM_ACTION_CHECK__ : An application case to case requirement whether CloudWatch should enable an action on all Alarm types.
    - __EBS_OPTIMIZED_INSTANCE__ : The following modules do not enforce EBS Optimized Instances and will be flagged as non-compliant if used: [aws_kubernetes](https://github.optum.com/CommercialCloud-EAC/aws_kubernetes), [aws_eks](https://github.optum.com/CommercialCloud-EAC/aws_eks), and [aws_egress_proxy](https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy). __Possible future enhancment would be to enhance said modules with EBS and add back this rule.__
- Update EIS Requirements
  - Removed the related Config Rules mentioned above. 


# AWS Launchpad 1.5.0

## Features
- Separate AWSAdapter class to its own Groovy file (```AWSAdapter.groovy```) to allow dynamic loading of class from release/tags.
- If a Single Account run, will load in the AWSAdapter class from the latest tag/release and instantiate one for the BaseLaunchpadPipeline.
- Add a ```version.txt``` file to determine what the next version of aws_launchpad should be tagged with. 
- Add 2 Config Rules for Launchpad
  - Open Security Groups : Checks if a Security Group has a Cidr of IPv4 0.0.0.0/0 or IPv6 ::/0
  - Attached Internet Gateways : Flags VPCs that have an Internet Gateway attached.

## Breaking Changes
- The Single Account run and the dynamic AWSAdapter class loading will not work with previous tag/releases due to the need of having the AWSAdapter class in the AWSAdapter.groovy file with a method call ```getAWSAdapter(jenkinsInstance)``` that will instantiate an instance of AWSAdapter. 

## Future Enhancements
- Update ```central_eis_account```, ```central_management_account```, and ```launchpad_account``` with v2.0.0 modules. 

# AWS Launchpad 1.4.1

## Features

- Removal of decommissioned AWS accounts from YAML: this enhancement ensures that only active AWS accounts associated with the master account in which launchpad is operating on are processed.  That is, accounts that are not in the active list will be bypassed. 

# AWS Launchpad 1.4.0

## Features

- Removal of default VPCs: Upon the provisioning of a new AWS account all the default VPCs associated with the new AWS account will get removed in order to avoid any Optum non-standard configurations regarding security.
- Enable Enterprise Support: In the case of an existing AWS Account that does not have __Enterprise__ level of __AWS Support__ enabled in their account, Launchpad will automatically create a __Support Case__ on behalf of their account through the respective AWS Master. See [enable_support.py](launchpad_account/enable_support.py) to see how the case is created. For additional information about the differences of __AWS Support__, please visit this [link](https://aws.amazon.com/premiumsupport/compare-plans/).

# AWS Launchpad 1.3.1

## Features

- Separated YAML into a private repo.
- Refactored the Jenkinsfile Pipeline.

# AWS Launchpad 1.3.0

## Features

- Central EIS Account: A new Central EIS Security Account has been created for each AWS Environment and it's used to house the Read and Break Glass roles for EIS. Please see the [EIS Access markdown](EIS_Access.md) for more details.
- EIS Access Roles in Launchpad Accounts: Each Account applied with Launchpad will also be applied with new Roles that allow privileged EIS Users access to the account with Read and/or Break Glass privileges. Please see the [EIS Access markdown](EIS_Access.md) for more details.

# AWS Launchpad 1.2.0

## Features

- Integration with AWS Account Provisioning: Ability to run Launchpad in an Single Account and its respective Central Logging Account during Provisioning when a new AWS Account is created. 

# AWS Launchpad 1.1.0

## Features

- **[AWS Config](https://aws.amazon.com/config/)**: Core rules defined in each account, logs are sent to a centralized S3 bucket in Commercial Cloud's Central Management Account. 

# AWS Launchpad 1.0.0

## Features

- **[Cloudtrail](https://aws.amazon.com/cloudtrail/)**: logs will be sent to a centralized S3 bucket in the Commercial Cloud Central Management Account.
