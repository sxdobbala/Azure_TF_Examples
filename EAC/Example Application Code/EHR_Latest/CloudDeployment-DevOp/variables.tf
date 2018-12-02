//Name that differentiates this from other projects/applications
variable "PROJECT_NAME" {
    type="string"
}
//Temporary names can be used to make sure that each deployment tears down the old one and builds a new one.
//  This may be useful for dev or qa enviornments. It is not recommended for more stable environments.
variable "USE_TEMPORARY_DATABASE" {
    type="string"
    default="false"
}
//Geographic region where the resources will be stored. For best performance, all resources for this app will be stored in the same region
variable "LOCATION" {
    type="string"
    default="East US"
}

//Username for the administrator account of the sql server
variable "SQL_LOGIN"{
    type="string"
	default=""
}
//Password for the administrator account of the sql server
variable "SQL_PASSWORD"{
    type="string"
	default=""
}
//TODO put the two variables i made from the release into here
variable "OPTUMID_CLIENTSECRET"{
	type="string"
}
variable "OPTUMID_CLIENTID"{
	type="string"
}
variable "VNET_CIDR" {
  description = "Classless Inter-Domain Routing (CIDR) block for the Vnet"
  type        = "list"
}
variable "LIST_OF_CIDR_BLOCK_FOR_SUBNETS" {
  description = "list of CIDR block for the subnets"
  # type      = "list"
}
variable "NAME" {
default =""
}
variable "name_format" {
  type        = "string"
  description = "The string format used to format name and namespace."
  default     = "%s%s"
}

