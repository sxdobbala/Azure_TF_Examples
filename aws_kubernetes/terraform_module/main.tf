data "aws_caller_identity" "current" {}

data "aws_vpc" "k8s" {
  id = "${var.vpc_id}"
}

locals {
  "k8s-etcd-order" {
    "1" = ["a/a"]
    "2" = ["a/a,b", "b/a,b"]
    "3" = ["a/a,b,c", "b/a,b,c", "c/a,b,c"]
    "4" = ["a/a,b,c,d", "b/a,b,c,d", "c/a,b,c,d", "d/a,b,c,d"]
    "5" = ["a/a,b,c,d,e", "b/a,b,c,d,e", "c/a,b,c,d,e", "d/a,b,c,d,e", "e/a,b,c,d,e"]
  }

  "k8s-etcd-ordinal" = ["a", "b", "c", "d", "e"]

  version_tag = {
    "cc-eac_aws_kubernetes" = "v1.6.0"
  }
}

resource "aws_autoscaling_attachment" "master" {
  count                  = "${var.master_count}"
  elb                    = "${aws_elb.api.id}"
  autoscaling_group_name = "${aws_autoscaling_group.master-asg.*.id[count.index]}"
}

#create autoscaling groups for master 
resource "aws_autoscaling_group" "master-asg" {
  count                = "${var.master_count}"
  name                 = "master-${element(var.aws_azs, count.index)}.masters.${var.cluster_name}"
  launch_configuration = "${aws_launch_configuration.master.*.name[count.index]}"
  max_size             = 1
  min_size             = 1
  vpc_zone_identifier  = ["${element(var.vpc_private_subnet_ids,count.index)}"]
  health_check_type    = "ELB"
  metrics_granularity  = "1Minute"
  enabled_metrics      = ["GroupDesiredCapacity", "GroupInServiceInstances", "GroupMaxSize", "GroupMinSize", "GroupPendingInstances", "GroupStandbyInstances", "GroupTerminatingInstances", "GroupTotalInstances"]

  tags = ["${concat(
    list(
      map("key", "KubernetesCluster", "value", "${var.cluster_name}", "propagate_at_launch", true),
      map("key", "cc-eac_aws_kubernetes", "value", "${local.version_tag["cc-eac_aws_kubernetes"]}", "propagate_at_launch", true),
      map("key", "Name", "value", "master-${element(var.aws_azs, count.index)}.masters.${var.cluster_name}", "propagate_at_launch", true),
      map("key", "k8s.io/cluster-autoscaler/node-template/label/kops.k8s.io/instancegroup", "value", "master-${element(var.aws_azs, count.index)}", "propagate_at_launch", true),
      map("key", "k8s.io/role/master", "value", "1", "propagate_at_launch", true),
      map("key", "aws_inspector", "value", "true", "propagate_at_launch", true),
      map("key", "terraform", "value", "true", "propagate_at_launch", true)
    ))
  }"]

  depends_on = ["null_resource.run-pekops"]
}

#create autoscaling group for nodes
resource "aws_autoscaling_group" "nodes-asg" {
  name                 = "nodes.${var.cluster_name}"
  launch_configuration = "${aws_launch_configuration.nodes.name}"
  max_size             = "${var.node_count_max}"
  min_size             = "${var.node_count_min}"
  vpc_zone_identifier  = ["${var.vpc_private_subnet_ids}"]
  health_check_type    = "ELB"
  metrics_granularity  = "1Minute"
  enabled_metrics      = ["GroupDesiredCapacity", "GroupInServiceInstances", "GroupMaxSize", "GroupMinSize", "GroupPendingInstances", "GroupStandbyInstances", "GroupTerminatingInstances", "GroupTotalInstances"]

  tags = ["${concat(
   list(
      map("key", "KubernetesCluster", "value", "${var.cluster_name}", "propagate_at_launch", true),
      map("key", "cc-eac_aws_kubernetes", "value", "${local.version_tag["cc-eac_aws_kubernetes"]}", "propagate_at_launch", true),
      map("key", "Name", "value", "nodes.${var.cluster_name}", "propagate_at_launch", true),
      map("key", "k8s.io/cluster-autoscaler/node-template/label/kops.k8s.io/instancegroup", "value", "nodes", "propagate_at_launch", true),
      map("key", "k8s.io/role/node", "value", "1", "propagate_at_launch", true),
      map("key", "aws_inspector", "value", "true", "propagate_at_launch", true),
      map("key", "terraform", "value", "true", "propagate_at_launch", true)
    ))
  }"]

  depends_on = ["null_resource.run-pekops"]
}

resource "aws_ebs_volume" "etcd-events" {
  count             = "${var.master_count}"
  availability_zone = "${element(var.aws_azs, count.index)}"
  size              = 20
  type              = "gp2"
  encrypted         = true

  tags = "${merge(map("Name", "${element(local.k8s-etcd-ordinal,count.index)}.etcd-events.${var.cluster_name}",
                                      "k8s.io/etcd/events","${element(local.k8s-etcd-order[var.master_count],count.index)}",
                                      "k8s.io/role/master","1","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"

  depends_on = ["null_resource.run-pekops"]
}

resource "aws_ebs_volume" "etcd-main" {
  count             = "${var.master_count}"
  availability_zone = "${element(var.aws_azs, count.index)}"
  size              = 20
  type              = "gp2"
  encrypted         = true

  tags = "${merge(map("Name", "${element(local.k8s-etcd-ordinal,count.index)}.etcd-main.${var.cluster_name}",
                                      "k8s.io/etcd/main","${element(local.k8s-etcd-order[var.master_count],count.index)}",
                                      "k8s.io/role/master","1","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"

  depends_on = ["null_resource.run-pekops"]
}

resource "aws_elb" "api" {
  name = "api-${substr(uuid(), 0, 8)}"

  listener = {
    instance_port     = 443
    instance_protocol = "tcp"
    lb_port           = 443
    lb_protocol       = "tcp"
  }

  security_groups = ["${aws_security_group.api-elb.id}"]
  subnets         = ["${var.vpc_public_subnet_ids}"]

  health_check = {
    target              = "TCP:443"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 10
    timeout             = 5
  }

  connection_draining = true
  idle_timeout        = 300
  tags                = "${merge(map("Name", "api.${var.cluster_name}","KubernetesCluster","${var.cluster_name}"),local.version_tag,var.global_tags)}"

  lifecycle {
    ignore_changes = ["name"]
  }

  depends_on = ["null_resource.run-pekops"]
}

resource "aws_launch_configuration" "master" {
  count                       = "${var.master_count}"
  name                        = "master-${element(var.aws_azs, count.index)}.masters.${var.cluster_name}-${null_resource.run-pekops.id}${var.ami_unique_identifier}"
  image_id                    = "${data.aws_ami.k8s.id}"
  instance_type               = "${var.master_instance_type}"
  iam_instance_profile        = "${aws_iam_instance_profile.masters.id}"
  security_groups             = ["${aws_security_group.masters.id}"]
  associate_public_ip_address = false
  ebs_optimized               = "${var.ebs_optimized}"

  # interpolation of file() fails https://github.com/hashicorp/terraform/issues/6460 & https://github.com/hashicorp/terraform/issues/10878
  #  user_data                   = "${file("${path.module}/data/aws_launch_configuration_master-${element(var.aws_azs, count.index)}.${var.cluster_name}_user_data")}"
  user_data = "${data.local_file.master.*.content[count.index]}"

  root_block_device = {
    volume_type           = "gp2"
    volume_size           = 60
    delete_on_termination = true
  }

  ephemeral_block_device = {
    device_name  = "/dev/sdc"
    virtual_name = "ephemeral0"
  }

  lifecycle = {
    create_before_destroy = true
  }

  depends_on = ["null_resource.run-pekops"]
}

resource "aws_launch_configuration" "nodes" {
  name                        = "nodes.${var.cluster_name}-${null_resource.run-pekops.id}${var.ami_unique_identifier}"
  image_id                    = "${data.aws_ami.k8s.id}"
  instance_type               = "${var.node_instance_type}"
  iam_instance_profile        = "${aws_iam_instance_profile.nodes.id}"
  security_groups             = ["${aws_security_group.nodes.id}"]
  associate_public_ip_address = false
  ebs_optimized               = "${var.ebs_optimized}"

  # interpolation of file() fails https://github.com/hashicorp/terraform/issues/6460 & https://github.com/hashicorp/terraform/issues/10878
  #user_data                   = "${file(path.module/data/aws_launch_configuration_nodes.${var.cluster_name}_user_data)}"
  user_data = "${data.local_file.nodes.content}"

  root_block_device = {
    volume_type           = "gp2"
    volume_size           = 120
    delete_on_termination = true
  }

  lifecycle = {
    create_before_destroy = true
  }

  depends_on = ["null_resource.run-pekops"]
}

terraform = {
  required_version = ">= 0.9.3"
}

# Kubernetes module creates launch scripts during initial creation.
# They are expected in workspace every subsequent terraform plan
# and apply run on the same infrastructure.
# These files are hence persisted along with other terraform configuration in an s3 bucket.
#
# In case those files are lost from the local file system,
# recreate them from S3 here:
# This is not working as expected but keeping here for future enhancements - consider always run = "${uuid()}"
resource "null_resource" "recreate_local_launch_scripts" {
  provisioner "local-exec" {
    interpreter = ["bash", "-c"]

    command = <<CMD
      set -eux

      # Check if the launch script is on S3
      s3_launch_script_location="s3://${var.s3_k8s_bucket_name}/${var.cluster_name}/launch_script/"
      aws s3 ls $${s3_launch_script_location} || exit 0

      # Create launch_script/ dir in module path
      local_launch_script_location="${path.module}/data/${var.cluster_name}/launch_script/"
      mkdir -p $${local_launch_script_location} || true

      # Copy the launch_scripts from s3 to the local module
      aws s3 cp \
        $${s3_launch_script_location} \
        $${local_launch_script_location} \
        --recursive
CMD
  }

  triggers {
    always_run = "${random_string.recreate_local_launch_scripts_trigger.result}"
  }
}

resource "random_string" "recreate_local_launch_scripts_trigger" {
  length = 22
}
