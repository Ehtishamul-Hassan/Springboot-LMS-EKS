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

# ---------------------------
# EKS Cluster IAM Role
# ---------------------------
resource "aws_iam_role" "eks_cluster_role" {
  name = "eksClusterRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "eks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

# Attach required policies 
resource "aws_iam_role_policy_attachment" "eks_cluster_AmazonEKSClusterPolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster_role.name
}

resource "aws_iam_role_policy_attachment" "eks_cluster_AmazonEKSVPCResourceController" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
  role       = aws_iam_role.eks_cluster_role.name
}

resource "aws_iam_role_policy_attachment" "eks_cluster_AdminAccess" {
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
  role       = aws_iam_role.eks_cluster_role.name
}

# ---------------------------
# Worker Node IAM Role
# ---------------------------
resource "aws_iam_role" "worker_node_role" {
  name = "eksWorkerNodeRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

# Attach required policies 
resource "aws_iam_role_policy_attachment" "worker_node_AmazonEKSWorkerNodePolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.worker_node_role.name
}

resource "aws_iam_role_policy_attachment" "worker_node_AmazonEC2ContainerRegistryReadOnly" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.worker_node_role.name
}

resource "aws_iam_role_policy_attachment" "worker_node_AmazonEKS_CNI_Policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.worker_node_role.name
}

resource "aws_iam_role_policy_attachment" "worker_node_AdminAccess" {
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
  role       = aws_iam_role.worker_node_role.name
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



