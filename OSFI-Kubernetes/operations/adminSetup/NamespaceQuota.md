

# Namespace Quota

When several users or teams share a cluster with a fixed number of nodes, there is a concern that one team could use more than its fair share of resources. Resource quotas are a tool for administrators to address this concern

A resource quota, defined by a ResourceQuota object, provides constraints that limit aggregate resource consumption per namespace. It can limit the quantity of objects that can be created in a namespace by type, as well as the total amount of compute resources that may be consumed by resources in that project.

For OSFI Kubernetes platform, we will be defining quotas for total amount of compute resources that can be consumed by a project/namespace in terms of Memory and CPU

# Creating the Resource Quota

Run this after the namespace is provisioned :

Create quota.yaml:
 
 ```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: mem-cpu-demo
spec:
  hard:
  hard:
    cpu: "156"
    memory: 720Gi
    requests.cpu: "156"
    requests.memory: 720Gi
    limits.cpu: "156"
    limits.memory: 720Gi
  ```
 Now run:
 
 `kubectl create -f quota.yaml -n <NAMESPACE>`
 
 # Create a LimitRange
 
 Once the namespace quota is enabled, every Container must have a memory request, memory limit, cpu request, and cpu limit. 
 
 LimitRange API object enables: 
 * default values for Memory/CPU request and limit 
 * min/max ranges for memory/CPU request and limits
 

Create limit.yaml:
  
 ```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: mylimits
spec:
  limits:
  - max:
      cpu: "32"
      memory: 128Gi
    min:
      cpu: 200m
      memory: 6Mi
    type: Pod
  - default:
      cpu: 500m
      memory: 500Mi
    defaultRequest:
      cpu: 300m
      memory: 300Mi
    max:
      cpu: "32"
      memory: 128Gi
    min:
      cpu: 100m
      memory: 3Mi
    type: Container
```
 Now run:
 
`kubectl create -f limit.yaml -n <NAMESPACE>`
