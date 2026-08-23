output "ec2_public_ip" {
  description = "Public IP address of the DevOps EC2 instance"
  value       = aws_instance.demo_instance.public_ip
}
