---
title: BASE vs PADU
description: Learn how BASE evolved out of PADU.
ms.assetid: 2165bf34-0542-4636-8462-25fcbf29de24
ms.topic: learn
ms.date: 06/11/2021
ms.custom: ""
ms.services: "dojo"
ms.author: "bashey"
_tocTitle: "Dojo"
---

## Concept

The Dojo originally utilized the [UHG PADU](https://github.optum.com/ea/techlandscape/wiki) (Preferred, Acceptable, Discouraged, and Unacceptable) on the Terraform profiles as a way of guiding teams to the correct implementations. The system worked well when attempting to provide an obsolesence path for Dojo modules however presented some challenges when viewed from the perspective of the enterprise. PADU itself is managed by a central committee and anything that bears the PADU symbol(s) has been through that process and is valid for any division within the enterprise. Therein lies the issue; the Dojo modules take PADU into account but often have architectural decisions specific to the Provider division within them. Said another way, they are not always meant for Enterprise-wide adoption. Due to this understanding we decided to use a different accronym that essentially means the same thing. That accronym is BASE (Best, Adequate, Subpar, and Excluded). BASE takes PADU into account when it is being assigned.

| Rating | Description |
| ------ | ----------- |
| ![Best BASE](media/best.png) | A Dojo infrastructure profile marked with "Best" represents the current optimal way of implementing a given infrastructure platform on the Cloud Service Provider. These profiles are considered production ready and provide functionality such as high-availability, backup, observability, and more. |
| ![Adequate BASE](media/adequate.png) | Dojo infrastructure profiles marked with "Adequate" represent architectural patterns that are still considered sufficient for most purposes. However it is up to you, the user, to make that determination. Profiles that were deprecated from "Best" to "Adequate" often represent an evolution in our understanding (or new functionality by the CSP). |
| ![Subpar BASE](media/subpar.png) | Dojo infrastructure modules marked with "Subpar" typically represent architectural patterns that are only useful in development or are actively being sunset. In general, no new production usage should be performed on "Subpar" profiles. Likewise, any existing implementations should have a roadmap plan to migrate away from the profile. |
| ![Excluded BASE](media/excluded.png) | Dojo profiles marked "Excluded" should not be used in any circumstances. New or existing usage my require a PEX to continue usage. These profiles are typically marked "Excluded" because they have inherent security risks, are not resilient enough to be used in production, or are significantly antiquated architectural patterns. |
