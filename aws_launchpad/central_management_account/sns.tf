resource "aws_sns_topic" "launchpad_lambda_topic" {
  name = "launchpad_lambda_topic"
}

data "aws_iam_policy_document" "launchpad_lambda_topic_policy_document" {
  statement {
    sid = "LaunchpadSNSLambdaPublish"

    actions = [
      "SNS:Publish",
    ]

    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${var.lambda_client_accounts}"]
    }

    resources = [
      "${aws_sns_topic.launchpad_lambda_topic.arn}",
    ]
  }

  statement {
    sid = "LaunchpadSNSLambdaAll"

    actions = [
      "SNS:Subscribe",
      "SNS:SetTopicAttributes",
      "SNS:RemovePermission",
      "SNS:Receive",
      "SNS:ListSubscriptionsByTopic",
      "SNS:GetTopicAttributes",
      "SNS:DeleteTopic",
      "SNS:AddPermission",
    ]

    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["${data.aws_caller_identity.current.account_id}"]
    }

    resources = [
      "${aws_sns_topic.launchpad_lambda_topic.arn}",
    ]
  }
}

resource "aws_sns_topic_policy" "launchpad_lambda_topic_policy" {
  arn    = "${aws_sns_topic.launchpad_lambda_topic.arn}"
  policy = "${data.aws_iam_policy_document.launchpad_lambda_topic_policy_document.json}"
}
