# common

## Overview

This is the common sub-module that contains ```.tf``` files that are used within the other sub-modules.

__NOTE: This sub-module is not meant to be called directly.__

## S3 Simple Variables:

The following variables are available for all S3 buckets and can be overridden in the S3 module

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

**NOTE:** The S3 bucket name must be a *globally* unique name.

**Bucket Name:** The s3 bucket name is computed from several other variables. If bucket_name is defined then the computed value is `${name}-${namespace?}`. Otherwise bucket_name is defined as `${name_prefix}-${name_suffix}-${namespace?}`.

**Bucket Encryption Types:** Adding bucket encryption is optional; only 1 type of encryption is allowed per bucket.  By default, S3 buckets are encrypted using the AES256 encryption type.  The bucket can optionally be assigned to any of the following encryption types:

|Bucket Encryption Type|Description   |
|-----------|---------------------------------------|
|aes256 |Activates bucket encryption using an AWS managed master key|
|aws:kms |Activates bucket encryption using a key that is managed within the AWS key management service; AWS creates a KMS key for you|
|aws:kms |Activates bucket encryption using a key that is managed within the AWS key management service; User can provide a KMS key to have control over the master encryption key|


**NOTE:** Please review the following notes on encryption:
* No encryption is deprecated
* If the encryption type does not exist, AES256 encryption type will be used.
* This S3 module does not manage aws:kms encryption keys; the keys must exist before calling this module.
* The `bucket_kms_key_id` is only used when the encryption type equal `aws:kms`.
* When using the aws:kms encryption type, the `bucket_kms_key_id` is optional.  If the `bucket_kms_key_id` is not provided, then the KMS service will generate a new encryption key managed by AWS. **This is not recommended.**
* When using the aws:kms encryption type, it is recommended to generate a new KMS key using the aws_kms module, then use that KMS key for encrypting S3 objects.

**Bucket Roles:** Adding S3 bucket roles is optional.  When creating an S3 bucket, the bucket can optionally be assigned to any of the following roles:

|Bucket Role Name  |Description              | Policy Name | Role Name |Default              |
|------------|---------------------------|--------------------|--------------------|------|
|bucket_read_only |Creates a ReadOnly policy for the S3 bucket and assigns the policy to a role|S3ReadOnly-{bucket_name}|S3ReadOnly-{bucket_name}|`false`|
|bucket_read_write |Creates a ReadWrite policy for the S3 bucket and assigns the policy to a role|S3ReadWrite-{bucket_name}|S3ReadWrite-{bucket_name}|`false`|
|bucket_full_control |Creates a FullControl policy for the S3 bucket and assigns the policy to a role|S3FullControl-{bucket_name}|S3FullControl-{bucket_name}|`false`|

**Global Roles:** Adding bucket policies to global roles is optional. When creating an S3 bucket, the bucket can be optionally assigned to any global roles.  This S3 bucket module does not create the global roles, these role names must be created before passing the role to this module.

|Global Bucket Roles |Description              | Policy Name |Default List of Role Names|
|-----------|---------------------------|---------------|-------------|
|global_read_only |Attached the S3 Bucket ReadOnly policy to the list of global role name(s)|S3ReadOnly-{bucket_name}|`null`|
|global_read_write |Attached the S3 Bucket ReadWrite policy to the list of global role name(s)|S3ReadWrite-{bucket_name}|`null`|
|global_full_control |Attached the S3 Bucket FullControl policy to the global role name|S3FullControl-{bucket_name}| `null`|

**NOTE:** Global bucket roles can be added to all types of S3 bucket, including simple, logging, replication, etc.

## S3 Simple Outputs:

|Output        |Description           |
|--------------|----------------------|
|id     |Name of the S3 bucket   |
|arn    |ARN of the S3 bucket  |

## S3 Logging Variables:

In addition to the above S3 Simple variables, the following variables are also available when the **logging** property is activated:

|Variable    |Description              |Default              |
|------------|-------------------------|---------------------|
|log_bucket_name         |S3 target bucket name to write logging files|{log_bucket_name_base}-{log_bucket_name_suffix}|
|log_bucket_name_base    |Base name for the logging S3 bucket|{S3-source-bucket-name}|
|log_bucket_name_suffix  |Suffix name for the S3 bucket|-logs|
|log_bucket_key |S3 bucket key to write logging files|logs/|
|log_bucket_custom_policy|IAM custom policy to attach to the log S3 bucket                            |`null`|
|log_bucket_force_destroy|Terraform will remove all objects from the S3 bucket; then delete the bucket|false|
|log_bucket_versioning_enabled|Enable AWS versioning on the S3 logging bucket                         |false|
|log_bucket_roles        |IAM roles to attach to the log S3 bucket                  |(See more details above)|

## S3 Logging Outputs:

In addition to the above S3 Simple outputs, the following outputs are also available when the **logging** property is activated:

|Output        |Description           |
|--------------|----------------------|
|log_bucket_id |Name of the S3 bucket |
|log_bucket_arn |ARN of the S3 bucket |
|log_bucket_key  |Name of the S3 bucket key |

## S3 Replication Variables:

In addition to the above S3 Simple variables, the following variables are also available when the **replication** property is activated:

|Variable    |Description              |Default              |
|------------|-------------------------|---------------------|
|rep_bucket_name         |S3 target bucket name to write logging files|{rep_bucket_name_base}-{rep_bucket_name_suffix}|
|rep_bucket_name_base    |Base name for the logging S3 bucket|{S3-source-bucket-name}|
|rep_bucket_name_suffix  |Suffix name for the S3 bucket|-replication|
|rep_bucket_custom_policy|IAM custom policy to attach to the rep S3 bucket                            |`null`|
|rep_bucket_force_destroy|Terraform will remove all objects from the S3 bucket; then delete the bucket|false|
|rep_bucket_roles        |IAM roles to attach to the log S3 bucket                  |(See more details above)|

**Note** Versioning is automatically enabled for both the primary and replicated buckets in order for replication to work correctly

## S3 Replication Outputs:

In addition to the above S3 Simple outputs, the following outputs are also available when the **replication** property is activated:

|Output        |Description           |
|--------------|----------------------|
|rep_bucket_id |Name of the S3 bucket |
|rep_bucket_arn |ARN of the S3 bucket |
|rep_bucket_key  |Name of the S3 bucket key |

**Note:** It can take up to a few minutes before S3 buckets are available for use.

## S3 CF Origin Variables:

In addition to the S3 Simple variables, the following variables are also available when the **cf-origin** module is used:

|Variable    |Description              |Default              |
|------------|-------------------------|---------------------|
|cf_origin_access_identity_arn    | CloudFront origin access identity arn for bucket policy | `` |
|cf_origin_cors_allowed_headers   | Specifies which headers are allowed (list)              | ["*"] |
|cf_origin_cors_allowed_methods   | Specifies which methods are allowed. Can be GET, PUT, POST, DELETE or HEAD (list) | ["GET", "PUT", "POST"] |
|cf_origin_cors_allowed_origins   | Specifies which origins are allowed(list)                     | ["*"] |
|cf_origin_cors_expose_headers    | Specifies expose header in the response                       | ["ETag"] |
|cf_origin_cors_max_age_seconds   | Specifies time in seconds that browser can cache the response for a preflight request | 3000 |
|cf_origin_logging_bucket_name    | The name of the bucket that will receive the log objects      | ``|
|cf_origin_logging_bucket_target_prefix | The name of the key prefix for log objects              | `` |  
|cf_origin_force_destroy          | A boolean that indicates all objects should be deleted from the bucket so that the bucket can be destroyed without error | false | 

## S3 CF Origin  Outputs:

In addition to the S3 Simple outputs, the following outputs are also available when the **cf-origin** module is used:

|Output        |Description           |
|--------------|----------------------|
|cf_origin_bucket_domain_name | The domain name of the static content bucket |