# rep-log

## Overview

This is a sub-module of the aws_s3 Terraform module. An S3 Bucket with capabilities of Cross Region Replication and log its access requests to a new S3 Bucket.

Note: the items/files starting with "common_" and "rep_" are symbolic links to cooresponding files
in the "..\common" sub-module directory.

## S3 Replication Log Variables:

|Variable    |Description              |Default              |
|------------|-------------------------|---------------------|
|name         |Name of the S3 bucket (must be globally unique S3 name)|{name_prefix}{name_suffix}|
|name_prefix    |Base name for the S3 bucket|{aws_account_id}-|
|name_suffix  |Suffix name for the S3 bucket|default|
|acl | The canned ACL to apply | `private` |
|custom_policy|IAM custom policy to attach to the S3 bucket                       |`null`|
|force_destroy|Terraform will remove all objects from the S3 bucket; then delete the bucket|false|
|versioning_enabled|Enable AWS versioning on the S3 bucket                        |false|
|sse_algorithm|Enable encryption on the S3 bucket                               |`AES256`|
|S3ssl_enforced |Enforce SSL data transfer on the S3 bucket                         |`true`|
|kms_master_key_id   |ARN referencing the KMS key If encryption type is `aws:kms`; ignored if not using `aws:kms` encryption|`null`|
|roles        |IAM roles to attach to the S3 bucket                               |(See more details below)|
|tags                |Map of tags to apply to this single s3 module                      |`null`|
|global_tags         |Map of tags to apply to all resources that have tags parameters    |`null`|
|rep_bucket_name         |S3 target bucket name to write logging files|{rep_bucket_name_base}-{rep_bucket_name_suffix}|
|rep_bucket_name_base    |Base name for the logging S3 bucket|{S3-source-bucket-name}|
|rep_bucket_name_suffix  |Suffix name for the S3 bucket|-replication|
|rep_bucket_custom_policy|IAM custom policy to attach to the rep S3 bucket                            |`null`|
|rep_bucket_force_destroy|Terraform will remove all objects from the S3 bucket; then delete the bucket|false|
|rep_bucket_roles        |IAM roles to attach to the log S3 bucket                  |(See more details above)|
|log_bucket_name         |S3 target bucket name to write logging files|{log_bucket_name_base}-{log_bucket_name_suffix}|
|log_bucket_name_base    |Base name for the logging S3 bucket|{S3-source-bucket-name}|
|log_bucket_name_suffix  |Suffix name for the S3 bucket|-logs|
|log_bucket_key |S3 bucket key to write logging files|logs/|
|log_bucket_custom_policy|IAM custom policy to attach to the log S3 bucket                            |`null`|
|log_bucket_force_destroy|Terraform will remove all objects from the S3 bucket; then delete the bucket|false|
|log_bucket_versioning_enabled|Enable AWS versioning on the S3 logging bucket                         |false|
|log_bucket_roles        |IAM roles to attach to the log S3 bucket                  |(See more details above)|

**Note** Versioning is automatically enabled for both the primary and replicated buckets in order for replication to work correctly

## S3 Replication Log Outputs:

|Output        |Description           |
|--------------|----------------------|
|id     |Name of the S3 bucket   |
|arn    |ARN of the S3 bucket  |
|rep_bucket_id |Name of the S3 bucket |
|rep_bucket_arn |ARN of the S3 bucket |
|rep_bucket_key  |Name of the S3 bucket key |
|log_bucket_id |Name of the S3 bucket |
|log_bucket_arn |ARN of the S3 bucket |
|log_bucket_key  |Name of the S3 bucket key |

**Note:** It can take up to a few minutes before S3 buckets are available for use.