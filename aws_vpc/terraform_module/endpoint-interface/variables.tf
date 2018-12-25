variable "aws_region" {
  description = "AWS region create resources"
  default     = "us-east-1"
}

variable "vpc_id" {
  description = "The VPC id for the endpoint to associate with"
}

variable "type_of_service" {
  description = "Type of endpoint interface service. 'aws' or 'custom'"
  default     = "aws"
}

variable "endpoint_service_name" {
  description = "Endpoint interface service name. if aws, one of 'kms','ec2','ec2messages','elasticloadbalancing','kinesis-streams','servicecatalog','sns' and 'ssm' OR Custom service name"
}

variable "private_dns_enabled" {
  description = "Whether or not to associate a private hosted zone with the specified VPC"
  default     = false
}

variable "list_of_security_group_ids" {
  type        = "list"
  description = "list of security groups to allow access to endpoint"
}

## workaround for  "value of 'count' cannot be computed error" 
#variable "number_of_endpoint_subnets" {
#  description = "Number of subnets to associate with endpoint"
#  default     = 0
#}

variable "list_of_endpoint_subnets" {
  type        = "list"
  description = "List of subnets to associate with the endpoints"
}
