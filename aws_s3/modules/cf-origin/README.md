# cf-origin

## Overview

This is a sub-module of the aws_s3 Terraform module. An S3 Bucket with capabilities for Website Hosting via CloudFront (static website hosting from an S3 Bucket).

Note: the items/files starting with "common_" are symbolic links to cooresponding files in the "..\common"
sub-module directory. 

## S3 CF Origin Variables:

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
|cf_origin_access_identity_arn    | CloudFront origin access identity arn for bucket policy | `` |
|cf_origin_cors_allowed_headers   | Specifies which headers are allowed (list)              | ["*"] |
|cf_origin_cors_allowed_methods   | Specifies which methods are allowed. Can be GET, PUT, POST, DELETE or HEAD (list) | ["GET", "PUT", "POST"] |
|cf_origin_cors_allowed_origins   | Specifies which origins are allowed(list)                     | ["*"] |
|cf_origin_cors_expose_headers    | Specifies expose header in the response                       | ["ETag"] |
|cf_origin_cors_max_age_seconds   | Specifies time in seconds that browser can cache the response for a preflight request | 3000 |
|cf_origin_logging_bucket_name    | The name of the bucket that will receive the log objects      | ``|
|cf_origin_logging_bucket_target_prefix | The name of the key prefix for log objects              | `` |  
|cf_origin_force_destroy          | A boolean that indicates all objects should be deleted from the bucket so that the bucket can be destroyed without error | false | 

## S3 CF Origin Outputs:

|Output        |Description           |
|--------------|----------------------|
|id     |Name of the S3 bucket   |
|arn    |ARN of the S3 bucket  |
|cf_origin_bucket_domain_name | The domain name of the static content bucket |