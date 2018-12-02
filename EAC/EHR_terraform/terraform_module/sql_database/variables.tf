variable "rg_name" {
  type    = "string"
  description="The name of the resource group in which to create the database."
}

variable "rg_location" {
  type    = "string"
  description="Specifies the supported Azure location where the resource exists."
}

variable "requested_service_objective_name" {
  type    = "string"
  default = "Basic"
  description = "Performance level for the database. The pricing tier of the database. If the database is in an elastic pool, returns ElasticPool. On the Basic tier, returns Basic. Single database in a standard service tier returns one of the following: S0, S1, S2, S3, S4, S6, S7, S9 or S12. Single database in a premium tier returns of the following: P1, P2, P4, P6, P11 or P15. SQL Data Warehouse returns DW100 through DW10000c."
}

variable "sqlserver_name" {
  type    = "string"
  description="The name of the SQL Server on which to create the database."
}

variable "sqldb_edition" {
  type="string"
  default = "Basic"
  description="The edition of the database to be created. Applies only if create_mode is Default. Valid values are: Basic, Standard, Premium, or DataWarehouse."
}

variable "max_size_bytes" {
  type="string"
  default="2147483648"
  description="The maximum size that the database can grow to."
}

variable "collation" {
  type = "string"
  default = "SQL_LATIN1_GENERAL_CP1_CI_AS"
  description = "The collation of the database"
}

variable "sqldb_name" {
  type    = "string"
  description = "Name of the SQL database"
}