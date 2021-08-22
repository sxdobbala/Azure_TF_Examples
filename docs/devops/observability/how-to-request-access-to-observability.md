---
title: How to Request Access to Observability
description: Use Secure to enable your access to Provider's Observability instance.
ms.assetid: 7c200fe7-a9ec-4732-be66-8e05131765ee
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "mnegron"
---

# Overview

Splunk Observability (formerly SignalFx) is accessible via UHG's Single-Sign-On (SSO) function.  In order to login to Observability you must be a member of an Active Directory Global Group specific to the product(s) you need access to.

## Prerequisites

- An MS Domain Account

## Steps to request AD Membership

- Navigate to [Secure](https://secure.uhc.com).
- Select "Add Group Membership".
- Enter the AD Group ```Optum_Provider_SignalFX``` into the "Search groups by group name:" field.
- Select the group ```Optum_Provider_SignalFX``` from the "Available Groups" list and press the Green Arrow to move the selected group to the "Selected Groups" list. Repeat this action for each AD Group you need access to in Splunk Observability.
- Select "Next".
- Select "Permanent" in the "Access Expires" field.
- Add a reason for the requested access in the "Description of why access is required:" field.
- Select "Submit".
