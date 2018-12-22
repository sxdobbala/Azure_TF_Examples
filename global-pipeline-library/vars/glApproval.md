# Using glApproval

The glApproval method is intended to help teams that need to insert manual approvals into their pipeline.

## Only Allowing Certain Individuals to Approve

This method can be configured to only allow approval for members of a secure group.

This is done with glApproval ... submitter: '{secure_group}'

With admin access to a Jenkins instance, navigate to:

1. Manage Jenkins

2. Configure Global Security

3. Set Authorization = Project-based Matrix Authorization Strategy

4. Add individual msid or secure group to the matrix with the "Add" button

## Avoiding Holding Up and Agent

To avoid an agent being allocated during the approval, "agent none" is typically used at the top of the Jenkinsfile.

```agent none```

The rest of your job can use the typical agent block.

```agent {label 'docker-maven-slave'}```
