locals {
  proxy_name = "${var.proxy_name}-${var.namespace}"

  version_tag = {
    "cc-eac_aws_egress_proxy" = "v1.3.0"
  }

  subnets_for_proxy_placement        = ["${split(",", element(var.subnets_for_proxy_placement,0) == "" ? join(",", data.aws_subnet_ids.default.ids) : join(",", var.subnets_for_proxy_placement))}"]
  subnets_for_proxy_placement_length = "${element(var.subnets_for_proxy_placement,0) == "" ? length(data.aws_subnet_ids.default.ids) : length(var.subnets_for_proxy_placement)}"

  ami_name_filter = "${var.ami_provider == "public" ? "amzn2-ami-hvm-2.0*": var.ami_name_filter }"
  ami_owner       = "${var.ami_provider == "public" ? "137112412989": var.ami_owner }"
  user_data       = "${var.ami_provider == "public" ? data.template_file.public_user_data.rendered : data.template_file.private_user_data.rendered }"
}

data "aws_subnet_ids" "default" {
  vpc_id = "${var.vpc_id}"
}

data "aws_caller_identity" "current" {}

resource "aws_autoscaling_group" "proxy" {
  name                 = "${local.proxy_name}"
  launch_configuration = "${aws_launch_configuration.proxy.id}"
  max_size             = "${var.enable_asg_policy == 1 ? var.max_number_instances : "${local.subnets_for_proxy_placement_length}"}"
  min_size             = "${local.subnets_for_proxy_placement_length}"
  vpc_zone_identifier  = ["${var.subnets_for_proxy_placement}"]
  health_check_type    = "ELB"

  tags = ["${concat(
    list(
      map("key", "Name", "value", "${local.proxy_name}", "propagate_at_launch", true),
      map("key", "terraform", "value", "true", "propagate_at_launch", true),
      map("key", "aws_inspector", "value", "true", "propagate_at_launch", true),
      map("key", "cc-eac_aws_egress_proxy", "value", "${local.version_tag["cc-eac_aws_egress_proxy"]}", "propagate_at_launch", true)
    ))
  }"]
}

data "template_file" "public_user_data" {
  template = "${file("${path.module}/public_user_data")}"

  vars {
    squid_port            = 3128
    egress_proxy_name     = "${local.proxy_name}"
    cloudwatch_logs_group = "${var.cloudwatch_log_group_name}"
    cloudwatch_region     = "${var.aws_region}"
  }
}

data "template_file" "private_user_data" {
  template = "${file("${path.module}/private_user_data")}"

  vars {
    egress_proxy_name     = "${local.proxy_name}"
    cloudwatch_logs_group = "${var.cloudwatch_log_group_name}"
    cloudwatch_region     = "${var.aws_region}"
  }
}

resource "aws_launch_configuration" "proxy" {
  name_prefix                 = "${local.proxy_name}-"
  image_id                    = "${data.aws_ami.proxy.id}"
  instance_type               = "${var.instance_type}"
  iam_instance_profile        = "${aws_iam_instance_profile.proxy.id}"
  security_groups             = ["${aws_security_group.proxy_node.id}"]
  associate_public_ip_address = true
  user_data                   = "${local.user_data}"

  root_block_device = {
    volume_type           = "gp2"
    volume_size           = 20
    delete_on_termination = true
  }

  lifecycle = {
    create_before_destroy = true
  }
}

resource "aws_elb" "proxy" {
  name                        = "${local.proxy_name}"
  security_groups             = ["${aws_security_group.proxy_elb.id}"]
  subnets                     = ["${var.subnets_for_proxy_placement}"]
  cross_zone_load_balancing   = true
  connection_draining         = true
  connection_draining_timeout = 60
  idle_timeout                = 300
  internal                    = true

  listener {
    instance_port     = 3128
    instance_protocol = "TCP"
    lb_port           = 3128
    lb_protocol       = "TCP"
  }

  health_check {
    target              = "TCP:3128"
    healthy_threshold   = 3
    unhealthy_threshold = 2
    interval            = 10
    timeout             = 5
  }

  tags = "${merge(map("Name", "${local.proxy_name}"),var.global_tags,local.version_tag)}"
}

resource "aws_cloudwatch_log_group" "proxy" {
  name              = "${var.cloudwatch_log_group_name}"
  retention_in_days = "${var.retention_days}"
  tags              = "${merge(map("Name", "${local.proxy_name}"),var.global_tags,local.version_tag)}"
}

resource "aws_proxy_protocol_policy" "proxy" {
  load_balancer  = "${aws_elb.proxy.name}"
  instance_ports = ["3128"]
}

resource "aws_autoscaling_attachment" "asg_attachment_bar" {
  autoscaling_group_name = "${aws_autoscaling_group.proxy.id}"
  elb                    = "${aws_elb.proxy.id}"
}

resource "aws_autoscaling_policy" "scale_out" {
  count                  = "${var.enable_asg_policy}"
  name                   = "${local.proxy_name}-scale-out-policy"
  scaling_adjustment     = 1
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 300
  autoscaling_group_name = "${aws_autoscaling_group.proxy.name}"
}

resource "aws_autoscaling_policy" "scale_in" {
  count                  = "${var.enable_asg_policy}"
  name                   = "${local.proxy_name}-scale-in-policy"
  scaling_adjustment     = -1
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 300
  autoscaling_group_name = "${aws_autoscaling_group.proxy.name}"
}

resource "aws_cloudwatch_metric_alarm" "scale_out" {
  count               = "${var.enable_asg_policy}"
  alarm_name          = "${local.proxy_name}-scale-out-alarm"
  alarm_description   = "Scale out if average Squid traffic > 5000 KB/s for 5 minutes"
  metric_name         = "TotalKbytesPerSecond"
  namespace           = "EgressProxy"
  statistic           = "Average"
  period              = "300"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  threshold           = "5000"

  dimensions {
    AutoScalingGroupName = "${aws_autoscaling_group.proxy.name}"
  }

  alarm_actions = ["${aws_autoscaling_policy.scale_out.arn}"]
}

resource "aws_cloudwatch_metric_alarm" "scale_in" {
  count               = "${var.enable_asg_policy}"
  alarm_name          = "${local.proxy_name}-scale-in-alarm"
  alarm_description   = "Scale in if average Squid traffic < 2000 KB/s for 15 minutes"
  metric_name         = "TotalKbytesPerSecond"
  namespace           = "EgressProxy"
  statistic           = "Average"
  period              = "900"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  threshold           = "2000"

  dimensions {
    AutoScalingGroupName = "${aws_autoscaling_group.proxy.name}"
  }

  alarm_actions = ["${aws_autoscaling_policy.scale_in.arn}"]
}

data "aws_ami" "proxy" {
  most_recent = true

  filter {
    name   = "name"
    values = ["${local.ami_name_filter}"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  # Amazon Account ID
  owners = ["${local.ami_owner}"]
}
