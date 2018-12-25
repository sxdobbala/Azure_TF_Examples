# Create Bucket With a Terraform Replication Example

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

# Configure the AWS Provider For Replication
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_replication_region}"
  alias      = "replication"
}

module "bucket_with_replication" {
  source                   = "../../modules/rep"
  name_suffix              = "bucket-with-replication"
  rep_bucket_name          = "bucket-with-replication-replication"
  rep_bucket_custom_policy = ""
  namespace                = "example"                             # Can't do random namespace due to interpolation error

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "bucket-with-replication"
  }
}
