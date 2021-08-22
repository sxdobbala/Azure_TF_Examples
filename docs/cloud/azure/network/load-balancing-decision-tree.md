---
title: Azure Ingress Decision Tree
description: Choose the best ingress options for your particular use case.
ms.assetid: 658e5022-c034-4f43-aada-ea44c7b8ae57
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "front-door"
ms.author: "bashey"
_tocTitle: "Azure"
---

## Concept

Microsoft Link: [Overview of load-balancing options in Azure](https://docs.microsoft.com/en-us/azure/architecture/guide/technology-choices/load-balancing-overview#decision-tree-for-load-balancing-in-azure)

When selecting the load-balancing options, here are some factors to consider:

- **Traffic type**. Is it a web (HTTP/HTTPS) application? Is it public facing or a private application?
- **Global versus. regional**. Do you need to load balance VMs or containers within a virtual network, or load balance scale unit/deployments across regions, or both?
- **Availability**. What is the service [SLA](https://azure.microsoft.com/support/legal/sla/)?
- **Cost**. See [Azure pricing](https://azure.microsoft.com/pricing/). In addition to the cost of the service itself, consider the operations cost for managing a solution built on that service.
- **Features and limits**. What are the overall limitations of each service? See [Service limits](https://docs.microsoft.com/en-us/azure/azure-subscription-service-limits).

The following flowchart will help you to choose a load-balancing solution for your application. The flowchart guides you through a set of key decision criteria to reach a recommendation.

![Decision tree for load balancing in Azure](media/load-balancing-decision-tree.png)
