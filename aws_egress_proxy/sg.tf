resource "aws_security_group" "proxy_node" {
  name        = "${local.proxy_name}_node"
  vpc_id      = "${var.vpc_id}"
  description = "Security group for proxy nodes"
  tags        = "${merge(map("Name", "${local.proxy_name}-node"),var.global_tags,local.version_tag)}"
}

resource "aws_security_group" "proxy_elb" {
  name        = "${local.proxy_name}_elb"
  vpc_id      = "${var.vpc_id}"
  description = "Security group for proxy elb"
  tags        = "${merge(map("Name", "${local.proxy_name}-elb"),var.global_tags,local.version_tag)}"
}

resource "aws_security_group_rule" "allow_elb_trafic_to_proxy_instance" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.proxy_node.id}"
  source_security_group_id = "${aws_security_group.proxy_elb.id}"
  from_port                = 3128
  to_port                  = 3128
  protocol                 = "tcp"
}

resource "aws_security_group_rule" "elb_outbound" {
  type                     = "egress"
  security_group_id        = "${aws_security_group.proxy_elb.id}"
  source_security_group_id = "${aws_security_group.proxy_node.id}"
  from_port                = 3128
  to_port                  = 3128
  protocol                 = "tcp"
}

resource "aws_security_group_rule" "proxy_instance_outbound" {
  type              = "egress"
  security_group_id = "${aws_security_group.proxy_node.id}"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}
