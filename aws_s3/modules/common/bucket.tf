# S3 bucket module
data "aws_caller_identity" "current" {}

locals {
  aws_account_id = "${data.aws_caller_identity.current.account_id}"
  version_tag = {"cc-eac_aws_s3" = "v2.0.0"}

  # Default prefix = AWS Account ID
  name_prefix = "${coalesce(
    var.name_prefix,
    local.aws_account_id
  )}"

  # Default suffix  = "default"
  name_suffix = "${coalesce(
    var.name_suffix,
    "default"
  )}"

  # Bucket name =
  #   [prefix]-[suffix]-[namespace?]
  #    or
  #   [name]-[namespace?]
  name = "${
    var.name == "" ?
      join("-", compact(list(
        local.name_prefix,
        local.name_suffix,
        var.namespace
      )))
      :
      join("-", compact(list(
        var.name,
        var.namespace
      )))
  }"

  # Enforce some type of encryption is used; reguardless; no if ands or butts about it!
  sse_algorithm = "${lookup(var.valid_encryption_types, var.sse_algorithm, "AES256")}"

  ssl_enforced = "${var.S3ssl_enforced}"
}

# Local block for working with policies
locals {
  # Because this var is typically from another module, has to be local var else terraform plan fails
  kms_master_key_id = "${var.kms_master_key_id}"

  # If a custom policy is provided, then include the policy onto the bucket; extract the statements from the policy
  custom_policy_exists = "${(length(var.custom_policy) == 0 ? 0 : 1)}"

  custom_policy_statement = "${format("%s", substr(var.custom_policy,
                                 length(var.custom_policy) == 0 ?  0 : 50,
                                 length(var.custom_policy)-56 <= 0 ?  0 : length(var.custom_policy)-56 ))}"

  # If ssl is enforced, create the ssl policy statement
  S3ssl_enforced = "${var.S3ssl_enforced == "" ? 0 : 1}"

  bucket_enforce_ssl_policy_statement = "${format("%s", substr(data.aws_iam_policy_document.bucket_enforce_ssl.json, 50, length(data.aws_iam_policy_document.bucket_enforce_ssl.json)-56))}"

  bucket_cf_origin_policy_statment = "${format("%s", substr(data.aws_iam_policy_document.bucket_enforce_cf_origin_only_access.json, 50, length(data.aws_iam_policy_document.bucket_enforce_cf_origin_only_access.json)-56))}"
}

# Local block to construct the final bucket policy
locals {
  # Check if either custom or encryption policy exists, then apply the policy to the bucket
  policy_exist = "${local.custom_policy_exists + local.S3ssl_enforced + local.enable_cf_origin_policy == 0 ? 0 : 1}"

  # Reconstruct the policy json into proper JSON format; combine any custom_policy,cf origin policy and encryption_policy
  policy_statement = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
  ${format("%s%s%s%s%s",
      local.custom_policy_exists == 0 ?
        format("%s", "")
        :
        format("%s", local.custom_policy_statement ),
      local.custom_policy_exists * local.S3ssl_enforced == 0 ?
        format("%s", "")
        :
        format("%s", "," ),
      local.ssl_enforced == 0 ?
        format("%s", "")
        :
        format("%s", local.bucket_enforce_ssl_policy_statement ),
      (local.custom_policy_exists + local.S3ssl_enforced) * local.enable_cf_origin_policy == 0 ?
        format("%s", "")
        :
        format("%s", "," ),
      local.enable_cf_origin_policy == 0 ?
        format("%s", "")
        :
        format("%s", local.bucket_cf_origin_policy_statment )
      )}
  ]
}
EOF
}

# Due to a terraform limitation on using "count" within a module construct
# See here: https://github.com/hashicorp/terraform/issues/953
# Call the aws_iam_role provider directly and not via the aws_iam module.
# Once the terraform defect has been fixed, this can be replaced with a call to the aws_iam module directly
# Add read_only role and policy limited to this single S3 bucket
resource "aws_iam_role" "bucket_role_read_only" {
  name               = "S3ReadOnly-${local.name}"
  count              = "${var.roles["bucket_read_only"]}"
  description        = "ReadOnly role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "bucket_role_read_only_inline" {
  count  = "${var.roles["bucket_read_only"]}"
  role   = "${aws_iam_role.bucket_role_read_only.name}"
  policy = "${data.aws_iam_policy_document.S3_ReadOnly.json}"
  name   = "${aws_iam_role.bucket_role_read_only.name}"
}

# Add read_write role and policy limited to this single S3 bucket
resource "aws_iam_role" "bucket_role_read_write" {
  name               = "S3ReadWrite-${local.name}"
  count              = "${var.roles["bucket_read_write"]}"
  description        = "ReadWrite role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "bucket_role_read_write_inline" {
  count  = "${var.roles["bucket_read_write"]}"
  role   = "${aws_iam_role.bucket_role_read_write.name}"
  policy = "${data.aws_iam_policy_document.S3_ReadWrite.json}"
  name   = "${aws_iam_role.bucket_role_read_write.name}"
}

# Add full_control role and policy limited to this single S3 bucket
resource "aws_iam_role" "bucket_role_full_control" {
  name               = "S3FullControl-${local.name}"
  count              = "${var.roles["bucket_full_control"]}"
  description        = "FullControl role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "bucket_role_full_control_inline" {
  count  = "${var.roles["bucket_full_control"]}"
  role   = "${aws_iam_role.bucket_role_full_control.name}"
  policy = "${data.aws_iam_policy_document.S3_FullControl.json}"
  name   = "${aws_iam_role.bucket_role_full_control.name}"
}

# Add the policy to any "global" roles that are passed into this module
# Note: The global role needs be be created outside of this module, with the role name passed in as a variable
resource "aws_iam_role_policy" "global_role_read_only_inline" {
  count  = "${length(var.global_roles["global_read_only"])}"
  role   = "${element(var.global_roles["global_read_only"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_ReadOnly.json}"
  name   = "S3ReadOnly-${local.name}"
}

resource "aws_iam_role_policy" "global_role_read_write_inline" {
  count  = "${length(var.global_roles["global_read_write"])}"
  role   = "${element(var.global_roles["global_read_write"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_ReadWrite.json}"
  name   = "S3ReadWrite-${local.name}"
}

resource "aws_iam_role_policy" "global_role_full_control_inline" {
  count  = "${length(var.global_roles["global_full_control"])}"
  role   = "${element(var.global_roles["global_full_control"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_FullControl.json}"
  name   = "S3FullControl-${local.name}"
}

resource "aws_s3_bucket_policy" "bucket_policy" {
  # Only create this resource when either a custom policy or encryption policy exists
  count  = "${local.policy_exist}"
  bucket = "${aws_s3_bucket.bucket.id}"
  policy = "${local.policy_statement}"
}
