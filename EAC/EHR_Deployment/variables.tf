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
}
//Password for the administrator account of the sql server
variable "SQL_PASSWORD"{
    type="string"
}