output "lambda_function_name" {
  value = "${module.lambda_invoker.function_name}"
}

output "lambda_function_arn" {
  value = "${module.lambda_invoker.arn}"
}

output "scheduler_rule_name" {
  value = "${module.scheduler.rule_name}"
}

output "scheduler_target_arn" {
  value = "${module.scheduler.target_arn}"
}

output "lambda_topic_arn" {
  value = "${var.env_sns_topic_arn}"
}
