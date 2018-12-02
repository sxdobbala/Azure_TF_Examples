module "namespace" {
  source      = "git::https://github.optum.com/CommercialCloud-EAC/terraform_common//terraform_module/namespace?ref=v1.2.1"
  name        = "${var.name}"
  namespace   = "${var.namespace}"
  name_format = "%s-%s"
}

# Create the key vault certificate (without lifetime action)
resource "azurerm_key_vault_certificate" "certificate_without_lifetime" {
  count              = "${length(var.lifetime_action_type) == 0 ? 1 : 0}"
  name               = "${module.namespace.name}"
  vault_uri          = "${var.vault_uri}"
  tags               = "${merge(var.global_tags, var.tags)}"
  certificate_policy {
    issuer_parameters {
      name = "${var.issuer_name}"
    }
    key_properties {
      exportable = "${var.private_key_is_exportable}"
      key_size = "${var.key_size}"
      key_type = "${var.key_type}"
      reuse_key = "${var.reuse_private_key_for_renewal}"
    }
    secret_properties {
      content_type = "${var.content_type}"
    }
    x509_certificate_properties {
      key_usage = [ "${var.x509_key_usage}" ]
      subject = "${var.x509_subject}"
      validity_in_months = "${var.x509_validity_months}"
    }
  }
}

# Create the key vault certificate (with lifetime action)
resource "azurerm_key_vault_certificate" "certificate_with_lifetime" {
  count              = "${length(var.lifetime_action_type) == 0 ? 0 : 1}"
  name               = "${module.namespace.name}"
  vault_uri          = "${var.vault_uri}"
  tags               = "${merge(var.global_tags, var.tags)}"
  certificate_policy {
    issuer_parameters {
      name = "${var.issuer_name}"
    }
    key_properties {
      exportable = "${var.private_key_is_exportable}"
      key_size = "${var.key_size}"
      key_type = "${var.key_type}"
      reuse_key = "${var.reuse_private_key_for_renewal}"
    }
    secret_properties {
      content_type = "${var.content_type}"
    }
    lifetime_action {
      action {
        action_type = "${var.lifetime_action_type}"
      }
      trigger {
        days_before_expiry = "${var.lifetime_action_trigger_days_before_expiry}"
      }
    }
    x509_certificate_properties {
      key_usage = [ "${var.x509_key_usage}" ]
      subject = "${var.x509_subject}"
      validity_in_months = "${var.x509_validity_months}"
    }
  }
}
