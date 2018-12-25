## 2.1.0 (*)
FEATURES:
* Sub-module, simple-with-storage has been added which extends simple and allows transition of objects to other storage classes, set expiration [PR-65]
  (https://github.optum.com/CommercialCloud-EAC/aws_s3/pull/65)

## 2.0.1 (December 5, 2018)
BUG FIXES
*  Replication rules keep recreating on every terraform apply when using this module even when the rule already exists [PR-64](https://github.optum.com/CommercialCloud-EAC/aws_s3/pull/64)


## 2.0.0 (Oct 23, 2018)
IMPROVEMENTS:
* Added EIS Endorsements and diagram to the README
* Update to Terraform 2.0 format

## 1.4.0 (Sept 19, 2018)
FEATURES:
* Support for cross region replicated buckets (rep feature) [PR-56](https://github.optum.com/CommercialCloud-EAC/aws_s3/pull/56)

## 1.3.0
IMPROVEMENTS:
* Default encryption feature is implemented in favor of bucket policy enforced encryption. "no encryption" is not an option anymore. Fixes #29
* Log buckets are also now encrypted by default, Thanks to default encryption. Fixes #22

FEATURES:
* Support for static web hosting via CloudFront. Watch out for [serverlessUI](https://github.optum.com/CommercialCloud-EAC/aws_serverlessui) 

## 1.2.1

BUG FIXES:

* Fix `value of 'count' cannot be computed` error, when using an interpolated `bucket_suffix`

## 1.2.0

BUG FIXES

* **BREAKING CHANGE** Removed `bucket_name` and `log_bucket_name` outputs, in order to support terraform v0.11.x

## 1.1.3 (Feb 6, 2018)

IMPROVEMENTS:
* Updated git references to use the new CommercialCloud-EAC Github Org

## 1.1.2 (Jan 18, 2018)
IMPROVEMENTS:
* **Option to enable a Public S3 bucket has been removed, public buckets are no longer required for Serverless UI implementation.

When implementing this change, this may break existing code if public S3 buckets were being used.

## 1.1.1 (Dec 12, 2017)
FEATURES:
* **Added an additional output to allow access to the bucket ARN after the bucket is created.

## 1.1.0 (Dec 6, 2017)
FEATURES:
* **Enforce SSL policy on S3 buckets by default; requires SSL transport with reading and writing to S3 buckets

BUG FIXES:
* **ReadOnly role can now GetObjects from S3 buckets
* **FullControl role no longer allows CreateBucket permission

## 1.0.6 (Oct 12, 2017)

FEATURES:
* **S3 buckets now support aws:kms encryption keys :** When creating an S3 bucket encryption keys stored in the AWS KMS system can now be used

## 1.0.5 (Oct 5, 2017)

FEATURES:
* **Logging and tflife feature flags now support encryption flags :** When creating an S3 bucket using logging and/or tflife feature flags, the module now utilizes the encryption_type flag

IMPROVEMENTS:
* **Code Refactoring :** The module code has been refactored to eliminate redundant code; the redundant code has been moved into the "common" folder and reused by each of the feature flag folders

## 1.0.4 (Oct 2, 2017)

FEATURES:
* **Added AES256 Encryption :** An S3 bucket policy now enforces AES256 encryption by default


## 1.0.3 (Sept 21, 2017)

FEATURES:
* **Added Default Polcies :** S3 buckets can now be created with optional policies for ReadOnly; ReadWrite; and FullControl policies.
* **Add Polcies to Global Roles :** An S3 bucket policy can now be added to globally defined roles.

## 1.0.2 (Aug 29, 2017)

BUG FIXES:
* **S3 Bucket Name :** S3 bucket name now gets created with proper name_space for simple and tflife features [see issue](https://github.optum.com/CommercialCloud-EAC-AWS/aws_s3/issues/14)

## 1.0.1 (Aug 24, 2017)

FEATURES:
* **Module Feature Flags :** Restructures the S3 module to enable the following feature flags: simple; logging; tflife; and allows for more feature flags in the future
* **Bucket Logging :** Creates a logging bucket which tracks the requests made to an S3 bucket
* **Bucket Terraform Lifecycle :** Creates an S3 bucket and enables Terraform lifecycle management on the bucket

BUG FIXES:
* **S3 Bucket Name :** S3 bucket name now gets created with proper name_space [see issue](https://github.optum.com/CommercialCloud-EAC/aws_s3/issues/12)

## 1.0.0 (Aug 10, 2017)

FEATURES:

* **bucket name :** If bucket name is not provided, one will be generated using {aws_account_id}-{name_space}
* **bucket policy :** Assigns the policy to the bucket if one is provided
* **bucket acl :** The default acl is `private` unless overridden
* **terraform lifecycle prevent destroy :** Can be activated to prevent Terraform from destroying the bucket
* **bucket tags :** Both global and s3 tags can be applied to the bucket

Please see github commit and pull request history for more details.
