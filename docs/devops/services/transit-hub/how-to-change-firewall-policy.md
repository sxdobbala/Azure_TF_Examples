---
title: Transit Hub Firewall Change Requests
description: Learn how to request a change to the Transit Hub firewalls.
services: network
ms.service: transit-hub
ms.topic: how-to
ms.reviewers: bashey,egibson,jleonhi
ms.author: bashey
author: bashey
ms.date: 06/20/2021
---

# Overview

The Transit Hub Palo Altos filter traffic at multiple layers. You will need to include the information found in *Table 1* in each request. When specifying CIDRs keep in mind that one side (source or destination) must be specific IPs. Follow the [Service Now engagement](../../itsm/service-now-engagement.md) guide to submit a request to the DevOps Enablement networking team.

| Source IP/CIDR | Source Port | Destination IP/CIDR | Destination Port | [App Type](https://applipedia.paloaltonetworks.com/) |
| -------------- | ----------- | ------------------- | ---------------- | ---------------------------------------------------- |
| x.x.x.x | * | x.x.x.x | TCP or UDP &lt;port no&gt; | Search Palo's [Applipedia](https://applipedia.paloaltonetworks.com/) and list your type here |

*Table 1: General format for requesting firewall changes*
