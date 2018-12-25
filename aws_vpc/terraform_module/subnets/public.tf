provider "aws" {
  region = "${var.aws_region}"
}

# create public subnets
resource "aws_subnet" "public" {
  count                   = "${var.number_of_public_subnets}"
  vpc_id                  = "${var.vpc_id}"
  cidr_block              = "${element(var.list_of_cidr_block_for_public_subnets, count.index)}"
  availability_zone       = "${element(var.list_of_aws_az, count.index)}"
  map_public_ip_on_launch = "${var.public_ip_on_launch}"
  tags                    = "${merge(map("Name", "${var.tag_name_identifier}-public-subnets"),var.other_public_subnet_tags,var.global_tags)}"

  lifecycle {
    ignore_changes = ["cidr_block"]
  }
}

#create route table for private subnets 
resource "aws_route_table" "public" {
  count  = "${var.number_of_public_subnets}"
  vpc_id = "${var.vpc_id}"
  tags   = "${merge(map("Name", "${var.tag_name_identifier}-public-route-table"),var.global_tags)}"
}

#associate public subnets with public route table which has access through intrnet gateway
resource "aws_route_table_association" "public" {
  count          = "${var.number_of_public_subnets}"
  route_table_id = "${aws_route_table.public.*.id[count.index]}"
  subnet_id      = "${aws_subnet.public.*.id[count.index]}"
}

# grant the VPC internet access on its public route table
resource "aws_route" "internet_access" {
  count                  = "${var.number_of_public_subnets}"
  route_table_id         = "${aws_route_table.public.*.id[count.index]}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${var.internet_gateway_id}"
}

# VPC endpoints association
resource "aws_vpc_endpoint_route_table_association" "dynamodb" {
  count           = "${var.number_of_public_subnets * var.associate_dynamodb_endpoint_with_public_route_table}"
  vpc_endpoint_id = "${var.vpc_endpoint_dynamodb_id}"
  route_table_id  = "${aws_route_table.public.*.id[count.index]}"
}

resource "aws_vpc_endpoint_route_table_association" "s3" {
  count           = "${var.number_of_public_subnets * var.associate_s3_endpoint_with_public_route_table}"
  vpc_endpoint_id = "${var.vpc_endpoint_s3_id}"
  route_table_id  = "${aws_route_table.public.*.id[count.index]}"
}

#Create NACL 
resource "aws_network_acl" "public" {
  count      = "${var.number_of_public_subnets > 0 ? var.create_nacl_for_public_subnets : 0}"
  vpc_id     = "${var.vpc_id}"
  subnet_ids = ["${aws_subnet.public.*.id}"]
  tags       = "${merge(map("Name", "${var.tag_name_identifier}-public-subnets-acl"),var.global_tags)}"
}
