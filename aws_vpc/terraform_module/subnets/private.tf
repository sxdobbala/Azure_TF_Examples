resource "aws_subnet" "private" {
  count             = "${var.number_of_private_subnets}"
  vpc_id            = "${var.vpc_id}"
  cidr_block        = "${element(var.list_of_cidr_block_for_private_subnets, count.index)}"
  availability_zone = "${element(var.list_of_aws_az, count.index)}"
  tags              = "${merge(map("Name", "${var.tag_name_identifier}-private-subnets"),var.global_tags,var.other_private_subnet_tags)}"
  depends_on        = ["aws_subnet.public"]

  lifecycle {
    ignore_changes = ["cidr_block"]
  }
}

#create route table for private subnets 
resource "aws_route_table" "private" {
  count  = "${var.number_of_private_subnets}"
  vpc_id = "${var.vpc_id}"
  tags   = "${merge(map("Name", "${var.tag_name_identifier}-private-route-table"),var.global_tags)}"
}

#associate private subnets with private route tables 
resource "aws_route_table_association" "private" {
  count          = "${var.number_of_private_subnets}"
  route_table_id = "${aws_route_table.private.*.id[count.index]}"
  subnet_id      = "${aws_subnet.private.*.id[count.index]}"
}

#associate NAT gateway route to all private route table
resource "aws_route" "to_nat_gw" {
  count                  = "${var.number_of_private_subnets * var.associate_nat_gateway_with_private_route_table}"
  route_table_id         = "${aws_route_table.private.*.id[count.index]}"
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = "${element(var.vpc_nat_gateway_ids, count.index)}"
}

# VPC endpoints association
resource "aws_vpc_endpoint_route_table_association" "private_dynamodb" {
  count           = "${var.number_of_private_subnets * var.associate_dynamodb_endpoint_with_private_route_table}"
  vpc_endpoint_id = "${var.vpc_endpoint_dynamodb_id}"
  route_table_id  = "${aws_route_table.private.*.id[count.index]}"
}

resource "aws_vpc_endpoint_route_table_association" "private_s3" {
  count           = "${var.number_of_private_subnets * var.associate_s3_endpoint_with_private_route_table}"
  vpc_endpoint_id = "${var.vpc_endpoint_s3_id}"
  route_table_id  = "${aws_route_table.private.*.id[count.index]}"
}

#Create NACL
resource "aws_network_acl" "private" {
  count      = "${var.number_of_private_subnets > 0 ? var.create_nacl_for_private_subnets : 0 }"
  vpc_id     = "${var.vpc_id}"
  subnet_ids = ["${aws_subnet.private.*.id}"]
  tags       = "${merge(map("Name", "${var.tag_name_identifier}-private-subnets-acl"),var.global_tags)}"
}
