output "name" {
  value = "${aws_iam_user.user.name}"
}

output "path" {
  value = "${aws_iam_user.user.path}"
}

output "arn" {
  value = "${aws_iam_user.user.arn}"
}
