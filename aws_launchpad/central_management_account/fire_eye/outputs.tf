output "cloudtrail_forwarder_role_arn" {
  value = "${aws_iam_role.cloudtrail_forwarder_role.arn}"
}
