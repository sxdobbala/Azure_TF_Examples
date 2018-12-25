data "aws_iam_policy_document" "managed-policy" {
  statement {
    effect = "Deny"

    actions = [
      "cloudformation:GetTemplate",
      "dynamodb:GetItem",
      "dynamodb:BatchGetItem",
      "dynamodb:Query",
      "dynamodb:Scan",
      "ec2:GetConsoleOutput",
      "ec2:GetConsoleScreenshot",
      "ecr:BatchGetImage",
      "ecr:GetAuthorizationToken",
      "ecr:GetDownloadUrlForLayer",
      "kinesis:Get*",
      "lambda:GetFunction",
      "logs:GetLogEvents",
      "s3:GetObject",
      "sdb:Select*",
      "sqs:ReceiveMessage",
    ]

    resources = ["*"]
  }
}
