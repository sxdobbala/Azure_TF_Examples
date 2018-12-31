# Azure Redbox Starter

A starter project for developers using Commercial Cloud's Azure Redbox Developer Accounts.

This repo holds Terraform files that deploy infrastructure to a developer's Redbox Account via Jenkins.

This repo will be used to redeploy a developer's infrastructure every week after Redbox Account cleanup occurs.

## Getting Started

1. Obtain a Azure Redbox Developer Account. See details [here](https://commercialcloud.optum.com/docs/redboxintro.html).

## Jenkins Configuration

The `Optumfile.yaml` and `Jenkinsfile` are related to the [JPaC Terraform Pipeline Template](https://github.optum.com/jenkins-pipelines/template-terraform). Further information can be found there.

## Running Terraform Locally

### Required Tooling

- [Install Terraform](https://www.optumdeveloper.com/content/odv-optumdev/optum-developer/en/development-tools-and-standards/infrastructure-as-a-code/hashicorp.html)
- [Install Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest)

### Local Configuration Changes

To run this locally, change `backend.tf` to use "local" rather than "azurerm". `backend.tf` will now look like:

```
terraform {
  backend "local" {}
}
```

> **NOTE:** Do not commit `backend.tf` changes as this will break the jenkins pipeline because each new run of the pipeline would not have access to the previously saved state.

### Commands

Then make sure you are logged in with your user via the Azure CLI and set your subscription to the Commercial Cloud Redbox Subscription:

```bash
$ az login
$ az account set --subscription e480a2a1-a59c-4b77-aa21-ffcbc8243f5f
```

Finally you can run the Terraform code via:

```bash
$ terraform init
$ terraform plan
$ terraform apply
```

Infrastructure can then be destroyed with:

```bash
$ terraform destroy
```
