output "instance_public_ips" {
  value = {
    for k, inst in module.ec2_instances :
    k => inst.public_ip
  }
}

output "instance_ids" {
  value = {
    for k, inst in module.ec2_instances :
    k => inst.instance_id
  }
}
