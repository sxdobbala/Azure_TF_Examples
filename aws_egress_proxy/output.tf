output "egress_proxy_url" {
  value = "http://${aws_elb.proxy.dns_name}:3128"
}

output "egress_proxy_security_group" {
  value = "${aws_elb.proxy.source_security_group_id}"
}

output "egress_proxy_s3_bucket_with_proxy_info" {
  value = "${module.s3.bucket_id}"
}

output "egress_proxy_codedeploy_deployment_app_name" {
  value = "${aws_codedeploy_deployment_group.proxy.app_name}"
}

output "egress_proxy_codedeploy_deployment_group_name" {
  value = "${aws_codedeploy_deployment_group.proxy.deployment_group_name}"
}
