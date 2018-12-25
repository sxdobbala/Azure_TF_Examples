# Add a custom inline policy to the role
data "aws_iam_policy_document" "custom_inline_policy2" {
  statement {
    sid       = "AWSInlinePolicyExample2"
    actions   = ["ec2:Describe*"]
    effect    = "Allow"
    resources = ["*"]
  }
}
