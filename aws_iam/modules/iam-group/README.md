# iam-group
### Overview
> An IAM group is a collection of IAM users. Groups let you specify permissions for multiple users, which can make it easier to manage the permissions for those users. For more details, [see this document](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups.html).

#### IAM Group Properties
The following properties are available in the group module:

|IAM Group Property|Description              |Default|
|----------------|-------------------------------|------------------|
|name      | Name of the group| default-group{-namespace}|
|path      | Path in which the create the group|`/`|
|custom_managed_policy| ARN of a custom managed policy to add to the group| `null`|
|custom_managed_policy_count| Count of a custom managed policies to add to the group| `0`|
|custom_inline_policy | List of inline policies to add to the group; each list element must contain: group_custom_inline_name, custom_inline_policy| `null`|
|custom_inline_policy_count| Count of custom inline policies to add to the group| `0`|
|users | List of users to assign to the IAM group| `null`|
|namespace|Name space for all resources| `null` |

#### IAM Group Outputs

The following group outputs are available:

|Output        |Description           |
|--------------|----------------------|
|name |Name of the group |
|id |Id of the group |
|arn  |AWS resource name of the group |
|unique_id  |Unique id assigned by AWS for the group |

**Note:** It can take up to a few minutes before IAM groups are available for use.

*A note about paths*: Paths help us emulate a simple namespacing mechanism like a directory structure. They also provides us with a mechanism to apply policies based on paths. However, it should be noted that the paths themselves don't afford any specific rights solely based on the structure alone. Access control is managed by permissions only and it is very much possible for two entities that don't share any path structure to have the same set of permissions. See [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html) for more details.

#### Examples

Examples are included in this repository, for more information see the examples folder.
