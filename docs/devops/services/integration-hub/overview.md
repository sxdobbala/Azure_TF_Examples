---
title: Integration Hub Overview
description: The Integration Hub provides a series of services geared towards managing Virtual Machines in the cloud. 
services: virtual-machine
ms.service: integration-hub
ms.topic: overview
ms.reviewers: bashey,egibson,jleonhi
ms.author: bashey
author: bashey
ms.date: 06/20/2021
---

# Overview

- **ASK ID:** UHGWM110-026738
- [Original Integration Hub Whitepaper](media/ProviderIntegrationHub.docx)

While Optum's general policy is to move away from Virtual Machines as part of a cloud migration this is not always feasible. Vendor software requirements, datacenter closures, and a number of other valid reasons have arisen during Provider's cloud migration that entail lifting *some* virtual machines into the cloud as part of a product migration. Still, Provider's success rate in reducing over 8,000 virtual devices to a few hundred is commendable.

To support those few hundred remaining Virtual Machines Provider must implement over [250 controls](media/ProviderVirtualWAN_UHG_Policy_Controls.xlsx) found in EGRC policy relating to non-immutable virtual machines. These policies cover everything from managing logins to deployments to patching schedules. The Integration Hub is an umbrella term for all the services that have been created to address the aforementioned policy requirements.

## Integration Hub Services

The sections below cover the major services offered by the Integration Hub. The list is non-exhaustive as new services are being added as need arises. Check back here for additional listings.

### Active Directory (Traditional)

The Integration Hub provides a traditional Active Directory Domain named "HUB". This domain has been hardened using Optum's own group policy and enforces all the applicable EGRC policies relating to login management and operating system controls. The DevOps team is working on connecting the HUB domain to Secure to facilitate the SOC2 requirements levered by a number of Provider products.

### Ansible Configuration as Code

An Ansible cluster has been created within the Integration Hub. Ansible provides configuration as code capabilities for non-immutable virtual machines. It is the standard with which Provider deploys virtual machines.

### Hardened Operating System Images

The default images available on the Azure marketplace come with quite a few vulnerabilities. The DevOps and Cloud Enablement teams have created a series of images that contain hardened images for use by the product teams.

### Management Network

The DevOps team has established a management network within the Integration Hub for use by any team connected to the Hub. This negates the need for each team to management a Bastion gateway thereby reducing cost. Further, a number of jumpbox images have been created with commonly required tools (SQL Management Studio, Putty, etc.).

### Patch Management

Patch management is currently (06.21.2021) in-progress by the DevOps team. Check back here for future updates.

## Transit Hub Requirement

To utilize the Integration Hub product teams must first attach their networks to the [Transit Hub](../transit-hub/index.yml). This is required to allow managed network access to the various services found within the Integration Hub. All traffic will be moderated by the Transit Hub firewalls.

## Chargebacks

There is, currently, no cost for utilizing Integration Hub services. This is subject to change depending on leadership guidance.
