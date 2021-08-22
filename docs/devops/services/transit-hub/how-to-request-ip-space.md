---
title: Transit Hub IP Space
description: The Transit Hub utilizes the 172.16.0.0/12 private IP range with NAT provided for Optum allocated (i.e. 'core routable') ranges.
services: network
ms.service: transit-hub
ms.topic: concept
ms.reviewers: bashey,egibson,jleonhi
ms.author: bashey
author: bashey
ms.date: 06/20/2021
---

# Concept

Since the Transit Hub spans multiple Azure regions and connects to many other locations a strong IP management strategy is in place. This can be observed in the following graphic whereby the CentralUS Azure regions have a dedicated IP block in the 172.22.0.0/16 range. Likewise a core (Optum) routable NAT range (10.225.220.0/23) is setup on the same region. Any connections traversing the ExpressRoute to an Optum datacenter would utilize one of these NAT IPs.

> **Note:**
>
> The EastUS Transit Hub is setup with several additional IP ranges since it was created before Provider pivoted to the multi-region VWan based structure.

![Transit Hub IP Allocation](media/ProviderVirtualWan%20-%20Provider%20Virtual%20Wan.png)
*Figure 1: Transit Hubs with Virtual WAN*

## Requesting IP Ranges

Whenever a team is creating a net new presence in Azure or AWS they should request IP space from the DevOps enablement team. A request can be submitted via [Service Now](../../itsm/service-now-engagement.md). Provider IP ranges will match the region of the Transit Hub where they will be utilized. Please request enough ranges to cover all your environments including dev, test, performance, uat, and any other environments required.

> **Reminder:**
>
> Azure takes 5 IPs from *every* subnet so be sure to factor that into your equations.