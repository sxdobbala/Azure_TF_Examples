resource "aws_security_group" "api-elb" {
  name        = "api-elb.${var.cluster_name}"
  vpc_id      = "${var.vpc_id}"
  description = "Security group for api ELB"
  tags        = "${merge(map("Name", "api-elb.${var.cluster_name}","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"
}

resource "aws_security_group" "masters" {
  name        = "masters.${var.cluster_name}"
  vpc_id      = "${var.vpc_id}"
  description = "Security group for masters"
  tags        = "${merge(map("Name", "masters.${var.cluster_name}","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"
}

resource "aws_security_group" "nodes" {
  name        = "nodes.${var.cluster_name}"
  vpc_id      = "${var.vpc_id}"
  description = "Security group for nodes"
  tags        = "${merge(map("Name", "nodes.${var.cluster_name}","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"
}

resource "aws_security_group_rule" "all-master-to-master" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.masters.id}"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
}

resource "aws_security_group_rule" "all-master-to-node" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.nodes.id}"
  source_security_group_id = "${aws_security_group.masters.id}"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
}

resource "aws_security_group_rule" "all-node-to-node" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.nodes.id}"
  source_security_group_id = "${aws_security_group.nodes.id}"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
}

resource "aws_security_group_rule" "api-elb-egress" {
  type              = "egress"
  security_group_id = "${aws_security_group.api-elb.id}"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "https-api-elb" {
  type              = "ingress"
  security_group_id = "${aws_security_group.api-elb.id}"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["${data.aws_vpc.k8s.cidr_block}", "49.111.140.50/32", "149.111.204.50/32", "198.203.175.175/32", "198.203.175.254/32", "198.203.177.177/32", "198.203.181.181/32", "149.111.28.128/32", "220.227.15.70/32"]
}

resource "aws_security_group_rule" "https-elb-to-master" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.api-elb.id}"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
}

resource "aws_security_group_rule" "master-egress" {
  type              = "egress"
  security_group_id = "${aws_security_group.masters.id}"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "node-egress" {
  type              = "egress"
  security_group_id = "${aws_security_group.nodes.id}"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "node-to-master-protocol-ipip" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.nodes.id}"
  from_port                = 0
  to_port                  = 65535
  protocol                 = "4"
}

resource "aws_security_group_rule" "node-to-master-tcp-1" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.nodes.id}"
  from_port                = 1
  to_port                  = 4001
  protocol                 = "tcp"
}

resource "aws_security_group_rule" "node-to-master-tcp-2" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.nodes.id}"
  from_port                = 4003
  to_port                  = 65535
  protocol                 = "tcp"
}

resource "aws_security_group_rule" "node-to-master-udp" {
  type                     = "ingress"
  security_group_id        = "${aws_security_group.masters.id}"
  source_security_group_id = "${aws_security_group.nodes.id}"
  from_port                = 1
  to_port                  = 65535
  protocol                 = "udp"
}
