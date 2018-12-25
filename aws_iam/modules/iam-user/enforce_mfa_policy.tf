# This policy allows the user to log into the console and setup their MFA
# This policy restricts users for doing any activity unless they log in using MFA, enforcing MFA
data "aws_iam_policy_document" "enforce_mfa" {
  statement {
    sid    = "AllowAllUsersToListAccounts"
    effect = "Allow"

    actions = [
      "iam:ListAccountAliases",
      "iam:ListUsers",
    ]

    resources = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/*"]
  }

  statement {
    sid    = "AllowIndividualUserToSeeAndManageOnlyTheirOwnAccountInformation"
    effect = "Allow"

    actions = [
      "iam:ChangePassword",
      "iam:GetAccountPasswordPolicy",
      "iam:GetLoginProfile",
      "iam:UpdateLoginProfile",
    ]

    resources = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/&{aws:username}"]
  }

  statement {
    sid    = "AllowIndividualUserToListOnlyTheirOwnMFA"
    effect = "Allow"

    actions = [
      "iam:ListVirtualMFADevices",
      "iam:ListMFADevices",
    ]

    resources = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:mfa/*",
      "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/&{aws:username}",
    ]
  }

  statement {
    sid    = "AllowIndividualUserToManageTheirOwnMFA"
    effect = "Allow"

    actions = [
      "iam:CreateVirtualMFADevice",
      "iam:DeleteVirtualMFADevice",
      "iam:RequestSmsMfaRegistration",
      "iam:FinalizeSmsMfaRegistration",
      "iam:EnableMFADevice",
      "iam:ResyncMFADevice",
    ]

    resources = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:mfa/&{aws:username}",
      "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/&{aws:username}",
    ]
  }

  statement {
    sid     = "AllowIndividualUserToDeactivateOnlyTheirOwnMFAOnlyWhenUsingMFA"
    effect  = "Allow"
    actions = ["iam:DeactivateMFADevice"]

    resources = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:mfa/&{aws:username}",
      "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/&{aws:username}",
    ]

    condition {
      test     = "Bool"
      variable = "aws:MultiFactorAuthPresent"
      values   = ["true"]
    }
  }

  statement {
    sid         = "BlockAnyAccessOtherThanAboveUnlessSignedInWithMFA"
    effect      = "Deny"
    not_actions = ["iam:*"]
    resources   = ["*"]

    condition {
      test     = "BoolIfExists"
      variable = "aws:MultiFactorAuthPresent"
      values   = ["false"]
    }
  }
}
