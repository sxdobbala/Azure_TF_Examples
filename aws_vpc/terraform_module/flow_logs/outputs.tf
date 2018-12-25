output "flow_log_id" {
  value = "${aws_flow_log.main_vpc.id}"
}

output "flow_log_role_arn" {
  value = "${aws_flow_log.main_vpc.iam_role_arn}"
}

output "flow_log_cw_log_group_name" {
  value = "${aws_flow_log.main_vpc.log_group_name}"
}

output "flow_log_traffic_type" {
  value = "${aws_flow_log.main_vpc.traffic_type}"
}
