resource "aws_security_group_rule" "private_egress" {
  type              = "egress"
  security_group_id = "${module.vpc.vpc_private_sg_id}"

  from_port   = 0
  to_port     = 0
  protocol    = "-1"
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "private_ingress_http" {
  type                     = "ingress"
  security_group_id        = "${module.vpc.vpc_private_sg_id}"
  source_security_group_id = "${module.vpc.vpc_public_elb_sg_id}"

  protocol = "tcp"

  #cidr_blocks = ["0.0.0.0/0"]
  from_port = 80
  to_port   = 80
}

resource "aws_security_group_rule" "public_egress" {
  type              = "egress"
  security_group_id = "${module.vpc.vpc_public_sg_id}"

  from_port   = 0
  to_port     = 0
  protocol    = "-1"
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "public_ingress_http" {
  type                     = "ingress"
  security_group_id        = "${module.vpc.vpc_public_sg_id}"
  source_security_group_id = "${module.vpc.vpc_public_elb_sg_id}"
  protocol                 = "tcp"

  #cidr_blocks = ["0.0.0.0/0"]
  from_port = 80
  to_port   = 80
}

resource "aws_security_group_rule" "public_elb_ingress_http" {
  type              = "ingress"
  security_group_id = "${module.vpc.vpc_public_elb_sg_id}"

  protocol    = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  from_port   = 80
  to_port     = 80
}

resource "aws_security_group_rule" "public_elb_egress" {
  type              = "egress"
  security_group_id = "${module.vpc.vpc_public_elb_sg_id}"

  from_port   = 0
  to_port     = 0
  protocol    = "-1"
  cidr_blocks = ["0.0.0.0/0"]
}
