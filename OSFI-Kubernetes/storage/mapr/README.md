# Introduction
MapR provides KDF (Kubernetes Data Fabric) Flex volume plug-in to mount existing BDPaaS volume in an container. Existing volume means only the BDPaaS tenant/project team can statically mount their volume(s) in a pod(container) and access their data in secure manner by using thier mapr ticket. Even though MapR do support <b>dynamic provisioning</b> of a volume, there is no plan to support <b>dynamic provisioning</b> feature in OSFI-Kubernetes cluster.

## MapR Static Provisioning in Nutshell
* MapR Flex volume plug-in lets Kubernetes Pod(container) to mount remote MapRFS volumes as a POSIX filesystem on worker nodes(local)
* Single POSIX client for Worker node for each container launched
* The remote MapR Cluster details, mount directory and mapr ticket name (secret) are part of Persistence Volume Object.

<p align="center">
  <img src="/images/maprfs-static-provisioning.png" width="700"/>
</p>


## Accessing BDPaaS Data
Existing MapR volume data can be accessed from Kubernetes pod(container) through MapR Flex volume plug-in. Follow below instructions to access MapR Volume from container


#### 1. Request Persistence Volume (PV) 

Please Contact admin team with the below information to create PV

* BDPaaS Env : prod or non-prod
* BDPaaS Volume name: full path of the volume (you can also specify a subfolder in side the volume)
* Amount of Storage :  Amount of storage required in the voulme 
* K8s Namespace: Tenant Namespace created in Kubernetes cluster
* K8s Env: Kubernetes Environment
* Secret Name: Secret Name used during the mount of volume.

#### 1.1 Create Persistence Volume
* Admin will create Persistence Volume as per tenant request
* The addmin will as label as volume-type: `pvc-mapr-<namespace>-<user>`

#### 2. Create secret
Next step to create a secret with name as requested in the step # 1

#### 2.1 Get BDPaaS ticket
* Login to BDPaaS EDGE node 
* Create the ticket with 'maprlogin password' command, this will create user ticket under /tmp/maprticket_<uid>

#### Note: The user ticket is valid for a day only. BDPaaS Tenant owner can use their long running service account ticket.

Example:

```
cat /tmp/maprticket_<uid>
datalake_prod oBvoD9KCktrwU4C0txRzTiaE84Xb+b+OJTEIP2JuB8xwqpxCd4GX.....................................................

```
#### 2.1 Convert the MapR ticket  to base64 encoded
* Copy the ticket whole ticket content, do not leave cluster name when you copy the content from MapR ticket file.
* Execute below command to convert the ticket content to base64 
```
echo -n "datalake_prod <base64-encoded ticket-value>" | base64

```
* Copy the converted base64 content into the Secret yaml file.
Refer: https://maprdocs.mapr.com/home/PersistentStorage/kdf_converting_a_string.html#concept_ogr_sxc_qcb

#### 2.2 Create a file `<user>-maprtkt-secret.yaml` (You can use any name. This is just an example)

```
apiVersion: v1
kind: Secret
metadata:
  name: <user>-maprtkt-secret
  namespace: <namespace>
type: Opaque
data:
  CONTAINER_TICKET: <base64 ticket created above - PASTE HERE> 
```
#### 2.3 Ready to create Secret
* Login to Kubernetes edge node and execute below command to create the secret in your namespace
* The user must be in tenant admin role to create secret

`kubectl create -f <user>-maprtkt-secret.yaml -n <NAMESPACE> `

#### 3 Create Persistence Volume Claim (PVC)
In this step we create pvc to claim the created Persistence Volume
#### 3.1 Create pvc file
* Create a file `pvc-mapr-<user>.yaml`

```

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pvc-mapr-<user>
  namespace: <NAMESPACE>
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 5Gi
  selector:
   matchLabels:
         volume-type: pvc-mapr-<namespace>-<user>
         
```

#### 3.2 Ready to create PVC
* Login to Kubernetes edge node and execute below command to create the pvc in your namespace
* The user must be in tenant admin role to create secret 
`kubectl create -f pvc-mapr-<user>.yaml -n <NAMESPACE>`

### 4 Ready to Access existing BDPaaS Volume
In this example will tie PVC and Pod to access the BDPaaS Volume

#### 4.1 Example 
In this example will use busybox image to mount BDPaaS Volume through PVC

* Create a busybox pod with the PVC created in the above step 
* Add UID and GID in the securityContext with which the pod needs to run

#### Note: make sure to use same UID and GID as in MapR ticket otherwise you will see permission denied when you write the file

* Create busybox-pod-mapr.yaml as described below

```
apiVersion: v1
kind: Pod
metadata:
  name: pod-mapr-static-example
  namespace: <NAMESPACE>
spec:
  securityContext:
    runAsUser: <uid>
    fsGroup: <gid>
  containers:
  - name: busybox-1
    image: busybox
    args:
    - sleep
    - "1000000"
    resources:
      requests:
        memory: "1Gi"
        cpu: "500m"
    volumeMounts:
    - mountPath: /mapr
      name: maprvolume
  volumes:
    - name: maprvolume
      persistentVolumeClaim:
        claimName: pvc-mapr-<user>

```

#### 4.2 Ready to Create Pod
* Login to Kubernetes edge node and execute below command to create the pod in your namespace

`kubectl create -f busybox-pod-mapr.yaml -n <NAMESPACE> `

