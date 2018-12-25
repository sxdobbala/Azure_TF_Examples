# Create Simple Bucket Without a Policy Example

# Configure the AWS Provider
provider "aws" {
  region     = "${var.aws_region}"
}

module "random_name" {
  source = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/random_name?ref=v1.2.1"
}

module "simple-bucket-with-storage-class-without-policy" {
  source                        = "../../modules/simple-with-storage-class"
  name_suffix                   = ""
  custom_policy                 = ""
  namespace                     = "example-${module.random_name.name}"
  prefix                        = "${var.prefix}"
  id                            = "${var.id}"
  enabled                       = "${var.enabled}"
  days_initial_storage_class    = "${var.days_initial_storage_class}"
  initial_storage_class         = "${var.initial_storage_class}"
  days_final_storage_class      = "${var.days_final_storage_class}"
  final_storage_class           = "${var.final_storage_class}"
  expiration_days               = "${var.expiration_days}"
  days_noncurrent_storage_class = "${var.days_noncurrent_storage_class}"
  noncurrent_storage_class      = "${var.noncurrent_storage_class}"

  global_tags = {
    global_tag = "example-${module.random_name.name}"
  }

  tags = {
    Name = "simple-bucket-with-storage-class-without-policy"
  }
}
