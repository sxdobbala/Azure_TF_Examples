variable "vpc_name" {
  description = "Name for the VPC"
  default     = "pe"
}

variable "vpc_cidr" {
  description = "Classless Inter-Domain Routing (CIDR) block for the VPC"
  default     = "10.0.0.0/16"
}

variable "aws_region" {
  description = "AWS region create resources"
}

variable "aws_azs" {
  description = "AWS availability zones within the single region where Subnets are created.if not specified all AZ with in the regions are selected by default"
  type        = "list"
  default     = [""]
}

variable "vpc_enable_dns_hostnames" {
  description = "Indicates whether the DNS resolution is supported for the VPC"
  default     = true
}

variable "vpc_enable_dns_support" {
  description = "Indicates whether the instances launched in the VPC get public DNS host names"
  default     = true
}

variable "internet_gateway_enabled" {
  description = "Indicates whether the VPC requires Gateway for internet. When set to 'true', public subnets will be created"
  default     = false
}

variable "nat_enabled" {
  description = "Indicates whether the instances in a private subnet to connect to the Internet using NAT Gateways.Requires `internet_gateway_enabled` to be true. Use [egress_proxy](https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy) for controlled Internet access for private subnets. By default no outbound internet access from Private subnets"
  default     = false
}

variable "enable_dynamodb_endpoint" {
  description = "Indicates whether to provision an DynamoDB endpoint to the VPC"
  default     = false
}

variable "enable_s3_endpoint" {
  description = "Indicates whether to provision an S3 endpoint to the VPC"
  default     = false
}

variable "public_subnets_cidr" {
  description = "CIDR for public subnets.This should be selected only if AWS availability zones [aws_azs] are selected and custom CIDR is needed.Subnet list length must match availability zones.CIDR of subnets cannot overlap and should be within the VPC CIDR range.Do not change the default value if you are not sure.if not specified, CIDR  for each subnet is calculated with maximum of 4094(/20 mask) address"
  type        = "list"
  default     = [""]
}

variable "private_subnets_cidr" {
  description = "CIDR for private subnets.This should be selected only if AWS availability zones [aws_azs] are selected and custom CIDR is needed.Subnet list length must match availability zones.CIDR of subnets cannot overlap and should be within the VPC CIDR range.Do not change the default value if you are not sure.if not specified, CIDR for each subnet is calculated with maximum of 4094(/20 mask) address"
  type        = "list"
  default     = [""]
}

variable "enable_public_ip_on_launch" {
  description = "Specify true to indicate that instances launched into the subnet should be assigned a public IP address"
  default     = false
}

variable "associate_dynamodb_endpoint_with_public_subnets" {
  description = "Specify true to indicate that public subnet should have private route to dynamodb"
  default     = false
}

variable "associate_s3_endpoint_with_public_subnets" {
  description = "Specify true to indicate that public subnet should have private route to s3"
  default     = false
}

#NACL
variable "enable_nacl" {
  description = "Enable NACL for the public and private subnets.Setting this to false make VPC to use default NACL and that explicitly allows all inbound and outbound traffic"
  default     = true
}

variable "allow_public_inbound_http" {
  description = "Allows inbound HTTP traffic from any IPv4 address to public subnets"
  default     = true
}

variable "allow_public_inbound_https" {
  description = "Allows inbound HTTPS traffic from any IPv4 address to public subnets"
  default     = true
}

variable "allow_public_inbound_response_to_internet" {
  description = "Allows inbound return traffic from hosts on the Internet that are responding to requests originating in the subnets"
  default     = true
}

variable "allow_outbound_http_from_public_subnet" {
  description = "Allows outbound HTTP traffic from the public subnets to the Internet"
  default     = true
}

variable "allow_outbound_https_from_public_subnet" {
  description = "Allows outbound HTTPS traffic from the public subnets to the Internet"
  default     = true
}

variable "allow_outbound_response_to_internet_from_public_subnet" {
  description = "Allows outbound responses to clients on the Internet (for example, serving web pages to people visiting the web servers in the subnet)"
  default     = true
}

# fix for https://github.optum.com/CommercialCloud-EAC/aws_vpc/issues/15
variable "allow_public_inbound_icmp_for_pmtu_discovery" {
  description = "ICMP rule to allow path MTU discovery to work"
  default     = true
}

variable "allow_inbound_http_from_vpc_subnets" {
  description = "Allows inbound HTTP traffic from any subnet within VPC"
  default     = true
}

variable "allow_inbound_https_from_vpc_subnets" {
  description = "Allows inbound HTTPS traffic from any subnet within VPC"
  default     = true
}

variable "allow_private_inbound_return_traffic_via_nat" {
  description = "Allows inbound return traffic from the NAT device in the public subnet for requests originating in the private subnet"
  default     = true
}

variable "allow_outbound_http_from_private_subnet" {
  description = "Allows outbound HTTP traffic from the private subnet to the Internet"
  default     = true
}

variable "allow_outbound_https_from_private_subnet" {
  description = "Allows outbound HTTPS traffic from the private subnet to the Internet"
  default     = true
}

variable "allow_outbound_response_to_internet_from_private_subnet" {
  description = "Allows outbound responses to the public subnet (for example, responses to web servers in the public subnet that are communicating with DB servers in the private subnet)"
  default     = true
}

variable "tag_name_identifier" {
  description = "tag name identifier for the aws_base"
}

variable "other_public_subnet_tags" {
  description = "Addtioanl public subnet tags to be applied to created resources"
  type        = "map"
  default     = {}
}

variable "other_private_subnet_tags" {
  description = "Addtioanl private subnet tags to be applied to created resources"
  type        = "map"
  default     = {}
}

variable "global_tags" {
  description = "Addtioanl global tags to be applied to created resources"
  type        = "map"

  default = {
    "terraform" = "true"
  }
}
