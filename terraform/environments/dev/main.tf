provider "aws" {
  region = var.region
}


# Get default VPC and SG
data "aws_vpc" "default" {
  default = true
}

data "aws_security_group" "default" {
  name   = "default"
  vpc_id = data.aws_vpc.default.id
}




############
# VPC + SG #
############

module "network" {
  source               = "../../modules/network"
  name                 = "eks"
  vpc_cidr             = var.vpc_cidr
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  availability_zones   = var.availability_zones
  cluster_name         = "dev-cluster"
}

###############
# EC2 MODULES #
###############

module "ec2_instances" {
  for_each          = var.enable_ec2 ? var.instances : {}
  source            = "../../modules/ec2"
  ami               = var.ami
  instance_type     = each.value.instance_type
  name              = each.value.name
  subnet_tag        = each.value.tag
  security_group_id = data.aws_security_group.default.id
  key_name          = var.key_name
  extra_tags        = each.value.extra_tags
}

###################
# IAM ROLES (EKS) #
###################

data "aws_iam_role" "eks_cluster_role" {
  count = var.enable_eks ? 1 : 0
  name  = "eksClusterRole"
}

# Worker node role (you must create this IAM role in AWS with proper policies)
data "aws_iam_role" "worker_node_role" {
  count = var.enable_eks ? 1 : 0
  name  = "eksWorkerNodeRole"
}

##################
# EKS CLUSTER    #
##################

module "eks" {
  source               = "../../modules/eks"
  cluster_name         = "my-ec2-eks"
  cluster_role_arn     = aws_iam_role.eks_cluster_role.arn
  worker_node_role_arn = aws_iam_role.worker_node_role.arn
  subnet_ids           = module.network.private_subnet_ids
  instance_types       = ["t3.small"]
  node_desired_size    = 2
  node_min_size        = 1
  node_max_size        = 3
}

##############
# RDS MODULE #
##############

module "rds" {
  source             = "../../modules/rds"
  name               = "eks"
  private_subnet_ids = module.network.private_subnet_ids
  rds_sg_id          = module.network.rds_sg_id
  db_username        = "root"
  db_password        = "12345678"
  db_name            = "securelibrarymanagementsystem"
}



