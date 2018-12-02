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

resource "azurerm_key_vault_secret" "secret" {
  name         = "${module.namespace.name}"
  value        = "${var.value}"
  vault_uri    = "${var.vault_uri}"
  content_type = "${var.content_type}"
  tags         = "${merge(var.global_tags, var.tags, local.version_tag)}"
}
