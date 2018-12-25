## v1.7.3 (December 17, 2018)
BUG FIXES:
* Allow return traffic from the internet even when allow_public_inbound_http is set to false
* Don't use allow_public_inbound_http variable to determine if we create an internet gateway

## v1.7.2 (November 30, 2018)

IMPROVEMENTS:
* Added Flow Logs to VPC Module. When a VPC creates a Flow Log, it attaches an existing CloudWatch Log Group created by Launchpad, with   a Subscription Filter to a Log Destination in the Launchpad's Central Management Account.
* FlowLogs to capture IP traffic across all ENI's within VPC

BREAKING CHANGES:

* Removed `aws_profile` as a variable for all modules


## v1.7.1 (September 28, 2018)

EIS Endorsed - September 2018

IMPROVEMENTS:
* Add PR template

## v1.7.0 (September 27, 2018)
BREAKING CHANGES:
* Updated flow log module so that vpc_id no longer uses default vpc if non is passed.  It is now a required field.

## v1.6.0 (May 22,2018)
BREAKING CHANGES:
* Updated VPC module so no internet gateway, s3 endpoint, and dynamodb endpoint are created by default

## v1.5.2 (May 15,2018)
FEATURES:
* Support for Endpoints(Interface type).Fixes #14

BUG FIXES:
* Fix ICMP black hole. Fixes #15

## v1.5.1 (May 14, 2018)
IMPROVEMENTS:
* Added EIS Security Endorsement documentation to README

## v1.5.0 (May 11, 2018)

BREAKING CHANGES:

* Add `aws_profile` as a _required_ variable for all modules

## v1.4.1 (Apr 13,2018 )
IMPROVEMENTS:
* Added Innersource guidelines

BUG FIXES:
* Added fix to enforce splat syntax in outputs 

## v1.4.0 (Apr 6,2018 )
IMPROVEMENTS:
* Separate subnet module for adding additional subnets.Fixes #11
* Support for flexible tagging 
* Better support for endpoint association  

## v1.3.1 (Mar 12, 2018 )
BUG FIXES:
* FlowLogs : Address an issue where CloudWatch log group is not destroyed
* VPC : Removing Route table association from VPC Endpoint resource to avoid conflicts
* VPC : Ignore changes to the subnets tags  

## v1.3.0 (Mar 6, 2018 )

FEATURES:
* Support for Flow logs 

IMPROVEMENTS:
* Example to integrate flow logs into VPC
* Improved Rspec tests

## v1.2.2 (Feb 6, 2018)

IMPROVEMENTS:
* Updated references to use new CommercialCloud-EAC Github Org

## v1.2.1 ( 12, 2017)
* fix for endpoints 
* Minor enhancements to VPC output

## v1.2.0 ( 12, 2017)
Support VPC Endpoints & Egress proxy

FEATURES:
* Egress module to secure & audit outbound internet access
* Endpoints to support private access to S3 & DynamoDB on same region 
* Rspec test for module 


## v1.1.1 ( 10, 2017)
Minor fix 

## v1.0.0 ( 10, 2017)
Base VPC initial release

FEATURES:
* VPC with Private & Public Subnets 
* NAT Gatways for outbound Internet access 
* Security Group & Network Access Control list to secure VPC
