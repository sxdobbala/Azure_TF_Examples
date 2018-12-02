locals {
  sql_server_version = "${lookup(var.sqlserver_version_types, var.sqlserver_version)}"
}

resource "azurerm_sql_server" "Server" {
  name                         = "${var.sqlserver_name}"
  resource_group_name          = "${var.rg_name}"
  location                     = "${var.rg_location}"
  version                      = "${local.sql_server_version}"
  administrator_login          = "${var.admin_id}"
  administrator_login_password = "${var.admin_pwd}"
}

/* resource "azurerm_template_deployment" "server_auditing" {
  name = "${azurerm_sql_server.Server.name}-enable-auditing"
  resource_group_name = "${var.rg_name}"
  depends_on = ["azurerm_sql_server.Server"]
  deployment_mode = "Incremental"

  parameters {
    "server_name"           = "${azurerm_sql_server.Server.name}"
    "rg_location"           = "${var.rg_location}"
    "audit_storage_account" = "${var.audit_storage_account}"
    "audit_event_types"     = "${var.audit_event_types}"
    "audit_days_to_retain"  = "${var.audit_days_to_retain}"
  }

  template_body = <<DEPLOY
  {
    "$schema": "http://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#",
    "contentVersion": "1.0.0.0",
    "parameters": {
        "server_name": {
            "type": "string",
            "metadata": {
                "description": "The name of the new database server to create."
            }
        },
        "audit_storage_account": {
            "type": "string",
            "metadata": {
                "description": "The name of the storage account in which the auditing is stored"
            }
        },
        "rg_location": {
            "type": "string",
            "metadata": {
                "description": "The location of the database server."
            }
        },
        "audit_event_types": {
          "type": "string",
          "defaultValue": "SUCCESSFUL_DATABASE_AUTHENTICATION_GROUP,FAILED_DATABASE_AUTHENTICATION_GROUP,BATCH_COMPLETED_GROUP",
          "metadata": {
          "description": "The event type to audit."
          }
        },
        "audit_days_to_retain": {
            "type": "string",
            "defaultValue": "90",
            "metadata": {
                "description": "The amount of time to keep audit logs."
            }
        }
    },
    "resources": [
        {
            "name": "[parameters('server_name')]",
            "type": "Microsoft.Sql/servers",
            "location": "[parameters('rg_location')]",
            "apiVersion": "2014-04-01-preview",
            "resources": [
                {
                    "apiVersion": "2017-03-01-preview",
                    "type": "auditingSettings",
                    "name": "Default",
                    "location": "[parameters('rg_location')]",
                    "dependsOn": [
                        "[concat('Microsoft.Sql/servers/', parameters('server_name'))]"
                    ],
                    "properties":   {
                        "state": "Enabled",
                        "audit_storage_account": "[parameters('audit_storage_account')]",
                        "storageEndpoint": "[concat('https://', parameters('audit_storage_account'), '.blob.core.windows.net/')]",
                        "storageAccountAccessKey": "[listKeys(resourceId('Microsoft.Storage/storageAccounts', parameters('audit_storage_account')), providers('Microsoft.Storage', 'storageAccounts').apiVersions[0]).keys[0].value]",
                        "storageAccountSubscriptionId": "[subscription().subscriptionId]",
                        "retentionDays": "[parameters('audit_days_to_retain')]",
                        "auditActionsAndGroups": "[split(parameters('audit_event_types'), ',')]",
                        "isStorageSecondaryKeyInUse": false
                    }
                }
            ]
        }
    ]
}
  DEPLOY
}
*/
