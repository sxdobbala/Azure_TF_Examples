---
title: Transit Hub VPN Architecture
description: The Transit Hub supports Site-to-Site VPN connections to most locations and devices.
services: network
ms.service: transit-hub
ms.topic: architecture
ms.reviewers: bashey,egibson,jleonhi
ms.author: bashey
author: bashey
ms.date: 06/20/2021
---

# Overview

Provider has substantial need for Virtual Private Network (VPN) connectivity. Common uses cases include HL7v2 connectivity to hospitals, vendor connectivity for processing, and connectivity to remote datacenters. The Transit Hub services this connectivity via Palo Alto devices in a highly-available, active-passive configuration. Each device is currently configured to handle up to 3Gbps but can be vertically scaled to achieve up to 8Gbps. Today, all clients are using the same scaled cluster but, should the need arise, the installation can support dedicated VPN clusters. 

![Provider VPN Appliances](media/ProviderVirtualWan%20-%20PaloSandwhich.png)

*Figure 1: Provider VPN appliances are represented in the lower right of this image.

## Highly Available Configuration

The Palo Alto VPN appliances are configured in the Azure EastUS region in an active-passive configuration with each device in its own availability zone. This provides single region resiliency should an appliance fail or be brought down for patching/servicing. In the case of a regional disaster the VPN Appliances will be brought online in the CentralUS region (this will, however, change the public IP). Clients would only need to update the peer IP in their devices to come back online.

## Network Address Translation

One of the critical features provided by the Palo Alto appliances is the ability to perform network address translation (NAT) on either the source (SNAT) or destination (DNAT) of a VPN packet. This allows the Transit Hub to work with peered network even if the networks have overlapping IP ranges. In the Transit Hub's case all connections are currently mapped to an IP in the 10.225.216.0/23 range. So from the Optum side all traffic will appear to come from an IP in that range. From the client's perspective we also support a number of combinations: the client can provide an IP to use, we can provide a public IP, or the client can simply use the 10.225.216.0/23 IP. The DevOps engineer can help implementations work through this decision during each engagement.

## Chargebacks

There is, currently, no cost for utilizing Transit Hub services. This is subject to change depending on leadership guidance.
