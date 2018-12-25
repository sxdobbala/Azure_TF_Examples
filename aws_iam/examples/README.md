# IAM Module Examples

* create_policy: Creates a IAM policy
* create_role_with_service_pricipal: Creates a IAM role, allows service principal to assume the role
* create_role_with_user_pricipal: Creates an IAM user; Creates a IAM role, allows IAM user principal to assume the role
* create_role_with_federated_pricipal: Creates a IAM role, allows a federated principal to assume the role
* create_role_with_aws_and_service_principal: Creates an IAM role with both user and service principals
* create_role_with_inline_policy: Creates an IAM role with an custom inline policy
* create_role_with_managed_policy: Creates an IAM role; creates a managed policy and assigns the policy to the role
* create_group_with_policies: Creates an IAM group with both a manage policy and an inline policy
* create_group_with_user_membership: Creates an IAM user; creates an IAM group and assigns the user to the group
* add_user_to_existing_group: Creates an IAM user; creates an IAM group and assigns the user to the group; creates additional user and adds user to already created IAM Group

**NOTE:** These examples assume terraform has been installed and AWS access keys have been configured.

#### Run the examples

```
> cd <example folder>

Update the terrraform.tfvars file with the proper AWS access and secret keys

> terraform init
> terraform plan
> terraform apply
```

Cleanup the example from AWS
```
> terraform destroy
