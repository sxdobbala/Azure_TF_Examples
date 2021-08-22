---
title: SFTP in Public Cloud
description: Describes the recommended SFTP solution for applications hosted in the public cloud. 
ms.assetid: b2eb3a46-1d35-4ed4-94e5-843989a9080a
ms.topic: concept
ms.date: 06/11/2021
ms.custom: ""
ms.services: "secure-transfer"
ms.author: "skidd"
_tocTitle: "Azure"
---

## SFTP for Public Cloud

The current recommendation for SFTP across the enterprise is ECG for both applications deployed within an Optum data center and in the public cloud. ECG is a complete File Transfer Management (FTM) that supports reliable and EIS compliant file transfer over several protocols including SFTP and HTTPs. Centralized auditing, encryption, compression, automated routing and job scheduling are also part of the ECG solution.

For products that reside in Azure, ECG can push files via HTTPS directly to an Azure Storage Account. To create a new ECG configuration to push to an Azure Storage Account you must setup a meeting with the ECG team to manually configure the connection. More detailed instructions can be found [here](https://hubconnect.uhg.com/docs/DOC-247339). Outbound file transfers can be handled by direct SFTP from the application to ECG.

## Handling Large Transfers

Inform the ECG team if you expect that very high volume or file sizes greater than 50GB. ECG is capable of accommodating high volumes and file sizes but will isolate your transfers to avoid impact to other users.


## Cloud Based Options

There are a few simple pre-packaged SFTP solutions such as SFTP Gateway available in the Azure Marketplace. SFTP Gateway and similar solutions utilize Azure Storage Accounts and virtual machines with software that supports basic SFTP functionality. The total cost for the solution will be comprised of the cost of the virtual machine, storage and software licensing cost. EIS compliance and a solution for appropriate auditing and entitlement review will need to be identified by the application team prior to electing this approach. **This is not a recommended solution at this time.**

Cloud based FTM solutions such as MoveIT and Globalscape have comparable features to ECG. These higher cost alternatives are more viable implemented as a shared capability across the provider portfolio. This may be a future consideration but is not currently recommended.

## ECG Public Cloud Roadmap

While ECG has discussed long term plans to migrate to the public cloud there is no work currently scheduled. In the meantime, teams are strongly encouraged to use the ECG to Azure Storage Account solution in order to take advantage of the security and auditing as well as offer a uniform experience for our external clients across the enterprise.
