# Kubernetes node update
This module is replacing the existing k8s nodes with updated AMI in a graceful way. When AMI is updated, running pods need to be moved out of a node so the node can be terminated. The ASG brings up a new node with the updated AMI. This will be done to all the nodes.

## Prerequisites for running kubernetes nodes update job
1. There is a new AMI built using aws-ami. Link provide here: [ct-instrumentation/aws-ami](https://github.optum.com/ct-instrumentation/aws_ami)
1. Terraform scripts in EAC have been updated to use this new AMI. Link provide here: [cloud-idp/EAC](https://github.optum.com/cloud-idp/everything-as-code/tree/preprod/terraform)


After previous changes been made, the k8s nodes need to be upgrade with latest AMI. This module takes care of that by "draining" the nodes which involves:

1. Not scheduling any new processes in the node.
1. Moving exising processes/pods from the node to other running nodes.
1. Shut down the node.

Once that is complete, the node is terminated. The AWS autoscaler will start up a new instance in its place which will have the updated AMI.

## How it works

1. All requirements are specified in the config.yml file.
1. Use Jenkins job to trigger the patch update:

```groovy
@Library('PatchManagement') _
def patchInit = new com.optum.patchmanagement.PatchManagement().init()
```

A comprehensive yaml script may look like this. 

```yml
notificationEmail: non-reply@optum.com
jenkinsNode: docker-aws-slave
awsCredentialsId: AWS_OID_DEV_SERVICE_ACCOUNT
awsAccountId: 757541135089
kubeconfigfileCredentialsId: e258c596-c2ba-491f-80c9-349204f1c93d
awsEnv: 
  - envName: dev
    autoscalingGroups: 
      - masters.k8s.dev.ctkube.com
      - nodes.k8s.dev.ctkube.com
```

## Descriptions for this config.yml file:
- notificationEmail: The person who will be notified by email when the job is completed or if failure occurs.
- jenkinsNode: The node this job will be run. The default value is docker-maven-slave.
- awsCredentialsId: The credential used by Jenkins to access AWS.
- awsAccountId: In order to log in to AWS, we need the credentials and role. The awsAccountId is needed to specify the role.
- kubeconfigfileCredentialsId: The credential used by Jenkins to get kubeconfig file for this environment.
- awsEnv: An array containing one or more environments to patch.
  - envName: The environment the Patch will be run such as Dev, Stage, Test.
  - autoscalingGroups: An array containing one or more autoscaling groups names which the nodes belong to.
