# Add a custom inline policy to the role
data "aws_iam_policy_document" "custom_inline_policy1" {
  statement {
    sid       = "AWSInlinePolicyExample1"
    actions   = ["ec2:Describe*"]
    effect    = "Allow"
    resources = ["*"]
  }
}
