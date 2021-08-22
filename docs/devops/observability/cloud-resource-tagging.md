---
title: Cloud Resource Tagging
description: Tagging your resources is a great way to quickly and reliably get the information you need to optimize your Azure costs.
ms.assetid: 76305314-fa70-4e94-992e-cc139354a69b
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "terraform"
ms.author: "fcatacut"
---

# Concept

In order to optimize your Azure costs, you need all of the information that you can get about your resources.  Tagging your resources is a great way to quickly and reliably get this information.

Reducing costs using tags can be as simple as automating a process to deallocate VMs that are tagged ```Environment:DEV``` at the end (and to allocate them at the start) of each workday.

## Guidelines

Here are some guidelines on how to design and implement an effective resource tagging strategy.

- Identify the tags that you need. Some suggested tags are:
  - ASKID
  - Assignment Group
  - GL Code
  - Division
  - Portfolio
  - Product
  - Component
  - Component Version
  - Environment 
- Set strict rules (naming conventions, spelling, abbreviations *if allowed*) for both tag names and values. Inconsistent tagging can result it overlooked resources. If some people use ```CreateDate``` and others use ```CreatedDate```, ```CreationDate```, or ```DateCreated``` as the tag name, creating an automated process to delete all resources in a subscription tagged ```Environment:QA``` and older than 90 days won't be as effective as one where all of your resources are using the same tag name. **Leave no room for ambiguity.**
- Create a Terraform module to manage your tags *and* force ```terraform plan``` to fail if any are missing. We created an [Optum Tags](https://github.optum.com/Dojo360/optum-tags) module that requires the suggested tags above. Omitting any of them will result in the following error message:
  
  ```terraform
  
  Error: Invalid index on .terraform/modules/optum-tags/locals.tf line #, in locals:
  #:"<tag name>"= var.tags["<tag name>"]
  
  ```

  Implementation is pretty straightforward with the tag enforcement handled in [locals.tf](https://github.optum.com/Dojo360/optum-tags/blob/master/locals.tf):

  ```terraform
  required_tags = {
      "ASKID"             = var.tags["ASKID"]
      "AssignmentGroup"   = var.tags["AssignmentGroup"]
      "GLCode"            = var.tags["GLCode"]
      "Division"          = lookup(var.tags, "Division", "Optum360")
      "Portfolio"         = var.tags["Portfolio"]
      "Product"           = var.tags["Product"]
      "Component"         = var.tags["Component"]
      "ComponentVersion"  = var.tags["ComponentVersion"]
      "Environment"       = var.tags["Environment"]
  }
  ```  

  If a new tag needs to be added, we can prevent ```terraform plan``` from initially failing by making the new tag optional using the ```lookup``` function.

  An example of this can be seen on line 6 above where we check for a "Division" tag. If it's not specified, one is created with a default "Optum360" value. When we're confident that all of our resources have the new tag, we can update the code so that it's now required.

- Update your code to reference your "tagging" module.

  ```terraform

   variable "tags" {
     default = {
       "ASKID"               = "<ASK ID>"
       "AssignmentGroup"     = "<Assignment Group>"
       …
       "Environment"         = "<Environment>"
       "<Optional Tag Name>" = "<Optional Tag Value>"
     }
   }
   
   module "optum_tags" {
     source = "git::https://github.optum.com/Dojo360/optum-tags"
     tags   = var.tags
   }
   
   resource "azurerm_resource_group" "my_resource_group" {
     name     = "my-resource-group"
     location = "<location>"
     tags     = module.optum_tags.tags
   }

  ```

To ensure that your resources have the most up-to-date tags, we recommend omitting the ```ref``` argument as part of your "tagging" modules ```source``` value (see line 12 above).

## Limitations

There are tagging limitations:

- Not all resource types support tags. To determine if you can apply a tag to a resource type, see [Tag support for Azure resources](https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/tag-support).
- Each resource or resource group can have a maximum of 50 tag name/value pairs. If you need to apply more tags than the maximum allowed number, use a JSON string for the tag value.  The JSON string can contain many values that are applied to a single tag name. A resource group can contain many resources that each have 50 tag name/value pairs.
- The tag name is limited to 512 characters, and the tag value is limited to 256 characters. For storage accounts, the tag name is limited to 128 characters, and the tag value is limited to 256 characters.
- Generalized VMs don't support tags.
- Tags applied to the resource group are not inherited by the resources in that resource group.
- Tags can't be applied to classic resources such as Cloud Services.
- Tag names can't contain these characters: ```<```, ```>```, ```%```, ```&```, ```\```, ```?```, ```/```.

Source: [Use tags to organize your Azure resources](https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/tag-resources#limitations") (January 4, 2021)

## Dojo360 Azure Profiles

Teams are encouraged to use the [Dojo360](https://github.optum.com/Dojo360) Azure profiles to provision and manage your resources. Profiles, such as [App Services](https://github.optum.com/Dojo360/azure-app-services) and [Cosmos DB Account](https://github.optum.com/Dojo360/azure-cosmosdb-account), have incorporated the [Optum Tags](https://github.optum.com/Dojo360/optum-tags) module *and* are designed to mitigate risks by ensuring Optum Security Standards are being met.
