data "template_file" "install_apache_demo" {
  template                                             = "${file("${path.module}/scripts//install_apache_demo.sh.tpl")}"
  vars = {
    fqdn                                               = "${azurerm_public_ip.publicip01.fqdn}"
    email_address                                      = "${var.email_address}"
  }
}

data "template_cloudinit_config" "config" {
  # cloudinit has a limit of 16kb (after gzip'd)
  gzip                                                 = true
  base64_encode                                        = true
  part {
    content_type                                       = "text/x-shellscript"
    content                                            = "${file("${path.module}/scripts/harden_os.sh")}"
  }
  part {
    filename                                           = "install_apache_demo.sh"
    content_type                                       = "text/x-shellscript"
    content                                            = "${data.template_file.install_apache_demo.rendered}"
  }
}
