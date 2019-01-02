# Static PV connecting to BDPaaS


## Create PV

Contact dcos_admin <dcos_admin_DL@ds.uhc.com> admin team with the below information to create PV and follow the below steps

* BDPaaS Env : prod or non-prod

* BDPaaS Volume name: full path of the volume (you can also specify a subfolder in side the volume)

* Amount of Storage :  Amount of storage required in the voulme 

* K8S Name space : 


## Create secret

On the BDPaaS EDGE node create the ticket with 'maprlogin password' command, this will create user ticket under /tmp/maprticket_<uid>

Ex:

```
cat /tmp/maprticket_<uid>
datalake_prod oBvoD9KCktrwU4C0txRzTiaE84Xb+b+OJTEIP2JuB8xwqpxCd4GX.....................................................

```
convert the above contecnt into base64

```
echo -n "datalake_prod <base64-encoded ticket-value>" | base64

```
You must convert both the cluster name and string into base64 representation and then insert the result into the Secret.

Refer: https://maprdocs.mapr.com/home/PersistentStorage/kdf_converting_a_string.html#concept_ogr_sxc_qcb


Create a file `<user>-maprtkt-secret.yaml`

```
apiVersion: v1
kind: Secret
metadata:
  name: <user>-maprtkt-secret
  namespace: <namespace>
type: Opaque
data:
  CONTAINER_TICKET: <base64 ticket created above>
```

`kubectl create -f <user>-maprtkt-secret.yaml -n <NAMESPACE> `


## Create PVC

Create a file `pvc-mapr-<user>.yaml`

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
         volume-type: pv-mapr-<user>
         
```

`kubectl create -f pvc-mapr-<user>.yaml -n <NAMESPACE>`

## Use the PVC in any POD ( Example Busy box)

Create a busy box pod with the PVC created in the above step and the uid and gid with which the pod needs to needs to run.

create busybox-pod-mapr.yaml

```
apiVersion: v1
kind: Pod
metadata:
  name: pod-mapr-static-example
  namespace: <NAMESPACE>
spec:
  securityContext:
    runAsUser: <uid?
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

`kubectl create -f busybox-pod-mapr.yaml -n <NAMESPACE> `

