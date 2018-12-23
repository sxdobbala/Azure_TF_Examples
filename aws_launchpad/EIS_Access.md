# EIS Access to AWS Accounts

## Single Sign On Links

* AWS POC SSO : https://signontest.uhg.com/ping/AWSRouterSSO.asp
* AWS Enterprise SSO : https://signon.uhg.com/ping/AWSRouterSSO.asp

## Description

Enterprise Information Security (EIS) team needs access to *all* AWS accounts. There will be a dedicated Security Account per master account to be used for security related tasks:

| Environment | Cloud ID | Master Account # | Security Account # |
|-|-|-|-|
| POC | AWS_0000069 | 505641934058 | 614817484114 |
| Enterprise | AWS_0000027 | 657169126488 | 494528487614 |

Specifically they need two types of access: (1) *read-only* or non-privileged access and (2) *full-control/break glass* or privileged access.

These access privileges are provisioned through two AWS IAM roles. Members of the role `READ` will have read-only access and members of the role `BREAKGLASS` will have full access to *all* the resources in any specific AWS account. All AWS accounts will have these two roles created and maintained by the Launchpad.

Membership in the roles `READ` and `BREAKGLASS` are controlled by two corresponding Secure global groups. Any approved EIS team member in the global group `READ` can use their MS **Primary** ID to login to a security account and assume the `READ` role in any of the accounts in that environment (Enterprise or POC). Similarly, any approved EIS team member in the global group `BREAKGLASS` can use their MS **Secondary** ID to login to a security account and assume the `BREAKGLASS` role in any of the accounts in that environment (Enterprise or POC). This design is chosen for these two reasons: (1) to be able to quickly provision and, most importantly, de-provision users to the security roles, (2) to eliminate the administrative burden of adding two AD global groups per account and maintaining them, and (3) to relieve the users to have to select an account from a long list of accounts when they login to the AWS console. Note for the creation and usage of the MS **Secondary** ID, please follow these [steps](http://helpdesk.uhg.com/At_Your_Service/SW/Pages/Create-a-New-Group-Mailbox.aspx) to set up an email linked to the **Secondary** ID. This is needed for the SSO page.

**Note:** Global group names and AWS role names used in the description above indicate their *function*, actual names are dependent on the environment as shown below:

| Environment | Read-only Role & GG Names | Break-glass Role && GG Names | 
|-|-|-|
| POC | AWS_614817484114_EISRead | AWS_614817484114_EISBreakGlass |
| Enterprise | AWS_494528487614_EISRead | AWS_494528487614_EISBreakGlass |

Both of these roles in every account will be configured with AWS IAM Trust policies to allow members of the corresponding global groups to be able to assume that role *from* the security account in that environment (EA or POC).

Both of these roles in every account will also be configured with AWS IAM Permission policies to allow read-only - with AWS IAM managed policy `ReadOnlyAccess` *(see below) - and full access - with the AWS IAM managed policy `PowerUserAccess` - privileges to the members of the corresponding global groups in that environment (EA or POC).

* It is felt that AWS IAM managed policy `ReadOnlyAccess` is [more permissive than it needed to be](https://alestic.com/2015/10/aws-iam-readonly-too-permissive/). So it is further resticted with the following custom policy, named `EnterpriseSecurityReadOnlyAccessAddendum`:

```javascript
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "eis_readonly_access_addendum_policy",
            "Action": [
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
                "sqs:ReceiveMessage"
            ],
            "Effect": "Deny",
            "Resource": "*"
        }
    ]
}
```

## Setup

Follow this process to setup an EIS team member with access to AWS accounts:

| Environment | Read-only Access | Break-glass Access | 
|-|-|-|
| POC | Add the team member's **primary** MS ID to *AWS_614817484114_EISRead* Secure global group| Add the team member's **secondary** MS ID to *AWS_614817484114_EISBreakGlass* Secure global group |
| Enterprise |  Add the team member's **primary** MS ID to *AWS_494528487614_EISRead* Secure global group| Add the team member's **secondary** MS ID to *AWS_494528487614_EISBreakGlass* Secure global group |

## How to access AWS accounts

You can see our SwitchRoleAWS visual markdown [here](SwitchRoleAWS.md) or follow the steps below. 

### To access accounts in the POC environment with read-only privileges:

1. Log into the EIS AWS security account 614817484114 in the POC environment by using your primary MS ID on the AWS console. Select the role *AWS_614817484114_EISRead*.

2. Once inside the central security account, you can access any other account in the POC environment by selecting the drop-down box next to the currently logged in role name, EIS_AWS_Read (third drop-down from top righ corner). Select 'Switch Role' menu item in the drop-down, click on the 'Switch Role' button in the next screen, enter the account number and 'EIS_AWS_Read' as the role name, and then click on the 'Switch Role' to access that account.

### To access accounts in the POC environment with breakglass privileges:

1. Log into the EIS AWS security account 614817484114 in the POC environment by using your **secondary** MS ID on the AWS console. Select the role *AWS_614817484114_EISBreakGlass*.

2. Once inside the central security account, you can access any other account in the POC environment by selecting the drop-down box next to the currently logged in role name, EIS_AWS_BreakGlass (third drop-down from top righ corner). Select 'Switch Role' menu item in the drop-down, click on the 'Switch Role' button in the next screen, enter the account number and 'EIS_AWS_BreakGlass' as the role name, and then click on the 'Switch Role' to access that account.

### To access accounts in the Enterprise environment with read-only privileges:

1. Log into the EIS AWS security account 494528487614 in the enterprise environment by using your primary MS ID on the AWS console. Select the role *AWS_494528487614_EISRead*.

2. Once inside the central security account, you can access any other account in the enterprise environment by selecting the drop-down box next to the currently logged in role name, EIS_AWS_Read (third drop-down from top righ corner). Select 'Switch Role' menu item, click on the 'Switch Role' button in the next screen, enter the account number and 'EIS_AWS_Read' as the role name, and then click on the 'Switch Role' to access that account.

### To access accounts in the Enterprise environment with breakglass privileges:

1. Log into the EIS AWS security account 494528487614 in the enterprise environment by using your **secondary** MS ID on the AWS console. Select the role *AWS_494528487614_EISBreakGlass*.

2. Once inside the central security account, you can access any other account in the enterprise environment by selecting the drop-down box next to the currently logged in role name, EIS_AWS_BreakGlass (third drop-down from top righ corner). Select 'Switch Role' menu item, click on the 'Switch Role' button in the next screen, enter the account number and 'EIS_AWS_BreakGlass' as the role name, and then click on the 'Switch Role' to access that account.