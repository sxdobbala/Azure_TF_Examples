# Create Bucket With a Terraform lifecycle Example

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "bucket_with_logging_and_tflife" {
  source                   = "../../modules/tflife-log"
  name_suffix              = "bucket-with-logging-and-tflife"
  log_bucket_name          = "bucket-with-logging-and-tflife-logs"
  log_bucket_custom_policy = ""
  namespace                = "example"                             # Can't do random namespace due to interpolation error

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "bucket-with-logging-and-tflife"
  }
}
