variable "aws_profile" {
  description = "aws credential profile"
  default     = "default"
}

variable "vpc_id" {
  description = "VPC id for the Kubernetes cluster"
}

variable "aws_azs" {
  description = "AWS availability zones within a single region where Kubernetes resources are created. Please make sure, number of availability zones is equal or higher than the number of Master nodes"
  type        = "list"
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

variable "cluster_name" {
  description = "Name of the Kubernetes cluster. Name must be a qualified domain name with AWS Route53. ex: mycluster.examaple.com - example.com must be the domain in public Route53 Hosted zone"
}

variable "master_count" {
  description = "Number of master nodes to run. Each one of them run in a separate availability zone for HA"
  default     = "3"
}

variable "node_count_max" {
  description = "Maximum number of worker nodes to run in ASG. This depends on your workload"
  default     = "3"
}

variable "node_count_min" {
  description = "Minimum number of worker nodes to run in ASG. This depends on your workload"
  default     = "3"
}

variable "master_instance_type" {
  description = "EC2 instance type for master nodes"
  default     = "t2.medium"
}

variable "node_instance_type" {
  description = "EC2 instance type for worker nodes"
  default     = "t2.medium"
}

variable "ebs_optimized" {
  description = "If true, the launched EC2 instance will be EBS-optimized. Make sure instance support EBS optimization is set to true"
  default     = false
} 

variable "ami_os_name" {
  description = "Operating system to run Kubernetes nodes.`ubuntu` and `debian` are the only options"
  default     = "ubuntu"
}

variable "ami_owner" {
  description = "Owner name of the AMI"
  default     = "757541135089"
}

variable "ami_unique_identifier" {
  description = "To uniquely identify an ami"
  default     = "*"
}

variable "vpc_public_subnet_ids" {
  description = "Public subnet ids from VPC to launch Kubernetes public facing load balancers"
  type        = "list"
}

variable "vpc_private_subnet_ids" {
  description = "Private subnet ids from VPC to launch Kubernetes master and worker nodes"
  type        = "list"
}

variable "enable_nacl" {
  description = "Enable NACL for private subnets"
  default     = true
}

variable "private_network_acl_id" {
  description = "NACL id of the private subnets provided. Ignore if `enable_nacl` is false "
  default     = ""
}

variable "route53_zoneid" {
  description = "Route53 public zone id to create Kubernetes DNS records. This zone must be where cluster domain is registered"
  default     = ""
}

variable "s3_k8s_bucket_name" {
  description = "S3 bucket to persist cluster configurations"
}

variable "egress_proxy_endpoint" {
  description = "Egress proxy endpoint with port. Should be in the format http://dnsname:port"
}

## Validate this for updating cluster version
variable "pekops_trigger" {
  description = "changing this value effectively trigger pekops again , this would mean creating a new cluster"
  default     = "FIXED"
}

variable "tag_name_identifier" {
  description = "tag name identifier for all resources that supports tags"
  default     = "pe"
}

variable "global_tags" {
  description = "Additional global tags to be applied to created resources"
  type        = "map"

  default = {
    "terraform" = "true"
  }
}
