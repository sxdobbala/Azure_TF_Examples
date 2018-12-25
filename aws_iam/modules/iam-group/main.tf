# Local variables
locals {
  version_tag = {
    "cc-eac_aws_iam" = "v2.0.0"
  }

  name = "${length(var.name) == 0 ?
                      format("%s%s",
                            length(var.name) == 0 ?
                              format("%s", "default-group")
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
}

# Create the group
resource "aws_iam_group" "group" {
  name = "${local.name}"
  path = "${local.path}"
}

# Add any managed policies to the group
resource "aws_iam_group_policy_attachment" "group-managed-policy" {
  count      = "${var.custom_managed_policy_count}"
  group      = "${aws_iam_group.group.name}"
  policy_arn = "${element(var.custom_managed_policy, count.index)}"
}

# Add any inline policy to the group
resource "aws_iam_group_policy" "custom_inline_policy" {
  count  = "${var.custom_inline_policy_count}"
  name   = "${lookup(var.custom_inline_policy[count.index], "group_custom_inline_name")}"
  group  = "${aws_iam_group.group.id}"
  policy = "${lookup(var.custom_inline_policy[count.index], "custom_inline_policy")}"
}

#Add users to the group
resource "aws_iam_group_membership" "users" {
  name  = "${local.name}"
  users = ["${var.users}"]
  group = "${aws_iam_group.group.name}"
}
