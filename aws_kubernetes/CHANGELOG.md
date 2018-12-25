## v1.6.0 (November 19,2018)
* Upgrade Kubernetes to `1.10.11`,Docker to `17.03.2`, etcd version `3.1.17` 
* Support for Amazon Linux 2 , Debian is not supported anymore
* Support EBS optimized if EC2 instance supports it 
* ASG supports LB health checks and connection draining 
* AWS session manager polices are added 

IMPORTANT : This version has the patch for [Kubernetes security flaw](https://elastisys.com/2018/12/04/kubernetes-critical-security-flaw-cve-2018-1002105/) (CVE-2018-1002105)

## v1.5.1 (September 28, 2018)

EIS Endorsed - September 2018

IMPROVEMENTS:
* Add PR pipeline for Jenkins
* Add PR template

## v1.5.0 (Sep 25,2018)
IMPROVEMENTS:
* Support for Cluster Autoscaler to automatically adjusts the size of the Kubernetes cluster [BREAKING CHANGE: variable `node_count` is replaced by `node_count_max` and `node_count_min`]
* Remove provider block from the module [BREAKING CHANGE: variable `aws_region` is not needed]

FIXES:
* Fix SSM agent systemd name change

## v1.4.1 (May 29,2018)
IMPROVEMENTS:
* Security : Kubernetes API can only be accessed from Optum Network  & Within VPC.

## v1.4.0 (Apr 27,2018)
IMPROVEMENTS:
* Simplified work flow - everything is done via Terraform . No additional scripts to run 
* Subnets are managed independently. We will not create subnets as part of this module
* Reserve compute resources for system daemons
* Update ETCD version from 2.2.1 to 3.1.11
* Use of global_tags for better tag management
* Remove Route53 records created by Kubernetes as part of `terraform destroy`
* Quick start example 

## v1.3.2 (Feb 20,2018)
* Kubeconfig file will not be stored in S3 as a security measure 
* Fix for #32, Change in subnet CIDR calculation logic
* Default encryption for S3 bucket 

## v1.3.1 (Feb 6, 2018)

IMPROVEMENTS:
* Updated git references to use the new CommercialCloud-IAC Github Org

## v1.3.0 (12, 2017)
Secure Kubernetes

FEATURES:
* Kubernetes Version is upgraded to `1.8.4`
* Supports Egress proxy with a flag `-e` to control and audit outbound Internet access
* Strict RBAC for API access
* Audit controls: records of actions taken with in the cluster 
* ETCD storage is now encrypted by default 
* Support for VPC gateway endpoints for S3 & DynamoDB


## v1.2.0 ( 10, 2017)
Major update to PEKOPS

FEATURES:
* Kubernetes Version is upgraded to `1.7.8` from `1.6.2`. This includes patch for vulnerability [CVE-2017-14491] in `dnsmasq` used in `kube-dns`
* Support for Ubuntu along with Debian  with flag `-o`
* Support for user provided AMI's with flag `-i`
* AWS Inspector is included in the default Ubuntu AMI and all instances come with tags to create logical targets for assessment. Issue #3
* Validates user access level before provisioning the AWS resources. Fix for #17
* Added rspec test suite #8
* Storage classes are updated to support encryption by default and now comes with  `general_purpose_ssd`(General purpose SSD volume that balances price and performance for a wide variety of transactional workloads) & `provisioned_iops_ssd` (Highest-performance SSD volume designed for mission-critical applications)
* Cleanup documentation and small fixes

## v1.1.2 ( 10, 2017)
Updated the Debian Linux version to 4.4.78-k8s to fix the bug

## v1.1.1 ( 9, 2017)
PEKOPS 1.1.1 continue to provision Kubernetes 1.6.2 in Debian based AMI

FEATURES:

*	PEKOPS now requires VPC to be available. README has instructions to create new VPC if none exist 
*	Kubernetes cluster is now highly secure with removal of SSH and custom NACL rules for private subnets 
*	Kubernetes AMI now has SSM agent to replace SSH 
*	Kubernetes AMI now has OSSEC agent & AWS log agent to send security alerts to cloud watch
*	Lot of bug fixes . More information on issue log


## v1.0.0 ( 8, 2017)
This is the first release to support running Kubernetes on AWS with Terraform modules.

FEATURES:

* Automates the provisioning of Kubernetes clusters in (AWS) using Terraform Modules
* PEKOPS CLI to manage cluster configurations
* Deploys Highly Available (HA) Kubernetes Masters
* Creates VPC as part of the cluster 
