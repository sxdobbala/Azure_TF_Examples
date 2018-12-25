# Local variables
locals {
  version_tag = {
    "cc-eac_aws_iam" = "v2.0.0"
  }

  name = "${length(var.name) == 0 ?
                     format("%s%s",
                           length(var.name) == 0 ?
                             format("%s", "default-policy")
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

  path = "${var.path}"

  description = "${length(var.description) == 0 ?
                             format("%s", "default-policy-description")
                             :
                             format("%s", var.description)
                           }"
}

# Create the policies to attach to the role
resource "aws_iam_policy" "policy" {
  name        = "${local.name}"
  description = "${local.description}"
  path        = "${local.path}"
  policy      = "${var.document}"
}
