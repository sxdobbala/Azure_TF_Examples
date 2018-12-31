# Clean up Terraform resources

Often times when working on projects in Redbox, you'd like to 'wipe the slate clean' and start fresh.  This directory contains the right files to instruct Terraform to delete any resources it knows about - thereby resetting your Redbox to a 'clean' state and emptying out all objects in the terraform.tfstate file (in Azure Storage).

:exclamation: :exclamation: :exclamation: USE THIS AT YOUR OWN RISK :exclamation: :exclamation: :exclamation:

It will **not** remove any objects/resources built manually via Azure Portal, Azure CLI, nor the storage account set up by Redbox provisioning (i.e. that contains the Terraform state file).

**Due to a few race conditions/bugs still in the Azure provider/API/Terraform, you may need to replay the Jenkins job multiple times until all resources are cleaned up.  This issue manifests itself as errors in Jenkins like this for one or more resources:**

```
Error: Error applying plan:

3 error(s) occurred:

* module.subnet01.azurerm_subnet.subnet (destroy): 1 error(s) occurred:

* azurerm_subnet.subnet: Error deleting Subnet "demo1qz7g04hwwt77" (VN "demo1qz7g04hwwt77" / Resource Group "someone1-01"): network.SubnetsClient#Delete: Failure sending request: StatusCode=0 -- Original Error: autorest/azure: Service returned an error. Status=400 Code="InUseSubnetCannotBeDeleted" Message="Subnet demo1qz7g04hwwt77 is in use by /subscriptions/****/resourceGroups/someone-01/providers/Microsoft.Network/networkInterfaces/demo1qz7g04hwwt77/ipConfigurations/primary_config and cannot be deleted." Details=[]
```
