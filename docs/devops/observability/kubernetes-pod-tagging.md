---
title: Kubernetes Pod Tagging
description: Tagging your pods drives observability and incident management.
ms.assetid: bb8e3732-c055-41b2-affb-0dcefc6c6510
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "pterrell"
---

# Concept

Using Kubernetes labels within templates to ensure we include pertinent downstream information for assignment of incidents should be a priority.

Example of how this could be applied within a yaml template file:

![K8s Pod Labels](media/k8s-pod-labels.png)

Reference:
[9 Best Practices and Examples for Working with Kubernetes](https://www.replex.io/blog/9-best-practices-and-examples-for-working-with-kubernetes-labels)