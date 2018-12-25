variable "prefix" {
  description = "Object keyname prefix identifying one or more objects to which the rule applies"
  default = ""
}

variable "id" {
  description = "Unique identifier for the rule"
  default = "lifecycle-rule"
}

variable "enabled" {
  description = "Specifies lifecycle rule status"
  default = true
}

variable "initial_storage_class" {
  description = "Specifies the Amazon S3 storage class to which you want the object to transition initially which is INTELLIGENT_TIERING here"
  default = "INTELLIGENT_TIERING"
}
variable "final_storage_class" 
{
  description = "Specifies the Amazon S3 storage class to which you want the object to transition finally which is GLACIER here"
  default = "GLACIER"
}


variable "days_initial_storage_class"
{
  description = "Specifies the number of days after object creation  the object transition takes place from S3 to initial storage class "
  default = "30"
}

variable "days_final_storage_class"
{
  description = "Specifies the number of days after object creation the object transition takes place from initial storage class to final storage class"
  default = "60"
}

variable "expiration_days"
 {
  description = "Specifies the number of days after object creation object expires"
  default = "365"
}

variable "noncurrent_storage_class" 
{
  description = "Storage class for non current objects"
  default = "GLACIER"
}

variable "days_noncurrent_storage_class" {
  description = "Specifies the number of days after object becomes non current the transition takes to storage class"
  default = "30"
}
