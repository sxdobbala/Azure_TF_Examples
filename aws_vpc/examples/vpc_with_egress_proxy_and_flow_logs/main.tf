# provider
provider "aws" {
  region  = "${var.aws_region}"
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  vpc_cidr                 = "${var.vpc_cidr}"
  aws_region               = "${var.aws_region}"
  aws_azs                  = "${var.aws_azs}"
  vpc_name                 = "${var.aws_account}"
  tag_name_identifier      = "${var.tag_name_identifier}"
  internet_gateway_enabled = "${var.internet_gateway_enabled}"
  enable_dynamodb_endpoint = "${var.enable_dynamodb_endpoint}"
  enable_s3_endpoint       = "${var.enable_s3_endpoint}"
}

module "egress_proxy" {
  source                      = "git::https://github.optum.com/CommercialCloud-EAC/aws_egress_proxy.git//terraform_module?ref=v1.2.0"
  aws_region                  = "${var.aws_region}"
  proxy_name                  = "example-egress-proxy"
  vpc_id                      = "${module.vpc.vpc_id}"
  subnets_for_proxy_placement = ["${module.vpc.vpc_public_subnet_ids}"]
  s3_bucket_name_prefix       = "test1234"
  enable_asg_policy           = false
  tag_name_identifier         = "${var.tag_name_identifier}"
}

module "ssm" {
  source                    = "git::https://github.optum.com/CommercialCloud-EAC/aws_ssm.git//terraform_module?ref=v1.0.0"
  ssm_instance_profile_name = "ssm_profile"
}

module "flow_logs" {
  source              = "../../terraform_module/flow_logs/"
  aws_region          = "${var.aws_region}"
  vpc_id              = "${module.vpc.vpc_id}"
  tag_name_identifier = "${var.tag_name_identifier}"
}

data "template_file" "instance_user_data" {
  template = "${file("${path.module}/user_data")}"

  vars {
    EGRESS_URL = "${module.egress_proxy.egress_proxy_url}"
  }
}

resource "aws_instance" "example-web" {
  ami                    = "${data.aws_ami.example.id}"
  instance_type          = "t2.micro"
  vpc_security_group_ids = ["${module.vpc.vpc_private_sg_id}"]
  subnet_id              = "${module.vpc.vpc_private_subnet_ids[0]}"
  iam_instance_profile   = "${module.ssm.ssm_instance_profile_id}"
  user_data              = "${data.template_file.instance_user_data.rendered}"

  tags {
    Name      = "${var.tag_name_identifier}_private_standalone"
    terraform = "true"
  }
}
