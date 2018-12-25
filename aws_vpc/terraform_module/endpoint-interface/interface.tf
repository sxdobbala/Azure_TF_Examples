provider "aws" {
  region = "${var.aws_region}"
}

locals {
  service_name = "${var.type_of_service == "aws" ?  "com.amazonaws.${var.aws_region}.${var.endpoint_service_name}" : var.endpoint_service_name }"
}

resource "aws_vpc_endpoint" "interface" {
  vpc_id              = "${var.vpc_id}"
  service_name        = "${local.service_name}"
  vpc_endpoint_type   = "Interface"
  security_group_ids  = ["${var.list_of_security_group_ids}"]
  private_dns_enabled = "${var.private_dns_enabled}"
  subnet_ids          = ["${var.list_of_endpoint_subnets}"]
}

#There is an issue with concurrent call trying to modify VPC endpoint resulting from "aws_vpc_endpoint_subnet_association". Please refer https://github.com/terraform-providers/terraform-provider-aws/issues/3382.
#Ideally, We would like to avoid associating subnets directly in aws_vpc_endpoint as it reduces the extensibility. Associating subnets using both "aws_vpc_endpoint" & "aws_vpc_endpoint_subnet_association" creates conflicts


#resource "aws_vpc_endpoint_subnet_association" "interface" {
#  count           = "${var.number_of_endpoint_subnets}"
#  vpc_endpoint_id = "${aws_vpc_endpoint.interface.id}"
#  subnet_id       = "${element(var.list_of_endpoint_subnets, count.index)}"
#  depends_on      = ["module.verify_subnet_count"]
#}
#
#module "verify_subnet_count" {
#  source    = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/assert_equal?ref=v1.1.2"
#  actual    = "${length(var.list_of_endpoint_subnets)}"
#  expected  = "${var.number_of_endpoint_subnets}"
#  error_msg = "var.number_of_endpoint_subnets must equal the length of var.list_of_endpoint_subnets"
#}

