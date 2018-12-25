output "name" {
  value = "${aws_iam_group.group.name}"
}

output "id" {
  value = "${aws_iam_group.group.id}"
}

output "arn" {
  value = "${aws_iam_group.group.arn}"
}

output "unique_id" {
  value = "${aws_iam_group.group.unique_id}"
}

output "managed_policies" {
  value = ["${var.custom_managed_policy}"]
}

output "inline_policies" {
  value = ["${var.custom_inline_policy}"]
}

output "users" {
  value = ["${var.users}"]
}
