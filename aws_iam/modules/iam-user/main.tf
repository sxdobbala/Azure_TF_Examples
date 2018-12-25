data "aws_caller_identity" "current" {}

# Local variables
locals {
  version_tag = {
    "cc-eac_aws_iam" = "v2.0.0"
  }

  name = "${length(var.name) == 0 ?
                     format("%s%s",
                           length(var.name) == 0 ?
                             format("%s", "default")
                             :
                             format("%s", var.name ),
                           length(var.namespace) == 0 ?
                             format("%s", "" )
                             :
                             format("-%s", var.namespace )
                           )
      :
      format("%s", length(var.namespace) == 0 ?
                   format("%s", var.name)
                   :
                   format("%s-%s", var.name, var.namespace)
            )
      }"

  force_destroy         = "${var.force_destroy}"
  path                  = "${var.path}"
  create_iam_access_key = "${length(var.create_iam_access_key) == 0 ? 0 : 1}"
  enforce_mfa           = "${var.enforce_mfa}"
}

resource "aws_iam_user" "user" {
  name          = "${local.name}"
  path          = "${local.path}"
  force_destroy = "${local.force_destroy}"
}

resource "aws_iam_user_policy" "user" {
  name   = "${local.name}-enforce-mfa"
  user   = "${aws_iam_user.user.name}"
  count  = "${local.enforce_mfa}"
  policy = "${data.aws_iam_policy_document.enforce_mfa.json}"
}

# Generate API keys if required
resource "aws_iam_access_key" "user" {
  user  = "${aws_iam_user.user.name}"
  count = "${local.create_iam_access_key}"
}
