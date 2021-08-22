---
title: How to add an Observability Detector to a Product
description: Learn how to add issue detectors to a product.
ms.assetid: 8869d2af-b422-4e0e-87d9-c6ad34cd3460
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "mnegron"
---

# Overview

## What is a detector?

A detector evaluates metrics over a period of time against a specified condition, and optionally for a given duration. When a condition has been met, detectors generate events of a specified severity. Conceptually, you can think of a detector as a chart that can trigger alerts when a signal's value crosses specified thresholds defined in alert rules.

Specifically, detectors define the following:

- A trigger condition, specified in a SignalFlow Profile
- A severity to set when the trigger condition occurs
- Notifications sent to Splunk On-Call (formerly VictorOps)
- The content included in notifications

## What is an event?

An event is an incident that occurs irregularly that can be represented in SignalFx outside the flow of streaming metrics. Events can be represented as a structured log line to SignalFx (e.g., the values could be represented as any combination of key/value pairs). ). Events are secondary to metrics in SignalFx and are meant to provide context for the metric data. Events can be displayed on charts and viewed in the Events sidebar.

Each occurrence of an event is an instance of an event type. An event type is a reusable event name that groups together events that you want to show as a stream or series, such as code pushes. By reusing event types, you have the ability to add an event type to a chart, then view all events that occurred for that event type.

There are several ways events are created in SignalFx.

- Events are created whenever a detector triggers an alert. The detector provides information about the alert. When an alert clears, is manually resolved, or stopped (due to the detector being edited or deleted) a second event is created.
- A SessionLog event is created when a user logs into or out of your organization. The SessionLog event notes the action (either "session created" or "session deleted") and the ID of the user who created the session.
- A custom event is created when you capture and send an event to the SignalFx service via an integration or the SignalFx API. For example, you may send a "code push" event in to SignalFx each time your development team deploys new code, so that you can correlate it with the resource consumption profiles of your infrastructure before and after the event. (All "code push" events are instances of the "code push" event type.)
- You can also [create a custom event](https://docs.signalfx.com/en/latest/detect-alert/events-intro.html#custom-event) in SignalFx and manually note when that event occurs.

## What is an alert?

In SignalFx, events generated when detector conditions are met are referred to as alerts. An alert is triggered when the conditions for a detector rule are met. For example, a detector monitoring the number of requests served by an application may have a rule that produces an alert if the number is below a static threshold, e.g. 20 requests per minute, and/or above a calculated one, e.g. the mean + 3 standard deviations of the number of requests per minute over the past hour.

## Grouping a Detector Around Resources

A detector is more in tune with a Runbook, or Playbook, which would be used to resolve an incident, than a resource itself. For example, If you have 5 webservers servin up the same application and 2 web servers running an API, the playbook on those would be different to resolve issues.

## Adding a Detector Through Terraform

- [Here](https://github.optum.com/Dojo360/signalfx-detectors/tree/master/profiles) are the SignalFx detectors currently supported. These are used alongside the Azure Dojo360 profiles to deploy resources in your Azure environment
- In [this](https://github.optum.com/Dojo360/signalfx-detectors/tree/master/profiles/average-response-time#usage) example profile we use the SignalFx terraform provider and create a SignalFx detector. This detector is pre-configured to measure the average response time for an App Services running in your environment.
- In order to add the average response time for an App Services detector we must add the code in the example to the terraform configuration
- For example, we can use the [Profile: Single Region Scale Out App Service Container with BYODB](https://github.optum.com/Dojo360/azure-web-stack/tree/master/profiles/single-region-scale-out-app-svc-container-byodb#profile-single-region-scale-out-app-service-container-with-byodb-) to build out an AppStack which includes the App Service we'll want to monitor through SignalFx.
- Using the resource deployment code along with the SignalFx detector will allow you to set a detector on each of the resources you define.

## Sending an alert to Splunk On-Call

While there are multiple recipient types (email, Splunk On-Call, etc.) the preferred recipient type is Splunk On-Call. For this integration you will need a Splunk On-Call routing key, and have a team configured with an escalation policy in the VictorOps application. Please reach out to the DevOps Enablement team [Provider_DE_SystemOperations@ds.uhc.com](mailto:Provider_DE_SystemOperations@ds.uhc.com) for further information on setting this up.
