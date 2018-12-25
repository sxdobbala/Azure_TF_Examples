resource "aws_network_acl_rule" "public_inbound_http" {
  count          = "${var.allow_public_inbound_http * var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  protocol       = "tcp"
  rule_number    = 100
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 80
  to_port        = 80
}

resource "aws_network_acl_rule" "public_inbound_https" {
  count          = "${var.allow_public_inbound_https * var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  protocol       = "tcp"
  rule_number    = 110
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "public_inbound_response_to_internet" {
  count          = "${var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  protocol       = "tcp"
  rule_number    = 120
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024                                                 # inbound return traffic from hosts on the Internet responding to instance in the subnet
  to_port        = 65535                                                #Ephemeral Port range is different for different OS 
}

resource "aws_network_acl_rule" "outbound_http_from_public_subnet" {
  count          = "${var.allow_public_inbound_http * var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  egress         = true
  protocol       = "tcp"
  rule_number    = 130
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 80
  to_port        = 80
}

resource "aws_network_acl_rule" "outbound_https_from_public_subnet" {
  count          = "${var.allow_public_inbound_https * var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  egress         = true
  protocol       = "tcp"
  rule_number    = 140
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "outbound_response_to_internet_from_public_subnet" {
  #Allows outbound responses to clients on the Internet
  count          = "${var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  egress         = true
  protocol       = "tcp"
  rule_number    = 150
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 65535
}

# Fixes issue - https://github.optum.com/CommercialCloud-EAC/aws_vpc/issues/15
resource "aws_network_acl_rule" "public_inbound_icmp_for_pmtu_discovery" {
  count          = "${var.allow_public_inbound_icmp_for_pmtu_discovery * var.enable_nacl * var.internet_gateway_enabled }"
  network_acl_id = "${module.vpc-subnets.subnets_public_nacl_id}"
  protocol       = "icmp"
  rule_number    = 200
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  icmp_type      = 3
  icmp_code      = 4
}
