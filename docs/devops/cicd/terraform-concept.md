---
title: Terraform Concepts
description: Learn the ins and outs of deploying infrastructure as code (IaC) using HashiCorp's Terraform.
ms.assetid: 9bb9514c-e89c-477f-bf43-ffec9d2246c8
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "terraform"
ms.author: "bashey"
---

# Overview

**TLDR:** Terraform building blocks can be found on the [Dojo's Innersource Repository](https://github.optum.com/dojo360). Most frequently deployed with JPAC and the common Terraform library.

A mere ten years(ish) ago, it was not uncommon for every aspect of a platform to be manually created. If the operations and production control folks were lucky this resulted in development creating numerous word documents outlining all the infrastructure changes for a particular release. If they were *really* lucky, they knew about the changes prior to release day. As one can imagine this created a wee bit of contention between the groups; not to mention the increased change failure rates and release durations

As agile, and later DevOps took hold of the software industry the need to automate these changes became apparent. This need gave birth to two new terms (and a bunch of companies): Configuration as Code (CaC) and Infrastructure as Code. Configuration as Code deals primarily with everything above the Operating System of a particular device. Infrastructure as Code (IaC) deals with all the layers below that including device, network transport, network security, DNS, and more. Chef, Ansible, Puppet, and PowerShell DSC are all examples of Configuration as Code. AWS Cloud Formation, Azure Resource Manager (ARM) templates, and Terraform are all examples of Infrastructure as Code. Configuration as Code is an article unto itself and a topic for another time.

Regarding Infrastructure as Code, Optum has chosen to adopt Terraform as the company standard. With all decisions there are benefits and detractions to this decision. One the one hand Optum engineers develop knowledge of the syntax and logic of Terraform. In this manner they do not have to learn the unique syntax of each IaC technology. All cloud providers have dedicated teams working on Terraform and both Optum and the Cloud Enablement team @Provider have been in direct discussions with these teams. Which brings us to the main detractor: because there is often a bit of "lag" between the release of a feature on a cloud provider and Terraform's support of the feature the teams sometimes must fall back to the cloud native IaC to complete a task. In everyday use the Cloud Enablement team has found that all gaps are typically remediated within 3-6 months. When we need a feature more quickly than that we can contribute the functionality back to the Terraform resource manager for the Cloud in question.

## Terraform Usage

Provider engineering has developed a unique methodology for working with Terraform utilizing a pattern called "Profiles" that allows the end user to build their infrastructure out like Lego blocks. Profiles define a certain way of building the underlying cloud resource. By design profiles are very rigid and opinionated - BUT - there can be as many profiles created as deemed necessary.

There are three types of profiles:

- **Low-level** profiles sit just above the individual cloud resources. The profiles found in the Dojo's [Azure Diagnostics](https://github.optum.com/Dojo360/azure-diagnostics) module is a good example of a low level profile. In this example the module only creates Azure Event Hubs for specific resources in Azure.
- **Composite** profiles combine multiple low-level profiles into a more functional module. The Azure SQL Services module is a good example of this pattern. It combines the diagnostics, Optum tags, Storage Account, and numerous other low-level modules with the Terraform MSSQL resources to create full implementations of SQL databases in Azure.
- **Stack** profiles combine numerous composite modules to create full software stacks. The [Azure Web Stack](https://github.optum.com/Dojo360/azure-web-stack) module combines the aforementioned Azure SQL Services, Azure Ingress, Azure Networking, and other modules to create a serverless web stack for Azure. An engineer can deploy a full stack in as little as five minutes that is fully plumbed into Provider's health monitoring infrastructure.

All Terraform modules on the Dojo follow the same [template](https://github.optum.com/Dojo360/dojo-template-terraform). For existing modules, pull requests are strongly encouraged and appreciated. Substantial contributions generally result in additional compensation through UHG's Bravo program.

## Utilizing Dojo Profiles in your own Code

Engineers are strongly encouraged to reuse Dojo modules directly in their code to minimize duplication and reduce cost in operations. To reference a Dojo module simply point the source ("src") attribute of your module to the appropriate profile in the desired Dojo module. Be sure to reference the version number (found under the "Releases" tab in each repository). Failing to reference the version number may result in unplanned failures due to changes at the repository HEAD. Here's an [example](https://github.optum.com/Dojo360/azure-diagnostics/tree/master/profiles/app-service-event-hub-diagnostics) using the aforementioned Azure Diagnostics module; note the "&lt;CHANGE_ME&gt;" for the version number and other appropriate values:

```terraform
module "app_service_diagnostics" {
     source = "git:/https://Dojo360/azure-diagnostics//profiles/app-service-event-hub-diagnostics?ref=&lt;CHANGE_ME&gt;"
     app_service_resource_id = azurerm_app_service.diagnostics_example_as.id
     app_service_name = azurerm_app_service.diagnostics_example_as.name
     eventhub_namespace_name = "&lt;CHANGE_ME&gt;"
     eventhub_namespace_resource_group_name = "&lt;CHANGE_ME&gt;"
}
```

## Planning your Terraform Implementation

There are three factors to consider when planning and separating Terraform deployments: 1) what are the logical separations in the infrastructure, 2) what is the frequency of change for a given component, and 3) what is the critical importance of the component. We'll examine each of these in turn:

### What are the Logical Separations?

To preface this entire section: **for a single isolated microservice it is actually highly desirable to deploy all layers at once**.
**However, most of our apps are not single, isolated, microservices…**

For all the brown field apps out there: Terraform modules and their associated deployments generally align with the Layers of the infrastructure:

- [Cloud subscription bootstrapping](https://github.optum.com/Dojo360/azure-subscription-bootstrap) should be one deployment and should handle all your common storage, IAM, container registry, and diagnostic needs *except*
- The [networking layer](https://github.optum.com/Dojo360/azure-networking) should be the next deployment and should include all the networking components including virtual networks/virtual private clouds, subnets, security groups, route tables, and method of remote access. Load Balancers *might* be in this deployment but are typically in the compute deployments. DNS subdomain delegation is typically handled here. We do often see Azure Front Door, WAF, and Application Gateway deployed here (but updated with the compute deployments).
- Any [database](https://github.optum.com/Dojo360/azure-sql-services) modules/deployments are next. There should generally be a separate deployment for each database. This is due to the critical nature of each database. It would be really unfortunate if a Terraform coding error dropped your production database and all its data…
- A number of compute deployments should be next. The teams vary greatly in their implementations here - *and that's OK*. Should all components be deployed together? Should everything be separate? Reality is often somewhere in the middle with a combination of [serverless](https://github.optum.com/Dojo360/azure-app-service), [container](https://github.optum.com/Dojo360/azure-kubernetes-services), and (as a last resort) [virtual machine](https://github.optum.com/Dojo360/azure-virtual-machine). Creation of subdomains typically occurs at this layer. As previously mentioned any updates to Front Door or other "front-end" components should occur here.
- Other layers, for instance IOT, Big Data, Elastic Search, and Caching should all be separated from other components.

### What is the Frequency of Change for a Given Component?

The rule here is fairly straightforward: the larger the difference in rate of change between two components the more they should be separated into separate Terraform modules and deployments. If the application's Azure App Service(s) changes weekly but the database only changes once a month then it may be desirable to separate the modules.

### What is the Critical Importance of the Component?

Given that Terraform is a new technology and many developers are still learning how to appropriately define their infrastructure unexpected changes can *and often do* occur. Those changes often result in infrastructure being unexpectedly deleted and recreated. For the immutable compute infrastructure this really doesn't matter. For the 20TB production database that is still trying to serve production traffic while the deploy is happening this can be disastrous. For this reason, we strongly recommend separating components due to the critical nature of the component. Likewise, **we strongly recommend making prodigious use of Locks in your production environments**. Any critical component containing data that could be lost due to an unexpected change should typically be locked.

> **We can't state this enough - lock your databases people.**

## "Building" Terraform

Those familiar with Terraform always get a funny look in their eyes when we talk about this step. The key concern here is the requirement that we are able to show what Terraform code was deployed for a given version. The teams typically solve this by combing two methods: 1) they tag the Terraform repository with a version as one of the first steps and 2) they typically store a copy of that version of the Terraform in Artifactory. Why the need for the second step? For SOC2 audits it is the easiest way to determine that the stored version of Terraform has not been tampered with (by MD5 hash). SOC2 requires that we can prove that no developer altered the code between development and production.

## Deploying Terraform

**Recommendation**: Use a single Azure Storage Account or AWS S3 Bucket to store your Terraform state. For Azure based platforms, the [Azure subscription bootstrap](https://github.optum.com/Dojo360/azure-subscription-bootstrap) module sets this up for you.

While there is no requirement to use one tool over another for deploying Terraform the majority of teams that we work with leverage Jenkins Pipeline as Code (JPAC) to deploy Terraform.
