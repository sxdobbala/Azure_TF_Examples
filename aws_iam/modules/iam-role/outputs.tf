output "name" {
  value = "${aws_iam_role.role.name}"
}

output "id" {
  value = "${aws_iam_role.role.unique_id}"
}

output "arn" {
  value = "${aws_iam_role.role.arn}"
}

output "user_principals" {
  value = "${var.assume_role_aws_principals}"
}

output "service_principals" {
  value = "${var.assume_role_service_principals}"
}

output "federated_principals" {
  value = "${var.assume_role_federated_principals}"
}

output "managed_policies" {
  value = ["${var.custom_managed_policy}"]
}

output "inline_policies" {
  value = ["${var.custom_inline_policy}"]
}
