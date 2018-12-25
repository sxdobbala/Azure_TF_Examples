variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-1"
}

variable "list_of_aws_az" {
  description = "The AZ for the subnets"
  type        = "list"
}

variable "vpc_id" {
  description = "The VPC id for the subnets to associate with"
}

variable "number_of_public_subnets" {
  description = "Number of public subnets to be created.Variable 'internet_gateway_id' must be provided"
  default     = 0
}

variable "internet_gateway_id" {
  description = "The id of the specific Internet Gateway to retrieve. Must when `number_of_public_subnets` > 0"
  default     = ""
}

variable "list_of_cidr_block_for_public_subnets" {
  description = "list of CIDR block for the public subnets"
  type        = "list"
}

variable "public_ip_on_launch" {
  description = "Specify true to indicate that instances launched into the subnet should be assigned a public IP address"
  default     = false
}

variable "vpc_endpoint_dynamodb_id" {
  description = "VPC dynamodb endpoint id"
  default     = ""
}

variable "vpc_endpoint_s3_id" {
  description = "VPC s3 endpoint id"
  default     = ""
}

variable "associate_dynamodb_endpoint_with_public_route_table" {
  description = "Specify true to indicate that instances launched into the public subnet should have route to dynamodb"
  default     = false
}

variable "associate_s3_endpoint_with_public_route_table" {
  description = "Specify true to indicate that instances launched into the public subnet should have route to s3"
  default     = false
}

variable "create_nacl_for_public_subnets" {
  description = "create seperate NACL for the public subnets.If 'false' , default NACL is assigned to the subnets"
  default     = true
}

variable "number_of_private_subnets" {
  description = "Number of private subnets to be created"
  default     = 0
}

variable "list_of_cidr_block_for_private_subnets" {
  description = "list of CIDR block for the private subnets"
  type        = "list"
}

variable "associate_nat_gateway_with_private_route_table" {
  description = "Specify true to indicate that instances launched into the private subnet should haveaccess to outbound internet"
  default     = false
}

variable "vpc_nat_gateway_ids" {
  description = "list of NAT Gateway ids to associate with private subnets.Must when `associate_nat_gateway_with_private_route_table` is true"
  type        = "list"
  default     = []
}

variable "associate_dynamodb_endpoint_with_private_route_table" {
  description = "Specify true to indicate that instances launched into the private subnet should have route to dynamodb"
  default     = false
}

variable "associate_s3_endpoint_with_private_route_table" {
  description = "Specify true to indicate that instances launched into the private subnet should have route to s3"
  default     = false
}

variable "create_nacl_for_private_subnets" {
  description = "create seperate NACL for the private subnets.If 'false', default NACL is assigned to the subnets"
  default     = true
}

variable "tag_name_identifier" {
  description = "tag name identifier"
  default     = "pe"
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
