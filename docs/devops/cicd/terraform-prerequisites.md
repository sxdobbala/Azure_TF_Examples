---
title: Terraform Prerequisites
description: Setup your machine for terraform development
ms.assetid: a8154de8-f3ed-4b5a-aedb-ba1c6194e6ae
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "terraform"
ms.author: "rspano1"
---

# Overview

The instructions below will guide you through setting up your local workstation for developing infrastructure as code (IaC) using [HashiCorp's Terraform](https://www.terraform.io).

## Step 1 - Install Terraform

### PC users - Terraform

Install the [HashiCorp Terraform](http://appstore.uhc.com/AppInfo/AppVersionId/19881?BackToList=/AppList/AppList) package (v0.12.24 or later if available) from the AppStore.

### Mac Users - Terraform

```powershell
brew install terraform
```

Verify the installation from any Command, PowerShell, or terminal prompt using:

```bash
terraform -v

Terraform v0.12.24
```

See this link for more information about [Terraform](https://www.terraform.io/).

## Step 2 - Install Azure CLI

### PC Users - Az CLI

Install the [Microsoft Azure CLI](http://appstore.uhc.com/AppInfo/AppVersionId/18476?BackToList=/AppList/AppList) package (v2.0.63 or later if available) from the AppStore.

### Mac Users - Az CLI

```bash
brew install azure-cli
```

Verify the installation from any Command or PowerShell prompt using:

```bash
az -v

azure-cli                         2.0.76 *
```

You can find specific releases and release notes in this [azure-cli](https://github.com/Azure/azure-cli/releases) GitHub repo.

### Step 3 - Install Git

#### PC and Mac Users

Install the [Git](http://appstore.uhc.com/AppInfo/AppVersionId/17318?BackToList=/AppList/AppList) package from the AppStore.

Verify the installation from any terminal using:

```bash
git --version

git version 2.26.2
```

You can find Git documentation [here](https://git-scm.com/doc).

## Step 4 - Get your ENTID's

An ENTID is required for Azure Cloud Access. Here are the [instructions]](https://commercialcloud.optum.com/docs/getting-started/access-azure-portal/) for setting up your ENTID.

## Step 5 - Login to your Azure Cloud Subscription

After you have the prerequisite applications installed, use the Azure CLI command line to log in and view your account status.

```bash
az account login

# this will open up a login prompt for Microsoft Azure in your browser - login there with your RSA token.
```

You can view your current subscription using:

```bash
az account show
{
  "environmentName": "AzureCloud",
  "id": "af030d48-2dc2-434f-8a9d-737be02070bb",
  "isDefault": true,
  "name": "o360 Delivery Sandbox non-prod",
  "state": "Enabled",
  "tenantId": "db05faca-c82a-4b9d-b9c5-0f64b6755421",
  "user": {
    "name": "bashey4@entid.optumhub.net",
    "type": "user"
  }
}
```

If you have multiple subscriptions, set your current login to a specific subscription like this:

```bash
az account list
# shows list of all subscriptions your user has access to. Copy the guid of the subscription you want to manage.
az account set -s xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

## Start Terraforming

Congratulations! You're all set to start Terraforming! We highly recommend working through some of the "web stack" profiles on the [Dojo Innersource Organization](https://github.optum.com/Dojo360/azure-web-stack).

For each example simply follow these steps:

- "git clone" the given repo to your local machine
- Navigate to the appropriate example directory
- Change any &lt;CHANGE_ME&gt; values to something appropriate and save your work
- At the prompt perform a "terraform init" - this will initialize the example
- Next, enter "terraform plan" - this will test run the script without actually making any changes. You can view the proposed changes in the console.
- Finally, enter "terraform apply" followed by "yes" on the prompt to proceed. This will apply the changes to your Azure Subscription.
- When you're done with the resources simply delete the resource group and all its contents.

Good luck, have fun, and reach out to us via any of the means located in the footer if you need some help.
