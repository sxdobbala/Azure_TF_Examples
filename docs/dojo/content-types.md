---
title: Content Types
description: This document outlines the various content types typically found within the Dojo.
services: dojo
ms.service: dojo
ms.subservice: content
ms.topic: how-to
ms.reviewers: bashey,fcatacut,ksadinen
ms.author: bashey
author: bashey
ms.date: 06/11/2021
---

## Standard Content Types for the Dojo

### Overview

A general synopsis of the topic at hand. Typically contains some background information, perhaps some detail on where the topic is in use at Optum or within the industry, and any other pertinent information.

> **Example:**
>
> The Transit Hub provides a number of connectivity functions for Provider including a Regional WAN, datacenter connectivity via dedicated line or site-to-site VPN, and managed ingress, egress, and cross subscription connectivity.

### Concept

The Concept article should contain information regarding a specific subtopic within the overall topic. For instance, if the overall topic was "Transit Hub" a concept within that topic could be how the Transit Hub connects to other datacenters. A concept article typically deals with theory rather than practical application (see "Learn" for practical application).

> **Example:**
>
> The Transit Hub utilizes Palo Alto devices for site-to-site Virtual Private Network (VPN) connections to other datacenters. Traffic routing is typically setup using Network Address Translation (NAT) on the VPN connection in order to protect against IP conflicts.

### Get Started

A getting started article typically provides an introduction to a topic. It should guide a person who has never worked with the subject matter. Developers often call this the "Hello World" article. It should cover a non-exceptional use case for the topic at hand.

> **Example:**
>
> Connecting an Azure Virtual Network to the Transit Hub begins with an engagement with the DevOps Networking team. Here is how to begin that process…

### Architecture

An architectural article should provide deep introspection into the underlying platform or structure behind a given topic. Typical examples include physical/virtual infrastructure, traffic flows, software diagrams, etc.

> **Example:**
>
> The Transit Hub utilizes Azure Virtual WAN in combination with Palo Altos setup in a Transit Hub configuration to enable communication across Azure's cloud and to our various datacenters.
>
> &lt;insert network diagram here&gt;

### Deploy

A deployment article should help the reader deploy an instance of the topic to their own infrastructure. Hence, not all topics will have deployment articles.

> **Example:**
>
> The Transit Hub is deployed using Terraform via a Jenkins Pipeline. To deploy it, copy the example found in the Transit Hub GitHub Repo, store that in your own GitHub repository, then create a build in Jenkins passing in the following parameters…

### Download

Downloads are typically direct links to downloadable content. Downloadable content should be either stored in the media folder or linked to another Optum location. Typically, do not link to external sources unless the host is a well-known vendor with relatively static content.

### How To

The "How To" article deals with implementing specific areas of a topic. It should be a step-based article that walks the reader how to perform a specific need.

> **Example:**
>
> To enable traffic between networks connected with the Transit Hub a firewall change must occur on the Palo Altos. Here are the steps to submit a firewall change request: Step 1, Step 2, Step 3, etc.

### Learn

The "Learn" article typically focuses the practical application of a topic. It should focus on specific use cases within the topic and guide the reader through the solutions to those use cases.

> **Example:**
>
> As a product that is moving from an M&A (merger and acquisition) datacenter to Azure a number of requests need to occur. This article will teach the reader the concepts and processes required to establish connectivity to the datacenter, routing traffic across that connection, and allowing specific flows into the Azure Virtual Network including Palo Alto requests and network security group rules within the target Virtual Network.

### Reference

A reference article typically provides additional information around the theory, technologies, and history behind a given topic.

> **Example:**
>
> The Transit Hub uses Layer 3, 4, and 7 inspections. These layers are described in the OSI Model and here is a reference article on the OSI Model.

### Tutorial

The Tutorial article should teach the reader how to implement specific subtopics within a given topic. It is similar to the getting started article but typically expands to cover various areas that are not considered starting areas.

> **Example:**
>
> The Transit Hub supports managed egress for Virtual Machines in the cloud. Here are the steps to enable managed egress for your Virtual Machines. Step 1… Step 2… Step 3…

### What's New

Any content announcing a new feature or features for a given topic. Typically, these take a slightly informal almost blog like approach and provide a summary of the new feature set while directing the user on towards content that can provide deeper detail.

> **Example:**
>
> The Transit Hub now supports the CentralUS and EastUS2 Azure regions. All functions outlined in the documentation are available.
