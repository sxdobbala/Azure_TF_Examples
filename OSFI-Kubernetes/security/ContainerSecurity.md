# Container Security

Within the OSFI Kubernetes platform we have several security policies in place to prohibit containers from breaking out of their namespace and container and interfering with other users. The first of these is measures is called a pod security policy within Kubernetes. Pod Security Policies are an object within kubernetes that control what capabilities a pod has. This policy has been predefined for all users of the cluster and can only be modified with a [risk review](https://egrc.uhg.com/GenericContent/Record.aspx?id=0&moduleId=494).

## Default Pod Security Policy

### Users, Groups, and Priveleged
The default pod security policy prohibits users to run as root within a pod and additionally prohibits priveleged mode within containers. Additionally users can't change the group that there users belongs to so that it is a member of the root group. This is to prevent users from breaking out of the context of the container as well as to prohibit them from accessing information on the host machine itself.

In order to abide by these policies users simply have to run a pod that includes securityContexts as below. Additionally the user they run as has to exist within the docker image that they inted to launch  otherwise unexpected behaviour will occur.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: busybox 
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

### Volumes
In order to prevent accessing unauthorized data a select set of volume types have been defined for user by end users. That list is outlined below

* configMap
* secret
* downwardAPI
* projected
* emptyDir 
* persistentVolumeClaim
* glusterfs
* iscsi
* flexVolume
  * mapr.com/maprfs


Please note that the only allowed flex volume is mapr.com/maprfs. Additonally it is important to note that emptyDir should only be user for scrap space. The directory will be lost the moment the container is killed, restarted, or simply gets pushed to another node.

### Host Network, Host Ports, Host PID, and Host IPC

All containers must run in bridge networking mode access to the host network is prohibited. This means that within a container you can not consume a host port. Services should be used to expose a container on the host network. Finally the Host PID namespace and Host IPC (inter process communication) namespace are prohibited. This means that you can not contact the host PID's and communicate with processes running directly on the host.




