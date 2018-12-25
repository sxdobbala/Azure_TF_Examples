resource "aws_security_group_rule" "private_egress" {
  type              = "egress"
  security_group_id = "${module.vpc.vpc_private_sg_id}"

  from_port   = 0
  to_port     = 0
  protocol    = "-1"
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow_private_internet_traffic" {
  type                     = "ingress"
  security_group_id        = "${module.egress_proxy.egress_proxy_security_group}"
  source_security_group_id = "${module.vpc.vpc_private_sg_id}"

  protocol  = "tcp"
  from_port = 3128
  to_port   = 3128
}
