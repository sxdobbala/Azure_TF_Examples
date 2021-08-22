---
title: Provider Cloud Email Relay
description: The Provider Cloud Email Relay allows products to send email through the Optum email relays including support for distribution groups.
services: email
ms.service: email
ms.subservice: core
ms.topic: overview
ms.reviewers: bashey,rspano1
ms.author: bashey
author: bashey
ms.date: 06/18/2021
---

# Provider Cloud Email Relay

> **Note:**
> The Relay supports the products under Vinny Patalano's Provider pillar (Optum360). Products outside that portfolio will need to stand up and operate their own relays. If you would like a demonstration of Provider's implementation please contact the [Provider Cloud Enablement team](mailto:ProviderCloudEnablement@ds.uhc.com?subject=Email%20Relay%20Demo).

> **Note:**
> The Relay is not meant to be used for marketing blasts or other large volume requests. Please follow [Optum Policy](https://helpdesk.uhg.com/At_Your_Service/SW/Pages/emailspoofing.aspx) for sending such emails.

## Overview

The Cloud Email Relay services both Azure and AWS and provides deep integration with Optum's email services including support for distribution groups, routing all email through Optum's Data Loss Protection (DLP) stack (i.e. we scan them for PxI), and provides encrypted channels for sending email. All the normal Optum imposed restrictions apply (attachment size, messages per day, etc.).

## Architecture

The relays reside in Azure and are setup in a highly available configuration across multiple availability zones. Therefore patching and other changes are performed while the overall service remains online and available. The underlying email server is [hMail Server](https://www.hmailserver.com/), running on Windows, and configured in a secure encrypted manner. Secure SMTP (TLS) is the supported protocol on TCP port 587. All Azure Cloud and AWS CIDRs (IP Ranges) are whitelisted.

## Using the Relay

&nbsp;

| Host&nbsp;&nbsp; | &nbsp;&nbsp;Port&nbsp;&nbsp; | &nbsp;&nbsp;Encryption&nbsp;&nbsp; |
| ---- | ---- | ---------- |
| omail.o360.cloud&nbsp;&nbsp; | &nbsp;&nbsp;TCP 587&nbsp;&nbsp; | &nbsp;&nbsp;TLS 1.2 (required)&nbsp;&nbsp; |

&nbsp;

### Requesting an Account

To request an account on the relay please send an email to [Provider Cloud Enablement](mailto:ProviderCloudEnablement@ds.uhc.com?subject=Email%20Relay%20Account%20Request) and include the following information:

- The product that will be utilizing the service (ASK ID or name)
- A brief description of the emails being sent (e.g. "Forgot Password", "Reports are done", etc.)
- The expected email volume per day

## References

- [General Email Services](https://helpdesk.uhg.com/At_Your_Service/SW/Pages/Email.aspx)
