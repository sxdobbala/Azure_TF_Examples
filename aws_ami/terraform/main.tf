provider "aws" {
  profile = "${var.aws_profile}"
  region  = "${var.aws_region}"
}

module "vpc" {
  source                   = "git::https://github.optum.com/CommercialCloud-EAC/aws_vpc.git//terraform_module/vpc?ref=v1.7.1"
  aws_region               = "${var.aws_region}"
  aws_profile              = "${var.aws_profile}"
  vpc_cidr                 = "10.0.0.0/16"
  aws_azs                  = ["us-east-1a"]
  vpc_name                 = "${var.aws_account}"
  enable_s3_endpoint       = true
  internet_gateway_enabled = true
  tag_name_identifier      = "${var.tag_name_identifier}"

  global_tags = {
    terraform = "true"
    packer    = "true"
  }
}

module "s3" {
  source               = "git::https://github.optum.com/CommercialCloud-EAC/aws_s3.git//terraform_module//simple?ref=v1.4.0"
  bucket_name          = "packer-artifacts-${data.aws_caller_identity.current.account_id}"
  bucket_custom_policy = "${data.aws_iam_policy_document.bucket_policy.json}"
  bucket_force_destroy = true

  s3_tags = {
    Name = "${var.tag_name_identifier}_packer_artifactory"
  }
}

resource "aws_s3_bucket_object" "object" {
  depends_on   = ["module.s3"]
  bucket       = "${module.s3.bucket_id}"
  key          = "ami.zip"
  source       = "ami.zip"
  content_type = "application/zip"
}

resource "aws_instance" "packer_builder" {
  ami                         = "${data.aws_ami.amazon.id}"
  instance_type               = "t2.micro"
  availability_zone           = "us-east-1a"
  security_groups             = ["${module.vpc.vpc_public_sg_id}"]
  subnet_id                   = "${element(module.vpc.vpc_public_subnet_ids,0)}"
  key_name                    = ""
  associate_public_ip_address = true
  iam_instance_profile        = "${aws_iam_instance_profile.packer_builder.id}"
  user_data                   = "${file("terraform/user_data")}"

  tags {
    Name      = "${var.tag_name_identifier}_packer_builder"
    terraform = "true"
  }
}

resource "aws_security_group_rule" "allow_all" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "all"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = "${module.vpc.vpc_public_sg_id}"
}

resource "aws_network_acl_rule" "ssh-in" {
  network_acl_id = "${module.vpc.vpc_public_nacl_id}"
  rule_number    = 300
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.0.0/16"
  from_port      = 22
  to_port        = 22
}

resource "aws_network_acl_rule" "ssh-out" {
  network_acl_id = "${module.vpc.vpc_public_nacl_id}"
  rule_number    = 300
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.0.0/16"
  from_port      = 22
  to_port        = 22
}

terraform {
  required_version = ">= 0.10.0"
}
