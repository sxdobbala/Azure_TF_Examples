resource "aws_iam_instance_profile" "proxy" {
  name = "${local.proxy_name}"
  role = "${module.create-role-for-ec2-proxy.role_name}"
}

module "managed-policy-for-ec2-proxy" {
  source             = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/policy?ref=v1.0.3"
  policy_name        = "${local.proxy_name}"
  policy_path        = "/"
  policy_description = "ec2 role policy for squid proxy"
  policy_document    = "${data.aws_iam_policy_document.proxy.json}"
}

module "create-role-for-ec2-proxy" {
  source                             = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.3"
  role_name                          = "${local.proxy_name}-ec2"
  role_description                   = "ec2 role for squid proxy"
  role_assumerole_service_principals = ["ec2.amazonaws.com"]
  role_custom_managed_policy_count   = 2
  role_custom_managed_policy         = ["${module.managed-policy-for-ec2-proxy.policy_arn}", "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"]

  global_tags = "${merge(map("Name", "${local.proxy_name}"),var.global_tags,local.version_tag)}"
}

module "create-role-for-codedeploy" {
  source                             = "git::https://github.optum.com/CommercialCloud-EAC/aws_iam.git//terraform_module/role?ref=v1.0.3"
  role_name                          = "${local.proxy_name}-ec2-code-deploy"
  role_description                   = "Role for code deploy"
  role_assumerole_service_principals = ["codedeploy.amazonaws.com"]
  role_custom_managed_policy_count   = 1
  role_custom_managed_policy         = ["arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"]
  global_tags                        = "${merge(map("Name", "${local.proxy_name}"),var.global_tags,local.version_tag)}"
}

data "aws_iam_policy_document" "proxy" {
  statement {
    effect = "Allow"

    actions = [
      "logs:PutLogEvents",
      "logs:CreateLogStream",
      "logs:DescribeLogStreams",
      "logs:CreateLogGroup",
    ]

    resources = ["arn:aws:logs:${var.aws_region}:${data.aws_caller_identity.current.account_id}:log-group:${var.cloudwatch_log_group_name}:log-stream:*"]
  }

  statement {
    effect    = "Allow"
    actions   = ["cloudwatch:PutMetricData"]
    resources = ["*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:Get*",
      "s3:List*",
    ]

    resources = ["arn:aws:s3:::${var.s3_bucket_name_prefix}-${data.aws_caller_identity.current.account_id}/*", "arn:aws:s3:::aws-codedeploy*"]
  }

  # for Session manager 
  statement {
    effect = "Allow"

    actions = [
      "ssmmessages:CreateControlChannel",
      "ssmmessages:CreateDataChannel",
      "ssmmessages:OpenControlChannel",
      "ssmmessages:OpenDataChannel",
    ]

    resources = ["*"]
  }
}
