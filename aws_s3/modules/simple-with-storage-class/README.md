# simple-with-storage-class

## Overview

This is a sub-module of the aws_s3 Terraform module. A simple S3 Bucket with a default private acl, allows transition of objects from S3 Standard to other storage class(intial) after specified number of days from the time object is created and from intial storage class object is transtioned to another storage class(final, should be compatible) after specified period  from the time object is created, noncurrent objects are transitioned from S3 Standard to other storage class after specified number of days from the time object has become noncurrent.Object expires after specified expiration days.

Note: the items/files starting with "common_" are symbolic links to cooresponding files in the "..\common"
sub-module directory.

## S3 simple-with-storage-class Variables:

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
|prefix       |Filter to select objects for transition empty value indicates whole bucket|`null`|
|enabled      |Specifies lifecycyle rule status                                          |`true`|
|initial-storage-class |Storage class to which objects has to be transitioned initially  |INTELLIGENT_TIERING|
|final-storage-class  | Storage class to which objects has to be transitioned from intial storage class|GLACIER|
|days-initial-storage-class |No. of days from object creation after which transition to inital storage class has to take place from S3 Standard |30|
|days-final-storage-class  |No. of days from object creation after which transition to final storage class has to take place from initial-storage-class|60|
|expiration-days |No. of days after object creation object expires |365|
|noncurrent-storage-class|Storage class for non-current objects |GLACIER|
|days-noncurrent-storage-class|No. of days after object becomes non-current the object has to be moved noncurrent-storage-class |30|
## S3 simple-with-storage-class Outputs:

|Output        |Description           |
|--------------|----------------------|
|id     |Name of the S3 bucket   |
|arn    |ARN of the S3 bucket  |
