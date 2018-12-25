output "k8s_cluster_name" {
  description = "Name of the cluster"
  value       = "${var.cluster_name}"
}

output "k8s_master_asg_name" {
  description = "Autoscaling group names for master nodes"
  value       = "${element(concat(aws_autoscaling_group.master-asg.*.name,list("")),0)}"
}

output "k8s_nodes_asg_name" {
  description = "Autoscaling group name for worker nodes"
  value       = "${aws_autoscaling_group.nodes-asg.name}"
}

output "k8s_master_security_group_ids" {
  description = "Security group ids associated with master nodes"
  value       = ["${aws_security_group.masters.id}"]
}

output "k8s_node_security_group_ids" {
  description = "Security group ids associated with worker nodes"
  value       = ["${aws_security_group.nodes.id}"]
}

output "k8s_masters_role_arn" {
  description = "Master role ARN associated with master nodes"
  value       = "${module.role-for-masters.role_arn}"
}

output "k8s_nodes_role_arn" {
  description = "Worker role ARN associated with worker nodes"
  value       = "${module.role-for-nodes.role_arn}"
}

output "k8s_masters_role_name" {
  description = "Master role name associated with master nodes"
  value       = "${module.role-for-masters.role_name}"
}

output "k8s_nodes_role_name" {
  description = "Worker role name associated with worker nodes"
  value       = "${module.role-for-nodes.role_name}"
}
