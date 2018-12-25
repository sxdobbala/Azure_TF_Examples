/*
* User defined configurations
*/

// Note: Select the AWS region to launch resourses.Default is "us-east-2"
aws_region = "us-east-1"

//AWS account name
aws_account = "PE-dev"

//Specify the range of IPv4 addresses for the VPC in the form of CIDR block for the VPC
vpc_cidr = "10.0.0.0/16"

//Indicates whether the DNS resolution is supported for the VPC.Default is true
#vpc_enable_dns_support = false

//Indicates whether the instances launched in the VPC get public DNS hostnames.Default is true
#vpc_enable_dns_hostnames = false

//Indicates whether the VPC requires Gateway for internet.Default is false
internet_gateway_enabled = true

//Indicates whether the instances in a private subnet to connect to the Internet or other AWS services via NAT.Default is true
#nat_enabled = false

//Optional :: Specify the AWS availability zones, make sure they are available within the selected region
//if not selected all AZ with in the regions are selected by default

aws_azs = ["us-east-1a", "us-east-1b", "us-east-1c"]

//Advanced and Optional :: if you are not sure about this option, please ignore. You have been warned...
//This should be selected only if AWS availability zones [aws_azs] are selected and custom CIDR is needed for your subnets
//Subnet list length must match availability zones
//CIDR blocks of the subnets cannot overlap and should be within the VPC CIDR range 

#public_subnets_cidr  = ["10.0.0.0/20", "10.0.16.0/20", "10.0.32.0/20" ]
#private_subnets_cidr = ["10.0.48.0/20", "10.0.64.0/20", "10.0.80.0/20" ]

//If you do not wish to use NACL enabled , uncomment. You will be left with default NACL and that explicitly allows all inbound and outbound traffic
//AWS recommends to use both NACL & Security group together for enhanced security

#enable_nacl = false

//Below NACL rules are based on AWS recommendations for public and private subnets , feel free to add more rules to the NACLs by referring NACL id in output variables.Be mindful of rule numbe
// http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Appendix_NACLs.html
//All rules are enables by default. Uncommnet to disable

#allow_public_inbound_http = false
#allow_public_inbound_https = false
#allow_public_inbound_response_to_internet = false
#allow_outbound_http_from_public_subnet = false
#allow_outbound_https_from_public_subnet = false
#allow_outbound_response_to_internet_from_public_subnet = false

#allow_inbound_http_from_vpc_subnets = false
#allow_inbound_https_from_vpc_subnets = false
#allow_private_inbound_return_traffic_via_nat = false
#allow_outbound_http_from_private_subnet = false
#allow_outbound_https_from_private_subnet = false
#allow_outbound_response_to_internet_from_private_subnet = false

//Optional :: Specify the tag name identifier for the aws_base, default is "common_tech"
#tag_name_identifier = "optum"

enable_dynamodb_endpoint = true

enable_s3_endpoint = true
