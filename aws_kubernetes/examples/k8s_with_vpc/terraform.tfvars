aws_region="us-east-1"
aws_profile="default"
tag_name_identifier="pe"

#VPC
vpc_cidr="10.1.0.0/16"
aws_azs= ["us-east-1a", "us-east-1b", "us-east-1c"]
vpc_name= "example-vpc"

#Egress Proxy
proxy_name="example-egress-proxy"
proxy_instance_type="t2.medium"
proxy_max_number_of_instances="4"
cloudwatch_log_group_name="example-dev-egress-proxy"
cloudwatch_log_retention_days="7"
proxy_s3_bucket_name_prefix="example-dev-egress-proxy"

#Kubernetes
k8s_master_count = "3"
k8s_node_count_max  = "3"
k8s_node_count_min  = "2"
k8s_node_instance_type = "t2.medium"
#k8s_node_instance_type = "m4.large"
k8s_master_instance_type = "t2.medium"
#k8s_master_instance_type = "m4.large"
k8s_ami_os_name = "ubuntu"
k8s_ami_owner = "099720109477"
k8s_ami_unique_identifier = "ubuntu-xenial-16.04-amd64-server"
#k8s_ami_os_name = "amzn2"
#k8s_ami_owner = "137112412989"
#k8s_ami_unique_identifier = "ami-hvm-2.0"
s3_k8s_bucket_name = "pekops-common-tech-dev-2"
k8s_route53_parent_zoneid = "TEST"
#k8s_route53_parent_zoneid = "Z249HVDESX33LR"