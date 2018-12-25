variable "aws_region" {
  description = "AWS region to create resources"
  default     = "us-east-1"
}

variable "proxy_name" {
  description = "Name for the Egress proxy"
  default     = "squid-egress-proxy"
}

variable "vpc_id" {
  description = "VPC to launch the egress proxy.Required field"
}

variable "subnets_for_proxy_placement" {
  description = "subnet ids from public availability zones.Must be public zone subnets from your VPC .Default is default subnets for the given region"
  type        = "list"
  default     = [""]
}

variable "instance_type" {
  description = "ec2 instance type used for proxy instance"
  default     = "t2.medium"
}

variable "ami_provider" {
  description = "AMI provider is either public or private. If image is not public, you must also provide ami_name_filter and ami_owner"
  default     = "public"
}

variable "ami_name_filter" {
  description = "The name filter to find the AMI.Ignored when ami_provider is public"
  default     = ""
}

variable "ami_owner" {
  description = "The owner of the AMI. Ignored when ami_provider is public"
  default     = ""
}

variable "enable_asg_policy" {
  description = "Enable custom Scale In/Scale Out policy based on network traffic [KB/sec]"
  default     = true
}

variable "max_number_instances" {
  description = " Maximum number of instances to run with Scale out policy. This variable is ignored if `enable_asg_policy` is set to false"
  default     = 10
}

variable "cloudwatch_log_group_name" {
  description = "Name of the CloudWatch log group"
  default     = "egress-proxy-logs"
}

variable "retention_days" {
  description = "Number of days to retain the cloud watch logs"
  default     = 14
}

variable "s3_bucket_name_prefix" {
  description = "Name prefix for S3 bucket to output the Squid proxy information and also to store code deploy configurations"
  default     = "egress-squid-proxy-config"
}

variable "s3_log_bucket_name" {
  description = "s3 bucket name for logging s3 access to main bucket"
}

variable "namespace" {
  description = "The namespace parameter is appended to all resource names to create unquie resource names"
  default     = "pe"
}

variable "global_tags" {
  description = "Addtioanl global tags to be applied to created resources"
  type        = "map"

  default = {
    "terraform" = "true"
  }
}
