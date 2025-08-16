variable "region" {
  default = "ap-south-1"
}

variable "ami" {
  type = string
}

variable "key_name" {
  type = string
}

variable "instances" {
  type = map(object({
    az            = string
    tag           = string
    name          = string
    instance_type = string
    extra_tags    = map(string)
  }))
}

variable "enable_eks" {
  type    = bool
  default = true
}

variable "enable_ec2" {
  type    = bool
  default = true
}

variable "enable_network" {
  type    = bool
  default = true
}



variable "enable_rds" {
  type    = bool
  default = true
}

variable "vpc_cidr" {
  type = string
}

variable "availability_zones" {
  type = list(string)
}

variable "public_subnet_cidrs" {
  type = list(string)
}

variable "private_subnet_cidrs" {
  type = list(string)
}
