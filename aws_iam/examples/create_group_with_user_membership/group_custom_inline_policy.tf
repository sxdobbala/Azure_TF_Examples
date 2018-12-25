# Add a custom inline policy to the group
data "aws_iam_policy_document" "custom_inline_policy" {
  statement {
    sid       = "GroupInlinePolicyExample"
    actions   = ["ec2:Describe*"]
    effect    = "Allow"
    resources = ["*"]
  }
}
