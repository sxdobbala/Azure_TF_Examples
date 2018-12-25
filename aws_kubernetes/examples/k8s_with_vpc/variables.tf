## VPC

variable "aws_region" {}

variable "aws_profile" {}

variable "vpc_name" {}

variable "vpc_cidr" {}

variable "aws_azs" {
  type = "list"
}

## Egress Proxy

variable "proxy_name" {}

variable "proxy_instance_type" {}

variable "proxy_max_number_of_instances" {}

variable "cloudwatch_log_group_name" {}

variable "cloudwatch_log_retention_days" {}

variable "proxy_s3_bucket_name_prefix" {}

## Kubernetes

variable "k8s_master_count" {}

variable "k8s_node_count_max" {}

variable "k8s_node_count_min" {}

variable "k8s_master_instance_type" {}

variable "k8s_node_instance_type" {}

variable "k8s_ami_os_name" {}

variable "k8s_ami_owner" {}

variable "k8s_ami_unique_identifier" {}

variable "k8s_route53_parent_zoneid" {
  description = <<DESC
    Zone ID of a hosted zone that may be used
    to create a subdomain for the example K8s deployment.
    For example, if you set this to a Zone ID for `example.com`,
    the k8s cluster will be configured at
    `k8s-example.example.com`
DESC
}

variable "s3_k8s_bucket_name" {}

variable "tag_name_identifier" {}
