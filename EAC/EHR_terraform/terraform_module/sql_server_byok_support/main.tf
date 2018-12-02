resource "null_resource" "keyvault_enable_soft_delete" {
  provisioner "local-exec" {
    command = "az resource update --id /subscriptions/${var.keyvault_subscription_id}/resourceGroups/${var.rg_name}/providers/Microsoft.KeyVault/vaults/${var.keyvault_name} --set properties.enableSoftDelete=true"
  }
}

resource "null_resource" "sql_server_assign_identity" {
  provisioner "local-exec" {
    command = "az sql server update -n ${var.sqlserver_name} -g ${var.rg_name} --assign_identity"
  }
}

resource "null_resource" "key_vault_provide_permissions_to_encryption_key" {
  provisioner "local-exec" {
    command = "az keyvault set-policy --name ${var.keyvault_name} --object-id $(az sql server show --resource-group ${var.rg_name} --name ${var.sqlserver_name} --query identity.principalId) --key-permissions get wrapKey unwrapKey"
    interpreter = ["powershell", "-command"]
  }
  depends_on = ["null_resource.sql_server_assign_identity"]
}

resource "null_resource" "sql_server_add_encryption_key" {
  provisioner "local-exec" {
    command = "az sql server key create -g ${var.rg_name} -s ${var.sqlserver_name} -k ${var.key_uri}"
  }
  depends_on = ["null_resource.key_vault_provide_permissions_to_encryption_key"]
}

resource "null_resource" "sql_server_set_encryption_protector" {
  provisioner "local-exec" {
    command = "az sql server tde-key set -g ${var.rg_name} -s ${var.sqlserver_name} -t AzureKeyVault -k ${var.key_uri}"
  }
  depends_on = ["null_resource.sql_server_add_encryption_key"]
}