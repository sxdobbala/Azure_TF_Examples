resource "aws_network_acl_rule" "private_inbound_within_VPC" {
  count          = "${var.enable_nacl}"
  network_acl_id = "${var.private_network_acl_id}"
  protocol       = "-1"
  rule_number    = 200
  rule_action    = "allow"
  cidr_block     = "${data.aws_vpc.k8s.cidr_block}"
  from_port      = 0
  to_port        = 0
}

resource "aws_network_acl_rule" "private_outbound_within_VPC" {
  count          = "${var.enable_nacl}"
  network_acl_id = "${var.private_network_acl_id}"
  egress         = true
  protocol       = "-1"
  rule_number    = 210
  rule_action    = "allow"
  cidr_block     = "${data.aws_vpc.k8s.cidr_block}"
  from_port      = 0
  to_port        = 0
}
