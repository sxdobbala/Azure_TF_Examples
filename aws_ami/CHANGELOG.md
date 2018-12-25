## v1.2.1-beta1 (Dec 8, 2018)
Fix:
* Better AMI filtering as now AWS publishes arm architecture supported AMI’s

## v1.2.0-beta1 (Dec 2, 2018)

* Support AMI's for [aws_eks](https://github.optum.com/CommercialCloud-EAC/aws_eks) and [aws_egress_proxy](https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy)
* Support for AMI encryption 
* OSSEC version updated to 3.0.0
* Create resources in custom VPC instead of default VPC, update worker EC2 image to linux2 
* Improved NACL and SG rules
* Lot of cleanup

> Not EIS Endorsed

## v1.1.2 (September 28, 2018)

EIS Endorsed - September 2018

IMPROVEMENTS:
* Add PR pipeline for Jenkins
* Add PR template

## v1.1.1 (Feb 6, 2018)

IMPROVEMENTS:
* Update to use new CommercialCloud-EAC Github Org

## v1.1.0 ( 12, 2017)
Support Egress proxy

FEATURES:
* Support egress proxy aware agents like aws logs & SSM 
* Update patching and remove root login access 
* Schedule for Jenkins pipeline


## v1.0.0 ( 10, 2017)
Initial release

FEATURES:
* On Premise Jenkins pipeline to create and distribute Amazon machine images
* Packer, Terraform and Remote execution scripts to manage the AMI setup 
* SSM to execute remote commands, no SSH
* Latest Ubuntu & Debian images are updated with SSM,Cloud watch Log, OSSEC & Inspector agents
