ami      = "ami-0583c2579d6458f46"
key_name = "MumbaiKeyPair"

vpc_cidr             = "10.0.0.0/16"
availability_zones   = ["ap-south-1a", "ap-south-1b"]
public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
private_subnet_cidrs = ["10.0.3.0/24", "10.0.4.0/24"]



enable_rds = false
enable_eks = false
enable_ec2 = true

instances = {
  # nexus = {
  #   az            = "ap-south-1a"
  #   tag           = "1a"
  #   name          = "nexus-server"
  #   instance_type = "t2.micro"
  #   extra_tags = {
  #     Name        = "nexus"
  #     Environment = "dev"
  #   }
  # }

  # docker = {
  #   az            = "ap-south-1a"
  #   tag           = "1a"
  #   name          = "docker-server"
  #   instance_type = "t2.micro"
  #   extra_tags = {
  #     Name        = "docker"
  #     Environment = "dev"
  #   }
  # }

  automationHost = {
    az            = "ap-south-1a"
    tag           = "1a"
    name          = "eks-automation-host"
    instance_type = "t2.micro"
    extra_tags = {
      Name        = "automation-host"
      Environment = "dev"
    }
  }
}
