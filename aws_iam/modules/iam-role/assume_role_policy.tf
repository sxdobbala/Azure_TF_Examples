# Default policy attached to the role; this is overwritten if a policy is passed into the module
# data "aws_caller_identity" "current" { }

# Create a list to contain the users that should assume this role
data "aws_iam_policy_document" "assumerole_aws_principals_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${var.assume_role_aws_principals}"]
    }
  }
}

data "aws_iam_policy_document" "assumerole_service_principals_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["${var.assume_role_service_principals}"]
    }
  }
}

data "aws_iam_policy_document" "assumerole_federated_principals_policy" {
  statement {
    actions = ["sts:AssumeRoleWithSAML"]
    effect  = "Allow"

    principals {
      type        = "Federated"
      identifiers = ["${var.assume_role_federated_principals}"]
    }
  }
}
