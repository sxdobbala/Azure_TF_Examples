---
title: Observability Built-in Dashboards
description: Discover the built-in dashboards and metrics provided by Splunk Observability out-of-the-box.
ms.assetid: e0ac94eb-99c4-4deb-9b37-cbe7f31b5d5e
ms.topic: learn
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "rwhitcom"
---

# Overview

Splunk Observability (formerly SignalFx) provides a significant number of pre-built dashboards that teams can use to view the health of their infrastructure. We describe a number of the more common dashboards below:

## Azure

### App Service

- Connections Per App
- Requests Per App
- Bytes Received
- Bytes Sent
- HTTP 2xx Responses
- HTTP 4xx Responses
- HTTP 5xx Responses
- CPU Time
- Average Response Time

### Event Hubs

- Incoming Requests
- Successful Requests
- Top Event Hubs by Server Errors
- Top Event Hubs by Quota Exceeded Errors
- Top Event Hubs by User Errors
- Total Network I/O
- Top Event Hubs by Quota Exceeded Errors
- Top Event Hubs by User Errors
- Total Network I/O
- Total Messages I/O
- Connections Opened
- Throttled Requests

### Event Hub Namepsace

- Incoming vs Successful Requests
- Network I/O
- Messages I/O
- Throttled Requests
- Quota Exceeded Errors
- Server Errors
- User Errors
- Connections Opened

### Functions

Singular Instance:

- Active Function Apps (Sum)
- Total Invocations (Sum)
- Invocations per Function App
- Active Function Apps by Region
- Bytes Received (By All Function Apps)
- Top Bytes Received by Function App
- Bytes Receive vs 24hr Change %
- Bytes Sent (By All Function Apps)
- Top Bytes Sent by Function App
- Bytes Sent vs 24 hr Change %
- HTTP 5xx Errors
- Top Average Memory Usage by Function App
- Top Average Compute Duration by Function App

Aggregate Instance:

- Invocations (Sum)
- Invocations Min/Max/Average
- Compute Duration
- Average Compute Duration
- Bytes Received
- Bytes Sent
- HTTP 5xx Count
- Memory Usage

### Kubernetes Service

- &#35; of Nodes
- &#35; of Pods
- &#35; of Pods NOT in Ready State
- Allocatable CPU Cores
- Available Memory
- Pods by State  (Green indicates a pod is READY.  Apply Namespace filter to see all pods in a specific Namespace)
- Pods by Phase (Describes the meaning of the phases.  For example:  Ready, Pending, Failed, etc.)
- &#35; of Pods By Phase
- &#35; of Pods NOT in Ready state per namespace
- &#35; of Pods in Pending Phase
- &#35; of Pods in Failed Phase
- &#35; of Pods in Unknown Phase

### Logic Apps

Singluar Instance:

- Billable Executions
- Runs Completed
- Actions Completed
- Runs Succeeded/Failed/Throttled
- Actions Succeeded/Failed/Throttled
- Triggers Succeeded/Failed/Throttled
- Run Latency (s)
- Action Latency (s)

Aggregate Instance:

- Runs Succeeded
- Successful Run Latency (s)
- Runs Failed
- Runs Throttled
- Total Billable Executions
- Runs Completed

### Redis Cache

Singular Instance:

- Cache Hit Rate
- Cache Hit Rate Trend
- Server Load
- CPU %
- &#35; Connections
- Memory Fragmentation Ratio
- Used RSS Memory
- Used Memory
- Total Keys
- Evicted Keys
- Expired Keys
- Total Commands Processed
- Get Commands
- Set Commands
- Cache Reads
- Cache Writes

Aggregate Instance:

- &#35; Caches
- Lowest Cache Hit Rates
- Server Load %
- &#35; Connections
- Used Memory per Cache
- Total Keys per Cache
- Total Keys

### SQL Database

Singular Instance:

- DTU Percentage
- DTU Percentage Trend
- Server CPU Percentage
- Server CPU Percentage Trend
- Data I/O Percentage
- Log I/O Percentage
- Database Size Percentage
- Successful Connections
- Failed Connections
- Connections Blocked by Firewall

Aggregate Instance:

- Number of Databases
- Top Databases by Total Storage
- Top Databases by DTU Consumption %
- Top Databases by Data I/O %
- Top Databases by Connections Failed
- SQL Server CPU %
- Number of Blocked Connections
- Top Databases by Log I/O %

### SQL Elastic Pools

Singular Instance:

- eDTU Used per Database in Elastic Pool vs eDTU Limit
- DTU Percentage Trend
- Data IO Percentage
- Log IO Percentage
- CPU Percentage
- Elastic Pool Storage Used %
- Elastic Pool Storage Used per Database

Aggregate Instance:

- Top Elastic Pools by DTU Consumption %
- Top Elastic Pools by eDTU Used
- Top Elastic Pools by Total Storage
- Top Elastic Pools by Storage %
- Top Elastic Pools by Workers Percentage
- Top Elastic Pools by Sessions Percentage

### Storage

Singular Instance:

- Ingress Traffic
- Egress Traffic
- Latency (ms) of Successful Requests per API Call
- End to End Latency (ms) of Successful Requests per API Call
- Average Latency (ms) of Successful Requests
- Average End to End Latency (ms) of Successful Requests
- Used Capacity (bytes)
- Availability by API Call
- Transactions per API Call
- Transaction Response Types

Aggregate Instance:

- Top Accounts by Used Capacity
- Lowest Available Storage Servers
- Top Transactions by Storage Account
- Successful End to End Requests Latency (ms) Distribution
- Successful Requests Latency (ms) Distribution
- Total Network Egress
- Total Network Ingress

### Virtual Machine

Singular Instance:

- CPU %
- CPU % Trend
- Disk I/O
- Disk I/O Trend
- Disk IOPs Trend
- Network Bytes In vs 24hr Change %
- Network Bytes Out vs 24hr Change %
- CPU Credits Remaining
- CPU Credits Consumed

Aggregate Instance:

- Active Virtual Machines
- Top Virtual Machines by CPU %
- Active Virtual Machines by Region (Count)
- Network Bytes In
- Top Virtual Machine by Bytes In
- Network Bytes In vs. 24h Change %
- Network Bytes Out
- Top Virtual Machines by Bytes Out
- Network Bytes Out vs. 24h Change %
- Disk I/O Bytes/Sec
- Disk Ops/Sec

### VM Scale Set

Singular Instance:

- CPU %
- CPU % Trend
- Disk I/O (Totals)
- Disk I/O (Individual VMs)
- Disk IOPs
- Network Bytes In vs 24hr Change %
- Network Bytes Out vs 24hr Change %
- CPU Credits Remaining
- CPU Credits Consumed

Aggregate Instance:

- Number of Scale Sets
- Top Scale Sets by CPU %
- Active Scale Sets by Region
- Total Network Bytes In
- Top Scale Sets by Bytes In
- Network Bytes In vs. 24h Change %
- Total Network Bytes Out
- Top Scale Sets by Bytes Out
- Network Bytes Out vs. 24h Change %
- Disk I/O Bytes/Min
- Disk Ops/Min

## Docker

### Container

- CPU %
- Memory Used
- Memory %
- Network In Bits/sec
- Netowrk Out Bits/sec
- Disk Read Bytes/sec
- Disk Write Bytes/sec

### Host

Singular Instance:

- Running Containers (Current Count)
- Host CPU %
- Containers CPU %
- Host Memory %
- Memory % by Container
- Network Input Bits/sec by Container
- Network Output Bits/sec by Container
- Disk Read Bytes/sec by Container
- Disk Write Bytes/sec by Container

Aggregate Instance:

- Containers (Current Count)
- Containers (Count over time)
- Containers by Host
- Top Containers by CPU %
- CPU %
- Memory Used %
- Total Disk Bytes/sec
- Total Network Bits/sec

## Infrastructure

### Infrastructure (A) Inc. On-Prem

- &#35; Active Hosts (Current Count)
- CPU %
- Total Disk I/O Ops
- Total Memory
- Top CPU %
- Total Disk Space
- Memory Used %
- Top Mem Page Swaps/sec
- Total Network I/O
- Total Network Errors

### Infrastructure

- CPU Used %
- Memory Used %
- Total Network bits/sec
- Disk Used %
- CPU %
- Load Average
- Memory
- Memory Paging
- Disk I/O
- Disk Free %
- Network I/O
- Network Errors

### Infrastructure Windows

- CPU Used %
- Memory Used %
- Total Network bits/sec
- Disk Used %
- CPU %
- CPU % per core
- Memory
- Memory Paging
- Disk I/O
- Disk Free %
- Network I/O
- Network Errors

## Kubernetes

### K8s Container (Singular)

- CPU (% Utilization per Container)
- Memory Limit
- Memory Usage
- Filesystem Usage

### K8s Containers (Aggregate)

- &#35; Active Containers
- Network In
- Network Out
- CPU % (Top 10 Containers in Cluster with Highest CPU Usage)
- Memory Usage
- Filesystem Usage
- CPU %
- Memory Usage
- Filesystem Usage

### K8s Cluster

- &#35; Clusters
- &#35; Nodes per Cluster
- &#35; Containers per Cluster
- CPU Capacity Used
- Memory Usage
- Memory Capacity Used
- Network Receive Errors
- Network Transmit Errors

### K8s Cluster Services

- APIServer Instances
- Top 5 APIServer Workloads in Queue
- APIServer Admission Controller Rate
- APIServer RPC Rate
- Controller Manager Instances
- Top 5 Controller Manager Workloads in Queue
- Controller Manager Work Queue Depth
- Controller Manager Work Queue Latency
- Scheduler Instances
- Total Scheduling Latency
- Total Scheduling Duration
- Scheduler RPC Rate
- Proxy Instances
- Proxy Rules Sync Rate
- Proxy Rules Sync Latency
- Proxy Request Error Rate
- Etcd Instances (count)
- Etcd Leader Changes Seen
- Etcd Failed Proposals Seen
- Etcd RPC Rate
- CoreDNS Instances
- DNS Requests by Query Type
- Core DNS Responses by rcode
- Total CoreDNS Cache Size

### K8s Nodes

- &#35; Nodes
- Top Nodes by Total Memory
- Total Memory (bytes)
- Top Nodes by # Pods
- Top 10 Nodes by CPU Capacity Usage %
- Top Nodes by Memory Capacity Used
- Top Nodes by Network Usage
- Network Throughput (bytes/sec)
- Network Errors by Interface

### K8s Operations

- Deployments Not at Spec
- Desired Pods by Deployment
- Available Pods by Deployment
- Container Restarts
- Container Restarts by Node
- Container Restarts by Pod
- Pods by Phase (Count)
- Pods by Phase (Graph Over Time)
- Daemon Sets by Stage
- Net Pods Desired by Replica Set
- Net Pods Desired by Replication Controller

### K8s Overview

- K8s Nodes (Description)
- K8s Pods (Description)
- CPU Capacity Used per Node Grouped by Cluster
- &#35; of Pods by Phase
- CPU Usage per Pod Grouped by Namespace
- Number of Pods per Node
- Total of Pods
- Memory Capacity Used per Node
- Memory Usage per Pod
- Desired Pods
- Desired Pods by Deployments
- CPU Capacity Used per Node
- CPU Usage per Pod
- Available Pods
- Available Pods by Deployments
- Network Usage Per Node
- Bytes In per Pod
- Bytes Out per Pod

### K8s Pod (Singular)

- &#35; Active Containers
- Network Throughput (bytes/sec)
- Network Errors/sec
- % Memory Used per Container
- Memory Used per Container (bytes)
- % CPU Limit Used per Container
- % CPU Used per Container

### K8s Pods (Aggregate)

- &#35; Active Pods
- Network Throughput (bytes/sec)
- Network Errors/sec
- &#35; of Pods by Phase
- Available Pods by Deployments
- Desired Pods by Deployments
- % Memory Used per Pod
- Top 10 Pods by Average Container Memory Usage
- Memory Usage per Pod
- CPU Usage per Pod
- % CPU Limit Used per Pod

## MySQL

Singular Instance:

- Selects/sec
- Inserts, Updates, Deletes /sec
- Threads by State
- MySQL Bytes/sec
- Top Commands/sec
- Table Locks/sec
- Query Cache Operations/sec, Size
- Query Cache Hit %
- Selects/sec 24h Change %
- Mutations/sec 24h Change %

Aggregate Instance:

- &#35; Nodes
- Total Threads by State
- Total MySQL Bytes/sec
- Total Selects/sec
- Selects/sec
- Total Inserts, Updates, Deletes /sec
- Mutations/sec
- Total Table Locks/sec
- Total Query Cache Operations/sec, Size
- Overall Query Cache Hit %
- Top Selects/sec
- Top Mutations/sec
- Top Commands/sec

## Splunk Observability

### Engagement

- Users
- Users - Info (Description)
- Teams
- Teams - Info
- Users - Trend
- Teams - Trend
- Charts
- Dashboards
- Dashboard Groups
- Charts - Trend
- Dashboards - Trend
- Dashboard Groups - Trend
- Charts - Info
- Dashboards - Info
- Dashboard Groups - Info
- Detectors
- Detectors - Info
- Muting Rules
- Muting Rules - Info
- Detectors - Trend
- Muting Rules Trend

### Smart Gateway Cluster

- Gateway Saturation
- TAPM (Traces Analyzed Per Minute)
- TAPM Graphed over Time
- CPU Utilization
- Retained TPM (Total)
- Retained TPM Graphed over Time
- Memory Utilization %
- APM Identities
- APM Identities Graphed over Time
- Network Utilization
- Analyzed Traces/sec
- Retained Traces
- Disk Utilization
- Processed Spans
- Retained Spans
- Cluster-Name | Smart Gateway Version
- Dropped Traces
- Dropped Spans

### Smart Gateway Internals

- Late Spans
- Selected
- Buffered Traces
- Buffered Spans
- Expired Buffered Traces
- Spans Aged Out
- Traces Aged Out
- Num Go Routines
- Expired Buffer Entries Transfer
- Alloc
- Channel Sizes
- Buffer Entries Transfer
- Cluster Size

## Windows

### IIS

Singular Instance:

- IIS Total Requests/sec
- IIS Current Connections
- IIS Total Bytes Transferred/min
- Bytes sent+received/sec
- Requests/sec
- IIS Connection Attempts/sec
- IIS Current Connections
- IIS Users/sec
- IIS Files Sent and Received/sec
- IIS Not Found Errors/sec

Aggregate Instance:

- IIS Total Requests/sec
- IIS Current Connections
- IIS Connection Attempts/sec
- IIS Requests/sec  (Broken down by site)
- IIS Current Connections  (Broken down by site)
- Bytes sent + received/sec
- Requests/sec
- IIS Current Connections
- IIS Files Sent and Received/sec
- IIS Users/sec
- IIS Not Fount Errors/sec

## µAPM

### Endpoint

- Request Rate  (Current Request/s)
- Request Rate  (Graphed over Time)
- Request Latency  (90th Percentile Response Time)
- Request Latency Distribution  (Distribution of Endpoint Response Time with 50th, 90th, and 99th percentiles)
- Error Rate  (Current %)
- Error Rate  (Graphed over Time)
- Host Metrics
  - Host CPU Usage
  - Host Memory Usage
  - Host Disk Usage
  - Host Network Usage
  - Host Saturation (Max of CPU, Memory, and Disk Utilization)
- Kubernetes Pod Metrics
  - Pod CPU Usage
  - Pod Memory Usage
  - Pod Disk Usage
  - Pod Network Utilization (bytes/sec)

### Service

- Request Rate (Current Requests/s)
- Request Rate (Graphed over Time)
- Request Latency (90th percentile)
- Request Latency Distribution (Graphed over Time shoing 50th, 90th, & 99th percentile)
- Error Rate (Current % of Requests)
- Error Rate (Graphed over Time)
- Request Rate by Endpoint
- Request Latency by Endpoint (90th percentile)
- Error Rate by Endpoint
- Top Endpoints by Request Rate
- Top Endpoints by Error Rate
- Host Metrics
  - Host CPU Usage
  - Host Memory Usage
  - Host Disk Usage
  - Host Network Usage
  - Host Saturation (Max of CPU, Memory, and Disk Utilization)
- Kubernetes Pod Metrics  (Only populates if your service is running in Kubernetes and SignalFX Smart Agent is configured to monitor your Kubernetes Environment)
  - Pod CPU Usage
  - Pod Memory Usage
  - Pod Disk Usage
  - Pod Network Utilization (bytes/sec)

## µAPM PG (Previous Generation)

### Endpoint

- Service Map
- Request Rate  (Current)
- Request Rate  (Graphed over Time)
- Request Latency  (99th Percentile, Current)
- Request Latency Distribution (50th, 90th, and 99th Percentiles Graphed over Time)
- Error Rate  (Current)
- Error Rate  (Graphed over Time)
- Host Metrics
  - Host CPU
  - Host Memory Usage
  - Host Disk Usage
  - Host Network Utilization
  - Hosts Saturation  (Max of CPU, Memory, and Disk Utilization)
- Kubernetes Pod Metrics
  - Pod CPU Usage
  - Pod Memory Usage
  - Pod Disk Usage
  - Pod Network Utilization (bytes/sec)

### Service

- Service Map
- Request Rate  (Current)
- Request Rate  (Graphed over Time)
- Request Latency  (99th Percentile, Current)
- Request Latency Distribution (50th, 90th, and 99th Percentiles Graphed over Time)
- Error Rate  (Current)
- Error Rate  (Graphed over Time)
- Request Rate by Endpoint
- Request Latency (99th Percentile) by Endpoint
- Error Rate by Endpoint
- Top Endpoints
- Most Erroneous Endpoints
- Host Metrics
  - Host CPU Usage
  - Host Memory Usage
  - Host Disk Usage
  - Host Network Usage
  - Host Saturation
- Host Kubernetes Pod Metrics
  - Pod CPU Usage
  - Pod Memory Usage
  - Pod Disk Usage
  - Pod Network Utilization  (bytes/sec)