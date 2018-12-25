# Local variables
locals {
  version_tag = {
    "cc-eac_aws_iam" = "v2.0.0"
  }

  name = "${length(var.name) == 0 ?
                     format("%s%s",
                           length(var.name) == 0 ?
                             format("%s", "default-role")
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

  description = "${length(var.description) == 0 ?
                           format("%s", "default-description")
                           :
                           format("%s", var.description)
                         }"

  path = "${var.path}"
}

# This locals section builds the strings required to build the assumerole policy statement
locals {
  # If assumerole has service principals, then build the policy statement
  assumerole_service_principals_exists = "${(length(var.assume_role_service_principals) == 0 ? 0 : 1)}"

  assumerole_service_principals_policy_statement = "${length(var.assume_role_service_principals) == 0 ?
                                  format("%s", "")
                                  :
                                  format("%s", substr(data.aws_iam_policy_document.assumerole_service_principals_policy.json, 50, length(data.aws_iam_policy_document.assumerole_service_principals_policy.json)-56))
                                  }"

  # If assumerole has aws principals, then build the policy statement
  assumerole_aws_principals_exists = "${(length(var.assume_role_aws_principals) == 0 ? 0 : 1)}"

  assumerole_aws_principals_policy_statement = "${length(var.assume_role_aws_principals) == 0 ?
                                  format("%s", "")
                                  :
                                  format("%s", substr(data.aws_iam_policy_document.assumerole_aws_principals_policy.json, 50, length(data.aws_iam_policy_document.assumerole_aws_principals_policy.json)-56))
                                  }"

  # If assumerole has federated principals, then build the policy statement
  assumerole_federated_principals_exists = "${(length(var.assume_role_federated_principals) == 0 ? 0 : 1)}"

  assumerole_federated_principals_policy_statement = "${length(var.assume_role_federated_principals) == 0 ?
                                  format("%s", "")
                                  :
                                  format("%s", substr(data.aws_iam_policy_document.assumerole_federated_principals_policy.json, 50, length(data.aws_iam_policy_document.assumerole_federated_principals_policy.json)-56))
                                  }"
}

# This locals section builds the assumerole policy statement
locals {
  assumerole_policy_statement = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
  ${format("%s%s%s%s%s",
      local.assumerole_aws_principals_exists == 0 ? 
      format("%s", "") 
      : 
      format("%s", local.assumerole_aws_principals_policy_statement ),
      local.assumerole_aws_principals_exists * local.assumerole_service_principals_exists == 0 ? 
      format("%s", "")
        : 
        format("%s", "," ),
      local.assumerole_service_principals_exists == 0 ? 
      format("%s", "") 
      : 
      format("%s", local.assumerole_service_principals_policy_statement ),
      local.assumerole_aws_principals_exists * local.assumerole_service_principals_exists * local.assumerole_federated_principals_exists == 0 ?
        format("%s", "")
        :
        format("%s", "," ),
      local.assumerole_federated_principals_exists == 0 ?
        format("%s", "")
        :
        format("%s", local.assumerole_federated_principals_policy_statement )
      )
    }
  ]
}
EOF
}

# Create the role
resource "aws_iam_role" "role" {
  name               = "${local.name}"
  description        = "${local.description}"
  path               = "${local.path}"
  assume_role_policy = "${local.assumerole_policy_statement}"
}

# Add any managed policies to the role
resource "aws_iam_role_policy_attachment" "role-managed-policy" {
  count      = "${var.custom_managed_policy_count}"
  role       = "${aws_iam_role.role.name}"
  policy_arn = "${element(var.custom_managed_policy, count.index)}"
}

# Add any inline policy to the role
resource "aws_iam_role_policy" "custom_inline_policy" {
  count  = "${var.custom_inline_policy_count}"
  name   = "${lookup(var.custom_inline_policy[count.index], "custom_inline_name")}"
  role   = "${aws_iam_role.role.id}"
  policy = "${lookup(var.custom_inline_policy[count.index], "custom_inline_policy")}"
}
