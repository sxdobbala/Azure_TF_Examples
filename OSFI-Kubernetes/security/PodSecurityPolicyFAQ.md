# Pod Security Frequently Asked Questions
If you have not read the [container security policy](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/security/ContainerSecurity.md#volumes) for OSFI security please do so. Below are some frequently asked questions regarding errors within kubernetes.

## What does it mean when I see an error related to running a contianer as root?
**Error: container has runAsNonRoot and image will run as root**

The error above indicates that a pod was trying to run as a root user. Assuming the image being used can run as a nonroot user you simply need to add a security context to your pod spec.

```yaml
spec:
  securityContext: 
    runAsUser: 1000
    fsGroup: 1000
  containers:
  - name: mycontainer
    image: busybox 
    imagePullPolicy: Always
    args:
    - sleep
    - "100000"
    resources:
      requests:
        memory: "2Gi"
        cpu: "500m"
```

The above is a yaml snippet that includes a security context. It indicates that the pod will run as a user with uid 1000 and gid 1000. If the pod fails because the image used needs root then attempt to modify the image being used to add in a non root user that has the correct permissions. If this fails then submit a [risk review](https://egrc.uhg.com/GenericContent/Record.aspx?id=0&moduleId=494).

## What does it mean when I see an error related to running a container in privileged  mode?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [spec.containers[0].securityContext.privileged: Invalid value: true: Privileged containers are not allowed]**

The error above indicates that you attempted to start a pod that had priveleged access. By default priveleged pods are not allowed. To resolve this change priveleged to false. If the pod fails because the image used needs priveleged access. First attempt to modify the image so that this is not needed. If this fails then submit a [risk review](https://egrc.uhg.com/GenericContent/Record.aspx?id=0&moduleId=494).

## What does it mean when I see an error related to volumes?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [spec.volumes[0]: Invalid value: "hostPath": hostPath volumes are not allowed to be used]**

Within OSFI Kubernetes only a select set of Volume types are prohibited. That list can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/security/ContainerSecurity.md#volumes). To resolve the issue please remove the prohibited volume type.


## What does it mean when I see an error related to host network?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [spec.securityContext.hostNetwork: Invalid value: true: Host network is not allowed to be used spec.containers[0].securityContext.hostNetwork: Invalid value: true: Host network is not allowed to be used]**

Within OSFI Kubernetes pods are not prohibited to be run in Host Networking mode. This is to prevent issues with port contention. Remove the line regarding hostNetwork or set it to false to resolve the issue.

## What does it mean when I see an error related to host IPC?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [spec.securityContext.hostIPC: Invalid value: true: Host IPC is not allowed to be used spec.containers[0].securityContext.hostIPC: Invalid value: true: Host IPC is not allowed to be used]**

Within OSFI Kubernetes pods are not prohibited to be run in the Host Inner Process Communication namespace. This is to prevent containers from contacting processes running on the host directly. Remove the line regarding hostIPC or set it to false to resolve the issue.

## What does it mean when I see an error related to host PID?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [spec.securityContext.hostPID: Invalid value: true: Host PID is not allowed to be used spec.containers[0].securityContext.hostPID: Invalid value: true: Host PID is not allowed to be used]**

Within OSFI Kuberentes pods are prohibited from running in the Host PID namespace. This is to prevent pods from being terminated or terminating other processes running on the host. Remove the line regarding hostPID or set it to false to resolve the issue.

## What does it mean when I see an error related to capabilites?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [capabilities.add: Invalid value: "IPC_LOCK": capability may not be added]**

By default within OSFI Kubernetes Pods are forbidden from running with elevated capabilities. In order to resolve this remove the capability from the yaml. In the case where the image can not be modified to run without the given capability then a [risk review](https://egrc.uhg.com/GenericContent/Record.aspx?id=0&moduleId=494) needs to be submitted.

## What does it mean when I see an error related to fsGroup or Supplemental Group being 0?
**Error from server (Forbidden): error when creating "nonrootBusy.yaml": pods "busybox" is forbidden: unable to validate against any pod security policy: [fsGroup: Invalid value: []int64{0}: 0 is not an allowed group]**

Running as a root user or member of the root group is forbidden by default within kubernetes. Select a gid above 0 for fsGroup and/or SupplementalGroups and restart the pod. If root is needed and the image can not be modified to run without it then a [risk review](https://egrc.uhg.com/GenericContent/Record.aspx?id=0&moduleId=494) needs to be submitted.


## Why can't I use a port 80 or 443 within my container?

Ports under 1024 require root to priveleges to use. Simply change the container port to a port above 1024. Once this is done you can create a [service](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/services/ExternalServiceExposure.md) to expose the container to the outside world if desired.
