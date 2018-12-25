resource "null_resource" "run-pekops" {
  triggers {
    command = "${var.pekops_trigger}"
  }

  provisioner "local-exec" {
    when        = "create"
    interpreter = ["bash", "-c"]

    command = <<CMD
      set -e

      cd ${path.module}/data

      sh pekops setup \
        -c ${var.cluster_name} \
        -z ${join(",", var.aws_azs)} \
        -a ${join(",", var.vpc_public_subnet_ids)} \
        -b ${join(",", var.vpc_private_subnet_ids)} \
        -v ${var.vpc_id} -s ${var.s3_k8s_bucket_name} \
        -r ${var.route53_zoneid} \
        -e ${element(split("//",var.egress_proxy_endpoint),1)} \
        -m ${var.master_count} \
        -n ${var.node_count_max} \
        -q ${var.node_count_min} \
        -y ${var.node_instance_type} \
        -x ${var.master_instance_type} \
        -o ${var.ami_os_name} \
        -i '${var.ami_unique_identifier}' \
        -f ${var.ami_owner} \
        -p ${var.aws_profile} \
        -t ${var.tag_name_identifier} \
        -d ${self.id}

      cd -
CMD
  }
}

resource "null_resource" "delete-pekops" {
  provisioner "local-exec" {
    when        = "destroy"
    interpreter = ["bash", "-c"]

    command = <<CMD
      set -e

      cd ${path.module}/data

      sh pekops clean \
        -c ${var.cluster_name} \
        -s ${var.s3_k8s_bucket_name} \
        -r ${var.route53_zoneid} \
        -z ${join(",", var.aws_azs)}

      cd -
CMD
  }
}

## Please be aware of below issues 
#https://github.com/hashicorp/terraform/issues/17034
#https://github.com/hashicorp/terraform/issues/11806

data "local_file" "nodes" {
  filename = "${path.module}/data/${var.cluster_name}/launch_script/aws_launch_configuration_nodes.${var.cluster_name}_user_data${null_resource.run-pekops.id}"
}

data "local_file" "master" {
  count    = "${var.master_count}"
  filename = "${path.module}/data/${var.cluster_name}/launch_script/aws_launch_configuration_master-${element(var.aws_azs, count.index)}.${var.cluster_name}_user_data${null_resource.run-pekops.id}"
}
