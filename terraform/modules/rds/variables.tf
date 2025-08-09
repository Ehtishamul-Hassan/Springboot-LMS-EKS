variable "name" {}
variable "private_subnet_ids" {
  type = list(string)
}
variable "rds_sg_id" {}
variable "db_username" {}
variable "db_password" {}

variable "db_name" {
  description = "The name of the initial database to create in MySQL"
  type        = string
}
