# Create Bucket With a Terraform Replication and Logging Example

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

module "bucket_with_replication_and_logging" {
  source                   = "../../modules/rep-log"
  name_suffix              = "bucket-with-replication-and-logging"
  log_bucket_name          = "bucket-with-replication-and-logging-logs"
  rep_bucket_name          = "bucket-with-replication-and-logging-replication"
  log_bucket_custom_policy = ""
  rep_bucket_custom_policy = ""
  namespace                = "example"                                         # Can't use random namespace due to interpolation error

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "bucket-with-replication-and-logging"
  }
}
