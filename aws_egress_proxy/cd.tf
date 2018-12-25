resource "aws_codedeploy_app" "proxy" {
  name = "${local.proxy_name}"
}

resource "aws_codedeploy_deployment_group" "proxy" {
  app_name              = "${aws_codedeploy_app.proxy.name}"
  deployment_group_name = "${local.proxy_name}-group"
  autoscaling_groups    = ["${aws_autoscaling_group.proxy.name}"]
  service_role_arn      = "${module.create-role-for-codedeploy.role_arn}"

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE"]
  }
}
