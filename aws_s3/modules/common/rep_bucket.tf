# Create a bucket using rep feature_flag
data "template_file" "aws_s3_rep_bucket" {
  template = "/dev/null"

  vars {
    rep_bucket_name = "${length(var.rep_bucket_name) == 0 ?
                         format("%s%s%s",
                               length(var.rep_bucket_name_base) == 0 ?
                                 format("%s", local.name)
                                 :
                                 format("%s", var.rep_bucket_name_base ),
                               length(var.rep_bucket_name_suffix) == 0 ?
                                 format("-%s", "replication")
                                 :
                                 format("-%s", var.rep_bucket_name_suffix ),
                               length(var.namespace) == 0 ?
                                 format("%s", "" )
                                 :
                                 format("-%s", var.namespace )
                               )
          :
          format("%s", length(var.namespace) == 0 ?
                       format("%s", var.rep_bucket_name)
                       :
                       format("%s-%s", var.rep_bucket_name, var.namespace)
                )
          }"

    rep_force_destroy      = "${var.rep_bucket_force_destroy}"
    rep_acl      = "${var.rep_bucket_acl}"
    rep_bucket_key         = "${length(var.rep_bucket_key) == 0 ? format("%s/", "replication") : format("%s/", var.rep_bucket_key)}"

    rep_custom_policy_exists = "${(length(var.rep_bucket_custom_policy) == 0 ? 0 : 1)}"
    rep_bucket_custom_policy = "${var.rep_bucket_custom_policy}"
  }
}

# Due to a terraform limitation on using "count" within a module construct
# See here: https://github.com/hashicorp/terraform/issues/953
# Call the aws_iam_role provider directly and not via the aws_iam module.
# Once the terraform defect has been fixed, the can be replace with a call to the aws_iam module directly
# Add read_only role and policy limited to this single S3 bucket
resource "aws_iam_role" "rep_bucket_role_read_only" {
  name               = "S3ReadOnly-${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
  count              = "${var.rep_bucket_roles["bucket_read_only"]}"
  description        = "ReadOnly role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "rep_bucket_role_read_only_inline" {
  count  = "${var.rep_bucket_roles["bucket_read_only"]}"
  role   = "${aws_iam_role.rep_bucket_role_read_only.name}"
  policy = "${data.aws_iam_policy_document.S3_ReadOnly.json}"
  name   = "${aws_iam_role.rep_bucket_role_read_only.name}"
}

# Add read_write role and policy limited to this single S3 bucket
resource "aws_iam_role" "rep_bucket_role_read_write" {
  name               = "S3ReadWrite-${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
  count              = "${var.rep_bucket_roles["bucket_read_write"]}"
  description        = "ReadWrite role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "rep_bucket_role_read_write_inline" {
  count  = "${var.rep_bucket_roles["bucket_read_write"]}"
  role   = "${aws_iam_role.rep_bucket_role_read_write.name}"
  policy = "${data.aws_iam_policy_document.S3_ReadWrite.json}"
  name   = "${aws_iam_role.rep_bucket_role_read_write.name}"
}

# Add full_control role and policy limited to this single S3 bucket
resource "aws_iam_role" "rep_bucket_role_full_control" {
  name               = "S3FullControl-${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
  count              = "${var.rep_bucket_roles["bucket_full_control"]}"
  description        = "FullControl role constrained to a single S3 bucket"
  assume_role_policy = "${data.aws_iam_policy_document.default_policy.json}"
}

resource "aws_iam_role_policy" "rep_bucket_role_full_control_inline" {
  count  = "${var.rep_bucket_roles["bucket_full_control"]}"
  role   = "${aws_iam_role.rep_bucket_role_full_control.name}"
  policy = "${data.aws_iam_policy_document.S3_FullControl.json}"
  name   = "${aws_iam_role.rep_bucket_role_full_control.name}"
}

# Add the policy to any "global" roles that are passed into this module
# Note: The global role needs be be created outside of this module, with the role name passed in as a variable
resource "aws_iam_role_policy" "global_rep_role_read_only_inline" {
  count  = "${length(var.global_rep_bucket_roles["global_read_only"])}"
  role   = "${element(var.global_rep_bucket_roles["global_read_only"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_ReadOnly.json}"
  name   = "S3ReadOnly-${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
}

resource "aws_iam_role_policy" "global_rep_role_read_write_inline" {
  count  = "${length(var.global_rep_bucket_roles["global_read_write"])}"
  role   = "${element(var.global_rep_bucket_roles["global_read_write"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_ReadWrite.json}"
  name   = "S3ReadWrite-${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
}

resource "aws_iam_role_policy" "global_rep_role_full_control_inline" {
  count  = "${length(var.global_rep_bucket_roles["global_full_control"])}"
  role   = "${element(var.global_rep_bucket_roles["global_full_control"], count.index)}"
  policy = "${data.aws_iam_policy_document.S3_FullControl.json}"
  name   = "S3FullControl-${data.template_file.aws_s3_rep_bucket.rep_vars.name}"
}

resource "aws_s3_bucket_policy" "rep_bucket_custom_policy" {
  # Only create this resource when a rep bucket custom policy exists
  count  = "${data.template_file.aws_s3_rep_bucket.vars.rep_custom_policy_exists}"
  bucket = "${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_name}"
  policy = "${data.template_file.aws_s3_rep_bucket.vars.rep_bucket_custom_policy}"
}
