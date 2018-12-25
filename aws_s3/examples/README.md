# S3 Bucket Examples

* simple_bucket_to_storageclass: Creates a S3 bucket, without assigning a policy, allows  transition of objects from S3 Standard to other storage class and from there to another compatible storage class(transition takes after specified time in days from the time of object creation), allows transition of noncurrent objects from S3 Standard to storage class after specified number of days from the time object becomes non-current  and defines after how many days from the time of creation object has to expire
* simple_bucket_without_policy: Creates a S3 bucket, without assigning a policy to the bucket
* simple_bucket_without_policy_with_roles: Creates a S3 bucket, without assigning a policy to the bucket, assigns 2 roles:bucket_read_only; bucket_read_write to the bucket
* simple_bucket_with_policy: Creates a S3 bucket and assignes a policy to the bucket
* simple_bucket_with_encryption: Creates a S3 bucket and enforces AES256 encryption
* simple_bucket_with_role: Creates a S3 bucket and creates a read-write role that is associated with the bucket. 
* simple_bucket_with_kms_key_encryption: Creates a KMS key; creates a S3 bucket and enforces the aws:kms encryption type and id (note: this has a dependency on the aws_kms module with in GitHub)
* simple_bucket_with_global_role: Creates a S3 bucket; create a global role and assignes the bucket ReadOnly policy to the global role
* bucket_with_tflife: Creates a S3 bucket and enables the Terraform lifecycle options
* bucket_with_logging: Creates a S3 bucket and enables logging on the bucket
* bucket_with_logging_and_tflife: Creates a S3 bucket, enables logging and enables Terraform lifecycle options on the bucket
* bucket_with_replication_and_logging: Creates a S3 bucket, enables logging, and creates a cross region replication for the bucket
* cf_origin_bucket_with_policy: Creates a S3 bucket that can be used as a CloudFront origin for static web hosting  
* unsecured_bucket: Creates an simple S3 bucket with a policy to make it open for public, used for testing


**NOTE:** These examples assume terraform has been installed and AWS access keys have been configured.

#### Run the examples

```
> cd <example folder>

Update the terrraform.tfvars file with the proper AWS access and secret keys

> terraform init
> terraform plan
> terraform apply
```

Cleanup the example from AWS
```
> terraform destroy

NOTE: The terraform destroy for the `bucket_with_tflife` example will fail.
This is the correct behavior, see instructions below on how to remove Terraform objects with lifecycle properties.
```

####Remove Terraform objects with lifecycle prevent_destroy option enabled
Follow these steps to remove terraform objects with lifecycle `prevent_destroy` option:
* Edit the simple_bucket_without_policy.tf file
* Comment out the `module` section of terraform code
* Run `terraform apply`: This will remove the protected terraform object from AWS
* Run `terraform destroy`: This will destroy all remaining terraform objects from AWS
