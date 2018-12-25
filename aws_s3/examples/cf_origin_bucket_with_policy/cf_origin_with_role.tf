# Create Bucket Example with a policy attached

# Configure the AWS Provider
provider "aws" {
  region = "us-east-1"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create Bucket With a Policy Example

module "cf-origin-bucket-with-policy" {
  source                                 = "../../modules/cf-origin"
  name_suffix                            = "cf-origin-bucket"
  namespace                              = "example"                                                             # Can't do random namespace due to interpolation error
  custom_policy                          = "${data.aws_iam_policy_document.custom_policy.json}"
  versioning_enabled                     = "true"
  cf_origin_access_identity_arn          = "${aws_cloudfront_origin_access_identity.for_static_content.iam_arn}"
  cf_origin_cors_allowed_headers         = ["*"]
  cf_origin_cors_allowed_methods         = ["POST"]
  cf_origin_logging_bucket_name          = "${module.log_bucket.id}"
  cf_origin_logging_bucket_target_prefix = "cf-s3-log/"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "cf-origin-bucket-with-policy-and-role"
  }
}

## Create CloudFront origin access identity
resource "aws_cloudfront_origin_access_identity" "for_static_content" {
  comment = "for S3 static content examaple"
}

data "aws_caller_identity" "current" {}

module "log_bucket" {
  source = "../../modules/simple"
  name   = "log-bucket-example-${data.aws_caller_identity.current.account_id}-${module.random_name.name}"
  acl    = "log-delivery-write"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "cf-origin-bucket-with-policy-and-role"
  }
}
