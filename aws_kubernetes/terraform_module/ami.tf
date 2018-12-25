data "aws_ami" "k8s" {
  most_recent = true

  filter {
    name   = "name"
    values = ["*${var.ami_os_name}*${var.ami_unique_identifier}*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
   name   = "architecture"
   values = ["x86_64"]
  }

  owners = ["${var.ami_owner}"]
}
