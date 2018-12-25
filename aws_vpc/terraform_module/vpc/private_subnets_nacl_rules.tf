resource "aws_network_acl_rule" "private_inbound_http_within_VPC" {
  count          = "${var.allow_inbound_http_from_vpc_subnets * var.enable_nacl}"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  rule_number    = 100
  rule_action    = "allow"
  cidr_block     = "${var.vpc_cidr}"
  from_port      = 80
  to_port        = 80
}

resource "aws_network_acl_rule" "private_inbound_https_within_VPC" {
  count          = "${var.allow_inbound_https_from_vpc_subnets * var.enable_nacl}"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  rule_number    = 110
  rule_action    = "allow"
  cidr_block     = "${var.vpc_cidr}"
  from_port      = 443
  to_port        = 443
}

#Not just limiting this for NAT Gateways, this can be for egress proxy as well. 
resource "aws_network_acl_rule" "private_inbound_return_traffic_via_nat" {
  count          = "${var.allow_private_inbound_return_traffic_via_nat * var.enable_nacl }"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  rule_number    = 120
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024                                                                     # Allows inbound return traffic from the NAT device in the public subnet for requests originating in the private subnet
  to_port        = 65535                                                                    #Ephemeral Port range is different for different OS 
}

resource "aws_network_acl_rule" "outbound_http_from_private_subnet" {
  count          = "${var.allow_outbound_http_from_private_subnet * var.enable_nacl}"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  egress         = true
  rule_number    = 130
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 80
  to_port        = 80
}

resource "aws_network_acl_rule" "outbound_https_from_private_subnet" {
  count          = "${var.allow_outbound_https_from_private_subnet * var.enable_nacl}"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  egress         = true
  rule_number    = 140
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "outbound_response_to_internet_from_private_subnet" {
  #Allows outbound responses to clients on the Internet
  count          = "${var.allow_outbound_response_to_internet_from_private_subnet * var.enable_nacl}"
  network_acl_id = "${module.vpc-subnets.subnets_private_nacl_id}"
  protocol       = "tcp"
  egress         = true
  rule_number    = 150
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 65535
}
