# iam-role
### Overview
> An IAM role is similar to a user, in that it is an AWS identity with permission policies that determine what the identity can and cannot do in AWS. However, instead of being uniquely associated with one person, a role is intended to be assumable by anyone who needs it. Also, a role does not have standard long-term credentials (password or access keys) associated with it. Instead, if a user assumes a role, temporary security credentials are created dynamically and provided to the user. For more details, [see this document](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles.html).

#### IAM Role Properties
The following properties are available in the role module:

|IAM Role Property|Description              |Default|
|----------------|-------------------------------|------------------|
|name       | Name of the role| default-role{-namespace}|
|description| Text description of the role| `null` |
|path| The path to the role| `/` |
|assume_role_aws_principals    |AWS principals that can assume the role (ie. AWS users)| `null`|
|assume_role_service_principals|Service principals that can assume the role (ie. service users)| `null`|
|assume_role_federated_principals|Federated principals that can assume the role (ie. identities that were federated with SAML)| `null`|
|custom_managed_policy  | List of ARNs for custom managed policy to add to the role| `[]`|
|custom_managed_policy_count| Count of custom managed policies to add to the role| `0`|
|custom_inline_policy  | List of inline policies to add to the role; each list element must contain: custom_inline_name, custom_inline_policy| `null`|
|custom_inline_policy_count| Count of custom inline policies to add to the role| `0`|
|namespace|Name space for all resources| `null` |

**NOTE:** At least 1 assume_role_aws_principals or assume_role_service_principals or assume_role_federated_principals is required to create a role.

*A note about paths*: Paths help us emulate a simple namespacing mechanism like a directory structure. They also provides us with a mechanism to apply policies based on paths. However, it should be noted that the paths themselves don't afford any specific rights solely based on the structure alone. Access control is managed by permissions only and it is very much possible for two entities that don't share any path structure to have the same set of permissions. See [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html) for more details.

#### IAM Role Outputs

The following role outputs are available:

|Output        |Description           |
|--------------|----------------------|
|name |Name of the role |
|id |Id of the role |
|arn  |AWS resource name of the role |

**Note:** It can take several minutes before IAM roles are available for use.

#### Examples

Examples are included in this repository, for more information see the examples folder.
