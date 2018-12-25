# tflife

## Overview

This is a sub-module of the aws_s3 Terraform module. An S3 Bucket with the capability to prevent a Terraform ```destroy``` deleting the resource.

Note: the items/files starting with "common_" are symbolic links to cooresponding files in the "..\common"
sub-module directory.

## S3 TFLife Variables:

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

## S3 TFLife Outputs:

|Output        |Description           |
|--------------|----------------------|
|id     |Name of the S3 bucket   |
|arn    |ARN of the S3 bucket  |
