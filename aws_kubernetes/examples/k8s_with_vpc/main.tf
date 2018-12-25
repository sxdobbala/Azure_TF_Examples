provider "aws" {
  profile = "${var.aws_profile}"
  region  = "${var.aws_region}"
}

data "aws_caller_identity" "current" {}

module "vpc" {
  source                   = "git::https://github.optum.com/CommercialCloud-EAC/aws_vpc.git//terraform_module/vpc?ref=v1.7.1"
  vpc_cidr                 = "${var.vpc_cidr}"
  aws_region               = "${var.aws_region}"
  aws_profile              = "${var.aws_profile}"
  aws_azs                  = "${var.aws_azs}"
  vpc_name                 = "${var.vpc_name}"
  internet_gateway_enabled = true
  enable_dynamodb_endpoint = true
  enable_s3_endpoint       = true
  tag_name_identifier      = "${var.tag_name_identifier}"

  other_public_subnet_tags = {
    "kubernetes.io/cluster/${aws_route53_zone.k8s_cluster.name}" = "shared"
    "kubernetes.io/role/elb"                                     = 1
  }

  other_private_subnet_tags = {
    "kubernetes.io/cluster/${aws_route53_zone.k8s_cluster.name}" = "shared"
    "kubernetes.io/role/internal-elb"                            = 1
  }

  global_tags = {
    terraform   = "true"
    Team        = "OptumID"
    Environment = "Stage"
    CostCenter  = "THW789"
  }
}

### Egress Proxy

module "egress_proxy" {
  source                      = "git::https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy.git?ref=v1.3.1"
  aws_region                  = "${var.aws_region}"
  proxy_name                  = "${var.proxy_name}"
  vpc_id                      = "${module.vpc.vpc_id}"
  subnets_for_proxy_placement = ["${module.vpc.vpc_public_subnet_ids}"]
  instance_type               = "${var.proxy_instance_type}"
  max_number_instances        = "${var.proxy_max_number_of_instances}"
  cloudwatch_log_group_name   = "${var.cloudwatch_log_group_name}"
  retention_days        = "${var.cloudwatch_log_retention_days}"
  s3_bucket_name_prefix = "${var.proxy_s3_bucket_name_prefix}"
  s3_log_bucket_name    = "bucket-with-logging-logs-${data.aws_caller_identity.current.account_id}"
  namespace             = "${var.tag_name_identifier}"
}

module "bucket-for-logging" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//modules/simple?ref=v2.0.0"
  name = "bucket-with-logging-logs-${data.aws_caller_identity.current.account_id}"
  acl  = "log-delivery-write"
  force_destroy = true
}

resource "aws_security_group_rule" "allow_trafic_to_proxy_elb" {
  type              = "ingress"
  security_group_id = "${module.egress_proxy.egress_proxy_security_group}"
  from_port         = 3128
  to_port           = 3128
  protocol          = "tcp"
  cidr_blocks       = ["${var.vpc_cidr}"]
}

resource "null_resource" "prepare-code-deploy-egress-proxy" {
  provisioner "local-exec" {
    when    = "create"
    command = "cd ${path.module}/code_deploy_config && zip -r ../code_deploy.zip * && cd .. && aws s3 cp code_deploy.zip s3://${module.egress_proxy.egress_proxy_s3_bucket_with_proxy_info} --sse AES256"
  }

  depends_on = ["module.egress_proxy"]
}

resource "null_resource" "run_deployment_for_egress_proxy" {
  provisioner "local-exec" {
    when    = "create"
    command = "aws deploy create-deployment --application-name ${module.egress_proxy.egress_proxy_codedeploy_deployment_app_name} --deployment-group-name ${module.egress_proxy.egress_proxy_codedeploy_deployment_group_name} --description \"sample deployment\" --s3-location bucket=${module.egress_proxy.egress_proxy_s3_bucket_with_proxy_info},bundleType=zip,key=code_deploy.zip --region ${var.aws_region}"
  }

  depends_on = ["null_resource.prepare-code-deploy-egress-proxy"]
}

module "k8s" {
  source                 = "../../terraform_module/"
  vpc_id                 = "${module.vpc.vpc_id}"
  aws_profile            = "${var.aws_profile}"
  aws_azs                = "${var.aws_azs}"
  cluster_name           = "${aws_route53_zone.k8s_cluster.name}"
  master_count           = "${var.k8s_master_count}"
  node_count_max         = "${var.k8s_node_count_max}"
  node_count_min         = "${var.k8s_node_count_min}"
  node_instance_type     = "${var.k8s_node_instance_type}"
  master_instance_type   = "${var.k8s_master_instance_type}"
  ami_os_name            = "${var.k8s_ami_os_name}"
  ami_owner              = "${var.k8s_ami_owner}"
  ami_unique_identifier  = "${var.k8s_ami_unique_identifier}"
  route53_zoneid         = "${aws_route53_zone.k8s_cluster.zone_id}"
  vpc_public_subnet_ids  = ["${module.vpc.vpc_public_subnet_ids}"]
  vpc_private_subnet_ids = ["${module.vpc.vpc_private_subnet_ids}"]
  private_network_acl_id = "${module.vpc.vpc_private_nacl_id}"
  egress_proxy_endpoint  = "${module.egress_proxy.egress_proxy_url}"
  s3_k8s_bucket_name     = "${var.s3_k8s_bucket_name}"
  tag_name_identifier    = "${var.tag_name_identifier}"
}

module "flow_logs" {
  source                           = "git::https://github.optum.com/CommercialCloud-EAC/aws_vpc.git//terraform_module/flow_logs?ref=v1.7.1"
  aws_region                       = "${var.aws_region}"
  aws_profile                      = "${var.aws_profile}"
  vpc_id                           = "${module.vpc.vpc_id}"
  flow_log_group_name              = "vpc-flow-logs"
  traffic_type                     = "ALL"
  flow_log_group_retention_in_days = "7"
  tag_name_identifier              = "${var.tag_name_identifier}"
}

# Configure a `k8s-example.[parent domain]` hosted zone
# for K8s cluster
data "aws_route53_zone" "parent_domain" {
  count   = "${var.k8s_route53_parent_zoneid == "TEST" ? 0 : 1}"
  zone_id = "${var.k8s_route53_parent_zoneid}"
}

resource "aws_route53_zone" "k8s_cluster" {
  name = "k8s-example.${element(concat(data.aws_route53_zone.parent_domain.*.name, list("")), 0)}"

  force_destroy = true
}

resource "aws_route53_record" "k8s_cluster_ns_in_parent_domain" {
  name    = "${aws_route53_zone.k8s_cluster.name}"
  type    = "NS"
  zone_id = "${var.k8s_route53_parent_zoneid == "TEST" ? "TEST" : element(concat(data.aws_route53_zone.parent_domain.*.zone_id, list("")), 0)}"
  records = ["${aws_route53_zone.k8s_cluster.name_servers}"]
  ttl     = "300"
}
