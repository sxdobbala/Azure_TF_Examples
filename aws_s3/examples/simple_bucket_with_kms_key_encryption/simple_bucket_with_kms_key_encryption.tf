# Create Simple Bucket, with aws:kms encryption

# Configure the AWS Provider
provider "aws" {
  version    = "= 1.23.0"
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

# Create KMS key and assign aliases to the key example
module "s3_kms_key_example" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_kms.git?ref=v2.0.0"
  description = "s3-kms-key"
  alias_names = ["s3-kms-key"]
  policy      = ""
  namespace   = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "s3-kms-key-example"
  }
}

# Create S3 bucket, enforcing aws:kms encryption; using the kms_key_id created with the aws_kms module above
module "simple_bucket_with_kms_key_encryption" {
  source            = "../../modules/simple"
  name_suffix       = "simple-bucket-with-kms-key-encryption"
  sse_algorithm     = "aws:kms"
  kms_master_key_id = "${module.s3_kms_key_example.arn}"
  force_destroy     = "true"
  namespace         = "example-${module.random_name.name}"

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "simple-bucket-with-kms-key-encryption"
  }
}
