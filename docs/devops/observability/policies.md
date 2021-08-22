---
title: Observability Policies and Guidelines
description: The Dojo Observability practices and tools embedded in the IaS (Infrastructure as Code) Engineering framework includes monitoring, logging and alerting using Splunk Observability, Splunk Core and Splunk On-call.

ms.assetid: b7e63913-1fd2-4751-ab8c-7a23648d4af4
ms.topic: learn
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "mnegron"
---

# Overview

The Dojo Observability practices and tools embedded in the IaS (Infrastructure as Code) Engineering framework includes monitoring, logging and alerting using Splunk Observability, Splunk Core and Splunk On-call. Follow this page for on-going updates on Retention Policies and Guidelines and retention policies.

### Retention

The rules for retention vary by Splunk product:

| Instance | Retention Time |
| -------- | --------- |
| Splunk Core Prod Indexes    | 90 days |
| Splunk Core NonProd Indexes | 30 days |
| Splunk Observability | 13 months (aggregated) |

*Logs on Provider's non-production Splunk instance have no retention guarantee.*

### Service Level Targets

Splunk Core is managed by the Provider DevOps team. Hence, the service level targets are managed by the DevOps team. All other tools in the Splunk suite are Software as a Service (SaaS) and driven from the vendor. They are outlined here for ease of conception but are subject to change by the vendor.

| Instance | Target |
| -------- | --------- |
| Splunk Core UI | 99% up-time |
| Splunk Core Log Delay | &lt;15 minutes |
| Splunk Observability UI | >99.9% (SaaS - vendor driven) |
| Splunk Observability Log Delay | Varies by stream up to real-time |
| Splunk Core NonProd | No Target |
| Splunk OnCall | >99.9% (SaaS - vendor driven)

