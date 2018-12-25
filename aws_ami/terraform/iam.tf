resource "aws_iam_role" "packer_builder" {
  name               = "packer_builder.${var.aws_region}"
  assume_role_policy = "${data.aws_iam_policy_document.assume_role_packer_builder.json}"
}

resource "aws_iam_instance_profile" "packer_builder" {
  name = "packer_builder.${var.aws_region}"
  role = "${aws_iam_role.packer_builder.name}"
}

data "aws_iam_policy_document" "assume_role_packer_builder" {
  statement {
    effect = "Allow"

    actions = [
      "sts:AssumeRole",
    ]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy" "packer_builder" {
  name   = "packer_builder.${var.aws_region}"
  role   = "${aws_iam_role.packer_builder.name}"
  policy = "${data.aws_iam_policy_document.packer_builder.json}"
}

data "aws_iam_policy_document" "packer_builder" {
  statement {
    effect    = "Allow"
    actions   = ["ec2:*"]
    resources = ["*"]
  }

  statement {
    effect    = "Allow"
    actions   = ["s3:*"]
    resources = ["*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "ssm:DescribeAssociation",
      "ssm:GetDeployablePatchSnapshotForInstance",
      "ssm:GetDocument",
      "ssm:GetParameters",
      "ssm:ListAssociations",
      "ssm:ListInstanceAssociations",
      "ssm:PutInventory",
      "ssm:PutComplianceItems",
      "ssm:UpdateAssociationStatus",
      "ssm:UpdateInstanceAssociationStatus",
      "ssm:UpdateInstanceInformation",
      "ssmmessages:CreateControlChannel",
      "ssmmessages:CreateDataChannel",
      "ssmmessages:OpenControlChannel",
      "ssmmessages:OpenDataChannel",
    ]

    resources = ["*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "ec2messages:AcknowledgeMessage",
      "ec2messages:DeleteMessage",
      "ec2messages:FailMessage",
      "ec2messages:GetEndpoint",
      "ec2messages:GetMessages",
      "ec2messages:SendReply",
    ]

    resources = ["*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "ds:CreateComputer",
      "ds:DescribeDirectories",
    ]

    resources = ["*"]
  }
}
