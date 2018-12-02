locals {
  version_tag = {
    "cc-eac_azure_key_vault" = "v2.0.0"
  }
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

resource "azurerm_key_vault_key" "key" {
  name          = "${module.namespace.name}"
  vault_uri     = "${var.vault_uri}"
  key_type      = "${var.key_type}"
  key_size      = "${var.key_size}"
  key_opts      = [ "${var.key_opts}" ]
  tags          = "${merge(var.global_tags, var.tags, local.version_tag)}"
}
