variable "vnet_cidr" {
  description = "Classless Inter-Domain Routing (CIDR) block for the Vnet"
  type        = "list"
}

variable "list_of_cidr_block_for_subnets" {
  description = "list of CIDR block for the subnets"
  # type        = "list"
}

variable "name"
{
default =""
}

variable "appgwcertpasswd"
{
default =""
}



variable "SQL_LOGIN"
{
default =""
}


variable "SQL_PASSWORD"
{
default =""
}

variable "global_tags" {
  description = "Additional global tags to be applied to created resources"
  type = "map"
  default = {
    "terraform" = "true"
  }
}
