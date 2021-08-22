---
title: Transit Hub Virtual WAN Architecture
description: The Transit Hub utilizes Azure Virtual WAN to communicate between Azure regions and to connect to regional hubs.
services: network
ms.service: transit-hub
ms.topic: architecture
ms.reviewers: bashey,egibson,jleonhi
ms.author: bashey
author: bashey
ms.date: 06/20/2021
---

# Overview

The Transit Hub leverages Azure's [Virtual WAN](https://docs.microsoft.com/en-us/azure/virtual-wan/virtual-wan-about) service to facilitate connectivity between Azure regions and regional hubs like GA777. Azure Virtual WAN (VWan) provides 50Gbps backhaul traffic between regions and the VWan hubs provide a connection point for dedicated lines like ExpressRoute.While the VWan Hubs do support VPN connectivity the functionality is somewhat limited. Therefore we utilize the Palo Alto devices for Site-to-Site VPN tunnels.

In Provider's current setup, all routes are available to all Hubs (i.e. they are advertised to all endpoints). Each Hub is connected to one and only one virtual network in [Transit Hub](architecture-firewalls.md) configuration. All traffic flows freely over the VWan but is analyzed by the Palo Altos within each Transit Hub before carrying on to its destination.

## Data Transfer Charges

Traffic routing across the Virtual WAN is [43% cheaper](https://azure.microsoft.com/en-us/pricing/details/virtual-network/) ($0.02/GB vs $0.035/GB) versus using Global Peering between Azure Virtual Networks.

Any traffic entering Azure is not charged. Traffic leaving Azure (via VPN, ExpressRoute, or Point-to-site User VPN connections) is subject to the standard [Azure data transfer charges](https://azure.microsoft.com/pricing/details/bandwidth/).

For data transfer charges between a Virtual WAN hub, and a remote Virtual WAN hub or VNet in a different region than the source hub, data transfer charges apply for traffic leaving a hub. Example: Traffic leaving an East US hub will be charged $0.02/GB going to a West US hub. There is no charge for traffic entering the West US hub. All hub to hub traffic is subject to Inter-Region (Intra/Inter-continental) charges [Azure data transfer charges](https://azure.microsoft.com/pricing/details/bandwidth/).
