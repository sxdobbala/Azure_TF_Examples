# Create Bucket with a logging bucket without a Policy

# Configure the AWS Provider
provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
}

module "bucket-for-logging" {
  source    = "../../modules/simple"
  name      = "bucket-with-logging-logs"
  namespace = "example"                  # Can't do random namespace due to interpolation error# Can't do random namespace due to interpolation error
  acl       = "log-delivery-write"
}

module "bucket-with-logging" {
  source                   = "../../modules/existing-log"
  name_suffix              = "bucket-with-logging"
  custom_policy            = ""
  log_bucket_name          = "bucket-with-logging-logs"
  log_bucket_custom_policy = ""
  namespace                = "example"                    # Can't do random namespace due to interpolation error

  global_tags = {
    global_tag = "example"
  }

  tags = {
    Name = "bucket-with-log"
  }
}
