# Kubernetes module examples

## k8s_with_vpc
* [VPC](https://github.optum.com/CommercialCloud-EAC/aws_vpc) is created with public and private network zones across 3 different availability zones. Specific tags are added to subnets to allow Kubernetes to provision aws resources.VPC has NAT gateway disabled in favor of egress proxy 
* [Egress_Proxy](https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy) module is created for setting up secure outbound Internet access
* Deployment initiated to update egress proxy configuration, to allow outbound Internet access for specific domains .amazonaws.com,.cloudfront.net,.ubuntu.com,.debian.org,.docker.io,.dockerproject.org,.kope.io,.quay.io,.googleapis.com,.gcr.io,.newrelic.com
* SG rule is created for Egress proxy ELB SG to allow inbound access from master & worker nodes on port 3128
* [Flow Logs](https://github.optum.com/CommercialCloud-EAC/aws_vpc#flowlogs) to capture IP traffic across all ENI's within VPC

#### Create cluster 

Before you start,
* Validate the configuration in *terraform.tfvars* file.

* Supply a `k8s_route53_parent_zoneid` variable. This must be the Zone ID of a Route53 zone in your AWS account, on top of which a subdomain can be created for the k8s cluster.

* For production use,User account must have access to default AMI used in this project. To get permission to copy the AMI send a PR to [repo](https://github.optum.com/CommercialCloud-EAC/aws_ami). For poc use cases ,users can select publicly available AMI [ami_os_name=ubuntu ,ami_owner=099720109477,ami_unique_identifier=ubuntu-xenial-16.04-amd64-server]

```
terraform init
terraform plan
terraform apply
```
Upon successful completion, you should be able to validate your cluster by running below commands. If you don't have 'kubectl,' download from [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

```
kubectl get nodes
kubectl get pods -n kube-system
```

Note: 'kubeconfig' needed for 'kubectl' to authenticate is stored under "~/.kube/". Do not share kubeconfig in the wild. Use RBAC to manage service accounts with appropriate privileges. When PEKOPS setup is run in Jenkins pipeline , it is important to take a backup of ‘kubeconfig’ file as Jenkins nodes are ephemeral in nature. 


### Tearing down the cluster
```
terraform destroy
```

#### &#x1F53B; Note about Terraform State:

The S3 bucket will store Terraform [state file](https://www.terraform.io/docs/state/); the dynamodb will provide a [locking mechanism](https://www.terraform.io/docs/state/locking.html) for Terraform.Terraform bootstrap module automatically creates the required S3 bucket and dynamodb table. Please refer the module [here](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap/). Once you have the S3 bucket name and dynamodb table created ,update the `state.tf_bak` file, rename the file to `state.tf` and run the `terraform init`.

