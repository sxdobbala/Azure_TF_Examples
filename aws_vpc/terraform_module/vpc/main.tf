
#find all az available within the region cd 
data "aws_availability_zones" "azs" {}

data "aws_caller_identity" "current" {}

locals {
  number_of_public_subnets                             = "${element(var.aws_azs, 0) == "" ? length(data.aws_availability_zones.azs.names) * var.internet_gateway_enabled : length(var.aws_azs) * var.internet_gateway_enabled}"
  number_of_private_subnets                            = "${element(var.aws_azs, 0) == "" ? length(data.aws_availability_zones.azs.names)  : length(var.aws_azs) }"
  list_of_cidr_block_for_public_subnets                = ["${split(",", element(var.public_subnets_cidr,0) == "" ? join(",", null_resource.public-cidr-helper.*.triggers.list_of_cidr_block_for_public_subnets) : join(",", var.public_subnets_cidr))}"]
  list_of_cidr_block_for_private_subnets               = ["${split(",", element(var.private_subnets_cidr,0) == "" ? join(",", null_resource.private-cidr-helper.*.triggers.list_of_cidr_block_for_private_subnets) : join(",", var.private_subnets_cidr))}"]
  list_of_aws_az                                       = ["${split(",", element(var.aws_azs, 0) == "" ? join(",", data.aws_availability_zones.azs.names) : join(",", var.aws_azs))}"]
  internet_gateway_id                                  = "${var.internet_gateway_enabled == 1 ? join("", aws_internet_gateway.default.*.id) : "" }"
  vpc_endpoint_dynamodb_id                             = "${var.enable_dynamodb_endpoint == 1 ? join("", aws_vpc_endpoint.dynamodb.*.id) : "" }"
  vpc_endpoint_s3_id                                   = "${var.enable_s3_endpoint == 1 ? join("", aws_vpc_endpoint.s3.*.id) : "" }"
  vpc_nat_gateway_ids                                  = ["${split(",", join(",", aws_nat_gateway.nat_gw.*.id))}"]
  associate_nat_gateway_with_private_route_table       = "${var.internet_gateway_enabled * var.nat_enabled }"
  associate_dynamodb_endpoint_with_public_route_table  = "${var.enable_dynamodb_endpoint * var.associate_dynamodb_endpoint_with_public_subnets}"
  associate_s3_endpoint_with_public_route_table        = "${var.enable_s3_endpoint * var.associate_s3_endpoint_with_public_subnets}"
  associate_dynamodb_endpoint_with_private_route_table = "${var.enable_dynamodb_endpoint}"
  associate_s3_endpoint_with_private_route_table       = "${var.enable_s3_endpoint}"
  create_nacl_for_public_subnets                       = "${var.enable_nacl * var.internet_gateway_enabled}"
  create_nacl_for_private_subnets                      = "${var.enable_nacl}"
}

# create a VPC 
resource "aws_vpc" "main" {
  cidr_block           = "${var.vpc_cidr}"
  enable_dns_hostnames = "${var.vpc_enable_dns_hostnames}"
  enable_dns_support   = "${var.vpc_enable_dns_support}"
  tags                 = "${merge(map("Name", "${var.tag_name_identifier}-vpc"),var.global_tags)}"
}

# create an internet gateway 
resource "aws_internet_gateway" "default" {
  count  = "${var.internet_gateway_enabled}"
  vpc_id = "${aws_vpc.main.id}"
  tags   = "${merge(map("Name", "${var.tag_name_identifier}-vpc-igw"),var.global_tags)}"
}

# Creating VPC gateway endpoints for dynamodb
resource "aws_vpc_endpoint" "dynamodb" {
  count        = "${var.enable_dynamodb_endpoint}"
  vpc_id       = "${aws_vpc.main.id}"
  service_name = "com.amazonaws.${var.aws_region}.dynamodb"
}

# Creating VPC gateway endpoints for S3
resource "aws_vpc_endpoint" "s3" {
  count        = "${var.enable_s3_endpoint}"
  vpc_id       = "${aws_vpc.main.id}"
  service_name = "com.amazonaws.${var.aws_region}.s3"
}

#elastic ip for NAT instanses 
resource "aws_eip" "nat_eip" {
  count = "${local.number_of_public_subnets * var.nat_enabled}"
  vpc   = true
}

#create NAT instances for private resource outbound traffic , assuming  number of public az = number of private az 
resource "aws_nat_gateway" "nat_gw" {
  count         = "${local.number_of_public_subnets * var.nat_enabled}"
  allocation_id = "${aws_eip.nat_eip.*.id[count.index]}"
  subnet_id     = "${element(module.vpc-subnets.subnets_public_subnet_ids,count.index)}"
  depends_on    = ["aws_eip.nat_eip"]
}

resource "null_resource" "public-cidr-helper" {
  count = "${element(var.public_subnets_cidr,0) == "" ? local.number_of_public_subnets : 0}"

  triggers {
    list_of_cidr_block_for_public_subnets = "${cidrsubnet(var.vpc_cidr, 4, count.index)}"
  }
}

resource "null_resource" "private-cidr-helper" {
  count = "${element(var.private_subnets_cidr,0) == "" ? local.number_of_private_subnets : 0}"

  triggers {
    list_of_cidr_block_for_private_subnets = "${cidrsubnet(var.vpc_cidr, 4, count.index + local.number_of_public_subnets )}"
  }

  depends_on = ["null_resource.public-cidr-helper"]
}

module "vpc-subnets" {
  source                                               = "../subnets/"
  aws_region                                           = "${var.aws_region}"
  vpc_id                                               = "${aws_vpc.main.id}"
  internet_gateway_id                                  = "${local.internet_gateway_id}"
  vpc_endpoint_dynamodb_id                             = "${local.vpc_endpoint_dynamodb_id}"
  vpc_endpoint_s3_id                                   = "${local.vpc_endpoint_s3_id}"
  vpc_nat_gateway_ids                                  = "${local.vpc_nat_gateway_ids}"
  list_of_aws_az                                       = "${local.list_of_aws_az}"
  associate_nat_gateway_with_private_route_table       = "${local.associate_nat_gateway_with_private_route_table}"
  associate_dynamodb_endpoint_with_public_route_table  = "${local.associate_dynamodb_endpoint_with_public_route_table}"
  associate_s3_endpoint_with_public_route_table        = "${local.associate_s3_endpoint_with_public_route_table}"
  associate_dynamodb_endpoint_with_private_route_table = "${local.associate_dynamodb_endpoint_with_private_route_table}"
  associate_s3_endpoint_with_private_route_table       = "${local.associate_s3_endpoint_with_private_route_table}"
  create_nacl_for_public_subnets                       = "${local.create_nacl_for_public_subnets}"
  create_nacl_for_private_subnets                      = "${local.create_nacl_for_private_subnets}"
  number_of_public_subnets                             = "${local.number_of_public_subnets}"
  number_of_private_subnets                            = "${local.number_of_private_subnets}"
  list_of_cidr_block_for_public_subnets                = "${local.list_of_cidr_block_for_public_subnets}"
  list_of_cidr_block_for_private_subnets               = "${local.list_of_cidr_block_for_private_subnets}"
  public_ip_on_launch                                  = "${var.enable_public_ip_on_launch}"
  tag_name_identifier                                  = "${var.tag_name_identifier}"
  global_tags                                          = "${var.global_tags}"
  other_public_subnet_tags                             = "${var.other_public_subnet_tags}"
  other_private_subnet_tags                            = "${var.other_private_subnet_tags}"
}

module "flow_logs" {
  source              = "../../terraform_module/flow_logs/"
  aws_region          = "${var.aws_region}"
  vpc_id              = "${aws_vpc.main.id}"
  tag_name_identifier = "${var.tag_name_identifier}"
}
