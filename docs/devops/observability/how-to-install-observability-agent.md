---
title: How to Install the Observability Agent
description: Learn how to install the Splunk Observability agent.
ms.assetid: 47c3dd6b-c771-4ed4-868e-967654c8be5d
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "mnegron"
---

# Overview

This document covers the Observability Agent install pre-requisites, the various ways of installing the Observability Agent, the configuration needed for the agents to report server metrics to the open telemetry collector, and will introduce you to the Observability agent installation process and the different components involved. e.g. Ansible, Observability agent, telemetry collector, etc.).

## Pre-requisites

Setup the files for the Observability Agent Installation

- Check firewall rules if the server is not present in the DMZ. Port 443 outbound needs to be enabled if the server does not exist in the DMZ. Observability urls need to be able to be accessed from the server to be monitored.

## Observability Client Installation

- Ansible is used to run an ansible role that would install the agent on the target machine.
- The Ansible role uses the yum artifacts in UHC Artifactory which are in turn used to install the agent on the DMZ servers as the DMZ servers don't have access to yum repos from the internet.
- Observability agents are configured to route the metrics through the open telemetry collector.
- Open Telemetry collector is a service that is installed on dedicated load balanced servers in the DMZ that act as a proxy to route metrics from all DMZ servers to Observability. Open telemetry servers have the ports open to Observability. This eliminates the need to request FW rules to route traffic directly to Observability from all the servers in DMZ that are running Observability agent.
- There is no need of a firewall request if when deploying the Observability agent to the AppDB zone in the DMZ.
- For Presentation Zone and Database Zone in the DMZ - a solution is still pending (Either a FW needs to be opened between the zones or have an OTC collector in each zone)
- The Ansible playbook has the required yaml configuration required for the Observability agent to report metric back to Observability via the Collector.
- The Ansible command can be used to target VMs with either Linux and Windows Operating systems.

### Signal FX Installation command

Observability agent can be installed on a Virtual Machine or a group of Virtual Machines via a group defined in the inventory file with the following example command. The host value should contain host to be targeted.

```bash

sudo ansible-playbook -i odx-hosts -k install-Observability.yaml -u <user-id> --extra-vars "host=apvrs36238"

```

This command needs to be run from the Ansible Control Machine in the core targeting the VM to be monitored or a group of VMs.

### Observability JBoss Configuration for Java Apps Monitoring

- Ansible can be used to setup Observability to monitor JBoss
- Observability has a JBoss adapter (monitor) that is provided as a jar file called signalfx-tracing.jar
- signalfx-tracing.jar is copied to /var/lib/Observability-agent on the target machine where JBoss is running
- JBoss configuration is a standalone.conf (domain.conf) which is edited to load the Observability JBoss adapter signalfx-tracing.jar. Required environment variables are set in the same file.
  - export Observability_SERVICE_NAME=Observability-apvrd37877
  - JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -javaagent:/usr/lib/Observability-agent/Observability-tracing.jar"
