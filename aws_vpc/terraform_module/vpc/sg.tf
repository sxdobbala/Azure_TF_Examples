#public instance SG - subnet of instanses that are using this group expected have IGW route association  
resource "aws_security_group" "default_public_instance" {
  count       = "${var.internet_gateway_enabled }"
  name        = "default_public_instance_sg"
  vpc_id      = "${aws_vpc.main.id}"
  description = "default VPC security group ${var.vpc_name} public subnets"
  tags        = "${merge(map("Name", "${var.tag_name_identifier}-public-instance-sg"),var.global_tags)}"
}

#private instance SG 
resource "aws_security_group" "default_private_instance" {
  name        = "default_private_instance_sg"
  vpc_id      = "${aws_vpc.main.id}"
  description = "default VPC security group ${var.vpc_name} private subnets"
  tags        = "${merge(map("Name", "${var.tag_name_identifier}-private-instance-sg"),var.global_tags)}"
}

#elb SG - subnet of instanses that are using this group expected have IGW route association 
resource "aws_security_group" "default_public_elb" {
  count       = "${var.internet_gateway_enabled }"
  name        = "default_public_elb_sg"
  vpc_id      = "${aws_vpc.main.id}"
  description = "default VPC security group ${var.vpc_name} public subnets"
  tags        = "${merge(map("Name", "${var.tag_name_identifier}-elb-sg"),var.global_tags)}"
}

#elb SG rule - fixes https://github.optum.com/CommercialCloud-EAC/aws_vpc/issues/15
resource "aws_security_group_rule" "allow_icmp_pmtu" {
  count             = "${var.allow_public_inbound_icmp_for_pmtu_discovery * var.internet_gateway_enabled}"
  type              = "ingress"
  from_port         = 3
  to_port           = 4
  protocol          = "icmp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = "${aws_security_group.default_public_elb.id}"
}
