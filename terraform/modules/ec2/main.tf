# Get subnet using tag passed from root
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "selected" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  filter {
    name   = "tag:Name"
    values = [var.subnet_tag]
  }
}

data "aws_subnet" "selected" {
  id = data.aws_subnets.selected.ids[0]
}

resource "aws_instance" "this" {
  ami                         = var.ami
  instance_type               = var.instance_type
  subnet_id                   = data.aws_subnet.selected.id
  vpc_security_group_ids      = [var.security_group_id]
  associate_public_ip_address = true
  key_name                    = var.key_name


  user_data = <<EOF
#!/bin/bash
mkdir -p /home/ec2-user/.ssh
echo "${file("~/.ssh/ansible_key.pub")}" >> /home/ec2-user/.ssh/authorized_keys
chown -R ec2-user:ec2-user /home/ec2-user/.ssh
chmod 600 /home/ec2-user/.ssh/authorized_keys
EOF



  tags = merge({
    "Name" = var.extra_tags["Name"]
  }, var.extra_tags)


  root_block_device {
    volume_size = 15
    volume_type = "gp3"
  }
}
