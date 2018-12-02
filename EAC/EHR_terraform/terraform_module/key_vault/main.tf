data "azurerm_client_config" "current" { }

locals {
  version_tag = {
    "cc-eac_azure_key_vault" = "v2.0.0"
  }

  sku_name = "${lookup(var.sku_name_types, var.sku_name, "standard")}"

  # tenant_id - default to current user's tenant_id, if not overridden
  tenant_id = "${length(var.tenant_id) == 0 ?
          format("%s", data.azurerm_client_config.current.tenant_id)
          :
          format("%s", var.tenant_id)
  }"

  # object_id - default to current user's object_id, if not overridden
  object_id = "${length(var.object_id) == 0 ?
          format("%s", data.azurerm_client_config.current.service_principal_object_id)
          :
          format("%s", var.object_id)
  }"

  # If aux_object_id is defined, we'll create an access policy for the group/user.
  # Otherwise we don't create the access policy
  type = "${length(var.aux_object_id) == 0 ? "without_auxAP" : "with_auxAP" }"
}

module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

# Create the key vault
resource "azurerm_key_vault" "key_vault" {
  name                            = "${module.namespace.name}"
  location                        = "${var.location}"
  resource_group_name             = "${var.resource_group_name}"
  tenant_id                       = "${local.tenant_id}"
  tags                            = "${merge(var.global_tags, var.tags, local.version_tag)}"
  enabled_for_deployment          = "${var.enabled_for_deployment}"
  enabled_for_disk_encryption     = "${var.enabled_for_disk_encryption}"
  enabled_for_template_deployment = "${var.enabled_for_template_deployment}"
  sku {
    name                          = "${local.sku_name}"
  }
  # AzureRM supports multiple access_policies (up to 16 total), but there currently isn't a great
  # way to implement this in Terrform - so we just implement 1 or 2 access policies using count
  count                           = "${local.type == "without_auxAP" ? 1 : 0}"
  access_policy {
    tenant_id                     = "${local.tenant_id}"
    object_id                     = "${local.object_id}"
    certificate_permissions       = ["${var.access_policy_certificate_permissions}"]
    key_permissions               = ["${var.access_policy_key_permissions}"]
    secret_permissions            = ["${var.access_policy_secret_permissions}"]
  }
}

resource "azurerm_key_vault" "key_vault_auxap" {
  name                            = "${module.namespace.name}"
  location                        = "${var.location}"
  resource_group_name             = "${var.resource_group_name}"
  tenant_id                       = "${local.tenant_id}"
  tags                            = "${merge(var.global_tags, var.tags, local.version_tag)}"
  enabled_for_deployment          = "${var.enabled_for_deployment}"
  enabled_for_disk_encryption     = "${var.enabled_for_disk_encryption}"
  enabled_for_template_deployment = "${var.enabled_for_template_deployment}"
  sku {
    name                          = "${local.sku_name}"
  }
  # AzureRM supports multiple access_policies (up to 16 total), but there currently isn't a great
  # way to implement this in Terrform - so we just implement 1 or 2 access policies using count
  count                           = "${local.type == "with_auxAP" ? 1 : 0}"
  access_policy {
    tenant_id                     = "${local.tenant_id}"
    object_id                     = "${local.object_id}"
    certificate_permissions       = ["${var.access_policy_certificate_permissions}"]
    key_permissions               = ["${var.access_policy_key_permissions}"]
    secret_permissions            = ["${var.access_policy_secret_permissions}"]
  }
  access_policy {
    tenant_id                     = "${var.aux_tenant_id}"
    object_id                     = "${var.aux_object_id}"
    certificate_permissions       = ["${var.aux_access_policy_certificate_permissions}"]
    key_permissions               = ["${var.aux_access_policy_key_permissions}"]
    secret_permissions            = ["${var.aux_access_policy_secret_permissions}"]
  }
}
