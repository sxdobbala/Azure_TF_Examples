# iam-user
### Overview
> An IAM user is an entity that you create in AWS to represent the person or service that uses it to interact with AWS. A user in AWS consists of a name and credentials. For more details, [see this document](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users.html).

#### IAM User Properties:
The following properties are available in the user module:

|IAM User Property|Description              |Default|
|---------|-------------------------------|-----------|
|name       | Name of the user| default{-namespace} |
|path   | Path in which to create the user| `/`|
|force_destroy  | When destroying the user resource, don't destroy if non-terraform resources are tied to the user| `false`|
|create_iam_access_key | When set to true, generate AWS API keys| `false`|
|enforce_mfa |Enforces multi factor authentication (MFA) when the user logs in via AWS console| `true`|
|user_role_list  | List of roles that this user can assume; the role must exist before calling the user module| `null`|
|user_group_list  | List of groups that this user is assigned to; the group must exist before calling the user module| `null`|
|namespace|Name space for all IAM user resources| `null` |

**NOTE:** When changing the `path` terraform will delete and recreate the user resource.

**NOTE:** By default, console users will be required to authenticate using MFA. The user account allows the user to log in and setup MFA, but will restrict all other access until MFA has been activated.

#### IAM User Outputs

The following user outputs are available:

|Output        |Description           |
|--------------|----------------------|
|name |Name of the user |
|path|Path in which to create the user |
|arn  |The ARN assigned by AWS for this user |

*A note about paths*: Paths help us emulate a simple namespacing mechanism like a directory structure. They also provides us with a mechanism to apply policies based on paths. However, it should be noted that the paths themselves don't afford any specific rights solely based on the structure alone. Access control is managed by permissions only and it is very much possible for two entities that don't share any path structure to have the same set of permissions. See [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html) for more details.

#### Examples:

Examples are included in this repository, for more information see the examples folder.
