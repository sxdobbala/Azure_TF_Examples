data "aws_iam_policy_document" "lambda_audit_ec2_exec_policy" {
  statement {
    effect = "Allow"

    actions = [
      "sts:assumerole",
    ]

    resources = ["*"]
  }
}

resource "aws_s3_bucket_object" "upload_lambda_audit_ec2" {
  bucket                 = "${module.s3-central-lambda.bucket_id}"
  key                    = "lambda_audit_ec2.zip"
  source                 = "lambda_functions/lambda_audit_ec2.zip"
  server_side_encryption = "AES256"
}

module "lambda_audit_ec2" {
  source                          = "git::https://github.optum.com/CommercialCloud-EAC/aws_lambda.git//terraform_module?ref=v1.1.3"
  lambda_function_name            = "lambda_audit_ec2"
  lambda_description              = "Launchpad Lambda Function to Audit EC2 Instances"
  lambda_file_s3_bucket           = "${var.lambda_s3_bucket}"
  lambda_file_s3_key_zip          = "lambda_audit_ec2.zip"
  lambda_exec_custom_policy_count = 1

  lambda_exec_custom_policy = [
    {
      role_custom_inline_policy_name = "LambdaAuditEC2Policy"
      role_custom_inline_policy      = "${data.aws_iam_policy_document.lambda_audit_ec2_exec_policy.json}"
    },
  ]

  global_tags = "${var.global_tags}"
  lambda_tags = "${var.lambda_audit_ec2_tags}"
}
