variable "aws_region" {
  default = "us-east-1"
}

variable "account_id" {
  description = "The central AWS account id"
  default     = ""
}

variable "namespace" {
  description = "Namespace for this terraform run"
  default     = ""
}

variable "saml_provider" {
  description = "Configured SAML provider's ARN for this federation setup in the format arn:aws:iam::ACCOUNT_NUMBER:saml-provider/UHG_AWS_POC"
  default     = ""
}
