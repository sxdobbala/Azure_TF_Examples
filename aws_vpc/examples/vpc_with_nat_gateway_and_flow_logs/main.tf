# provider
provider "aws" {
  region = "${var.aws_region}"
}

module "vpc" {
  source                   = "../../terraform_module/vpc/"
  vpc_cidr                 = "${var.vpc_cidr}"
  aws_region               = "${var.aws_region}"
  aws_azs                  = "${var.aws_azs}"
  nat_enabled              = true
  vpc_name                 = "${var.aws_account}"
  tag_name_identifier      = "${var.tag_name_identifier}"
  internet_gateway_enabled = "${var.internet_gateway_enabled}"
  enable_dynamodb_endpoint = "${var.enable_dynamodb_endpoint}"
  enable_s3_endpoint       = "${var.enable_s3_endpoint}"
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

#create examaple elb in public zone
resource "aws_elb" "example-web" {
  name     = "example-web-elb"
  internal = false

  # The same availability zone as our instance
  subnets = ["${module.vpc.vpc_public_subnet_ids}"]

  # subnets = ["${split(",", join(",", module.vpc.aws_subnet.public_subnet.*.id))}"]
  security_groups = ["${module.vpc.vpc_public_elb_sg_id}"]

  listener {
    instance_port     = 80
    instance_protocol = "http"
    lb_port           = 80
    lb_protocol       = "http"
  }

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 3
    target              = "HTTP:80/"
    interval            = 30
  }

  # The instance is registered automatically
  instances                   = ["${aws_instance.example-web.id}"]
  cross_zone_load_balancing   = true
  idle_timeout                = 400
  connection_draining         = true
  connection_draining_timeout = 400

  tags {
    Name      = "${var.tag_name_identifier}_public_elb"
    terraform = "true"
  }
}

#create examaple ec2 in private zone
resource "aws_instance" "example-web" {
  ami                         = "${lookup(var.aws_amis, var.aws_region)}"
  instance_type               = "t2.micro"
  key_name                    = ""
  vpc_security_group_ids      = ["${module.vpc.vpc_private_sg_id}"]
  subnet_id                   = "${module.vpc.vpc_private_subnet_ids[0]}"
  associate_public_ip_address = false
  iam_instance_profile        = "${module.ssm.ssm_instance_profile_id}"
  user_data                   = "${module.ssm.ssm_data_template_file_ssm_agent_user_data}"

  tags {
    Name      = "${var.tag_name_identifier}_private_web"
    terraform = "true"
  }
}

resource "aws_ssm_document" "install-nginx" {
  name          = "install-nginx"
  document_type = "Command"

  content = <<DOC
  {
    "schemaVersion": "1.2",
    "description": "Install nginx on ec2 instance.",
    "parameters": {

    },
    "runtimeConfig": {
      "aws:runShellScript": {
        "properties": [
          {
            "id": "0.aws:runShellScript",
            "runCommand": ["yum -y update && yum -y install nginx && service nginx start"]
          }
        ]
      }
    }
  }
DOC
}

resource "aws_ssm_association" "ec2-nginx" {
  name        = "install-nginx"
  instance_id = "${aws_instance.example-web.id}"
}
