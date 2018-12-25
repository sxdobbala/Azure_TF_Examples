# aws-policy
### Overview
> You manage access in AWS by creating policies and attaching them to IAM identities or AWS resources. A policy is an object in AWS that, when associated with an entity or resource, defines their permissions. AWS evaluates these policies when a principal, such as a user, makes a request. Permissions in the policies determine whether the request is allowed or denied. For more details, [see this document](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html).

##### IAM Policy Properties
The following properties are available in the policy module:

|IAM Policy Property|Description                 |Default|
|----------------|-------------------------------|------------|
|name     | Name of the policy|default{-namespace}|
|path     | Path in which to create the policy|`/`|
|description| Description of the policy|`default-policy-description`|
|document | JSON policy document|(Required and there is no default)|
|namespace|Name space for all IAM policy resources| `null` |

##### IAM Policy Outputs
|Output|Description|
|----------------|---------------|
|name|Name of the policy|
|id|ID of the policy|
|arn|ARN of the policy|

*A note about paths*: Paths help us emulate a simple namespacing mechanism like a directory structure. They also provides us with a mechanism to apply policies based on paths. However, it should be noted that the paths themselves don't afford any specific rights solely based on the structure alone. Access control is managed by permissions only and it is very much possible for two entities that don't share any path structure to have the same set of permissions. See [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html) for more details.

#### Examples

Examples are included in this repository, for more information see the examples folder.

