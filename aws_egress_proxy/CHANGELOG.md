## v1.3.1 (Dec 8,2018)
BUG FIXES:
* Better AMI filtering as now AWS publishes arm architecture supported AMI's

## v1.3.0 (Oct 12,2018)
IMPROVEMENTS:
* Change AMI to Amazon Linux2 
* Support for private images [Needed for encrypted AMI's]
* Support for AWS session manager
* ASG ELB health check and enable access logging for S3 bucket 

BREAKING CHANGES:
* Refactor modules to "2.x format"

## v1.2.0 (September 27, 2018)
BREAKING CHANGES:
* Updated module so that vpc_id no longer uses default vpc if none is passed.  vpc_id is now a required field.

## v1.1.1 (Aug 30, 2018)

BUG FIXES:

* Fix missing squid_proxy.txt file, on bucket name change
  (see #11)


## v1.1.0 ( Apr 09,2018)
IMPROVEMENTS:
* SSM role added to support SSM API's 
* improved tags 

BUGFIXES:
* Fix the issue with create log group permission

## v1.0.3 ( Mar 12,2018)
BUGFIXES:
* Address an issue where CloudWatch log group is not destroyed

## v1.0.2 ( Mar 8, 2018)

IMPROVEMENTS:
Updated IAM(1.0.3) & S3(1.2.0) modules 

BUGFIXES:

* Output fix for S3 bucket
* Fix indentation

## v1.0.1 ( Feb 6, 2018)

IMPROVEMENTS:
* Updated references to new CommercialCloud-EAC Github Org

## v1.0.0 ( 12, 2017)
This is the first release to support egress proxy for secure outbound access.

FEATURES:

Module creates the following :

* Fleet of Squid proxy instances in all the public availability zones linked to Auto Scaling Group. EC2 instance has the IAM role to access selected S3 bucket ,Cloud watch logs & metrics
* An Auto Scaling launch configuration that will install the required services - Squid with default configurations, Log agent, Code Deploy & Inspector agent. Launch configuration also installs Cron job to collect the squid metrics and send them to Cloud Watch Metrics
* Creates an internal ELB with proxy_policy to distributes TCP requests across multiple Squid proxy instances. ELB is also attached to the Auto scaling group 
* A Security group is assigned to Proxy instances that has inbound only access from ELB and Security group assigned to ELB allows only access from private instances 
* Deployment group, application and IAM role for Code Deploy to revise the squid configurations. Code deployment is integrated with Auto scaling group as well to make sure all new instances starts with the latest configuration
* Optional Auto Scaling policies to increase or decrease the number of instances based on the Cloud Watch alarms depending on the total Squid traffic
* Cloud watch log group to capture the outbound access events 
* Creates an S3 bucket to host the artifacts for Code deploy and update the Squid URL after creating them 
