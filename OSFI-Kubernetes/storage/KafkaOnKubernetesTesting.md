# Kafka 

Kafka on Kubernetes is run as a stateful set. A prerequisite of running kafka is that persistent storage is needed and a zookeeper cluster needs to be installed.

# Test Cases
* GlusterFS Dynamic
* MapR Static
* MapR Dynamic
* iSCISI Static -- to do
* iSCISI Dynamic -- to do

For each of the above we need the user to be:
* Root
* Non Root

# MapR 
 In all of the below we will assume that all of the steps to configure MapR on Kubernetes have already been followed. These can be found (here) -- To do
## Creating a mapr ticket secret in kubernetes
In order to mount a dynamic or static volume a mapr ticket needs to be added to a secret in kubernetes. This mapr ticket needs to have access to the data in the volume you want to mount. To do follow the template below and replace the ticket contents with the base 64 encoded version of your ticket. To base 64 encode your mapr ticket run 

`cat <ticket-file> | base64 | tr -d \\n`

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: dlpocid-ticket-secret-prod
  namespace: mapr-examples
type: Opaque
data:
  CONTAINER_TICKET: <BASE64 encoded ticket>
```

## Setting up Static Volumes
Before running the stateful sets for zookeeper or kafka we need to create static volumes. To do so have a MapR Cluster admin create the desired number of volumes and the kubernetes admins create persistent volume claims for these volumes. Sample persistent volume claims can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/storage/static/mapr) in pv-<zk/kafka>.yaml files.

## Setting up Dynamic Volumes
In order to set up a dynamic volume against mapr a storage class needs to be set up within your namespace. It should look like the below: 

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
   name: secure-maprfs
   namespace: mapr-examples
provisioner: mapr.com/maprfs
parameters:
    restServers: "IP OF REST SERVER"
    cldbHosts: "CLDB1 CLDB2 CLDB 3"
    cluster: "datalake-mapr6"
    securityType: "secure"
    ticketSecretName: "ses-beta-ticket-secret"
    ticketSecretNamespace: "mapr-examples"
    maprSecretName: "mapr-provisioner-secrets"
    maprSecretNamespace: "mapr-examples"
    namePrefix: "kb"
    mountPrefix: "/kb"
    readOnly: "true"
    reclaimPolicy: "Retain"
    advisoryquota: "100M"
    readonly: "1"
```

The cldb hosts and rest server will need to be pointing at the correct user. A mapr ticket secret in kubernetes will need to be generated which is explaiend in the section on creating a static volume. Additionally a secret with the username and password of a user that has access to mount volumes into mapr needs to be provided. To get this access talk to the MapR admins. The ticket will be created with the below yaml:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mapr-provisioner-secrets
  namespace: mapr-examples
type: Opaque
data:
  MAPR_CLUSTER_USER: <Base64 encoded username>
  MAPR_CLUSTER_PASSWORD: <Base64 encoded password> 
```
To base64 encode your username and password run:

`echo <username> | base64`

`echo <password> | base64`

# Static/Root Kafka

Running as root requires no changes to the docker images for either zookeeper or kafka. 
## Zookeeper
The full zkDeploy.yaml file can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/static/mapr/root/zookeeper/zkDeploy.yaml). All that needs to be changed is the volume label found at line 153.

Now we run:

`kubectl create -f zkDeploy.yaml`

This will create a headless service, a config map, a pod disruption budget, and a statefulset for Zookeeper with 3 pods. The pod disruption budget ensures that at least 2 pods are running at all times.


## Kafka

### MapR
The full kafka.yaml file can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/static/mapr/root/kafka/kafka.yaml) All that needs to be updated is the volume label found on line 185 and the zookeeper config for kafka by updating line 76 and replacing mapr-examples with your namespace name. It should look like:

`          --override zookeeper.connect=zk-0.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-1.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-2.zk-svc.<NAMESPACE>.svc.cluster.local:2181 \`
. 

Now we run:

`kubectl create -f kafka.yaml`

This will create a headless service, a config map, a pod disruption budget, and a statefulset for kafka with 3 pods. The pod disruption budget ensures that at least 2 pods are running at all times.


# Static/Non Root

In order to run the containers as non root users the dockerfiles for both kafka and zookeeper needed to be modified to add the volume owner user to the image. 

## Zookeeper

The Dockerfile and scripts for zookeeper can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/storage/nonroot_docker_images/zookeeper). In the Dockerfile you need to edit lines 3 and 4 to match the volume owners user id and group, lines 51 and 54 need the gid of the group updated, and lines 52 and 53 need the uid of the user updated.

Now we need to edit the zkGenConfig.sh script by updating the username and group name lines 16 and 17.

We can now build the image with docker build and then push it to our docker.optum.com trusted registry repository.

`docker build -t docker.optum.com/<repo name>/<image_name>:<tag> <path/to/dockerfile>`

`docker login docker.optum.com`

`docker push docker.optum.com/<repo name>/<image_name>:<tag>`

### MapR
Now that the image is in docker we need to update the zkDeploy.yaml found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/static/mapr/root/zookeeper/zkDeploy.yaml) with the new image name(line 69). Additionally we need to update the volume match label found at the bottom of the yaml with the label provided by the kuberentes admins(line 156).  Finally we need to update the security context(lines 64 and 65) in the yaml so that the runAsUser matches the uid we added in the image and the runAsGroup matches the group we added in the image. 

Now we can launch the stateful set with:

`kubectl create -f zkDeploy.yaml`

## Kafka
The Dockerfile and log4j.properties for kafka can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/storage/nonroot_docker_images/kafka). The Dockerfile needs the user name, group name, user id, and group id added to the image. To do so change the username and group name(lines 3 and 4), the group id(lines 27 and 30), and the user id(lines 28 and 29).

We can now build the image with docker build and then push it to our docker.optum.com trusted registry repository.

`docker build -t docker.optum.com/<repo name>/<image_name>:<tag> <path/to/dockerfile>`

`docker login docker.optum.com`

`docker push docker.optum.com/<repo name>/<image_name>:<tag>`

### MapR
Now that the image is in docker we need to update the kafka.yaml found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/static/mapr/nonroot/kafka/kafka.yaml) with the new image name(line 63). Additionally we need to update the volume match label found at the bottom of the yaml with the label provided by the kuberentes admins(line 188).  Next we need to update the security context(lines 176 and 177) in the yaml so that the runAsUser matches the uid we added in the image and the runAsGroup matches the group we added in the image. Finally we need to update the zookeeper config for kafka by updating line 76 and replacing mapr-examples with your namespace name. It should look like:

`          --override zookeeper.connect=zk-0.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-1.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-2.zk-svc.<NAMESPACE>.svc.cluster.local:2181 \`

Now we can launch the stateful set with:

`kubectl create -f kafka.yaml`

# Dyanmic/Root
## Zookeeper

### MapR
To run a zookeeper stateful set as root find the zkDeploy.yaml file [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/mapr/zookeeper/zkDeploy.yaml). 

`kubectl create -f zkDeploy.yaml`

### GlusterFS

## Kafka
### MapR
To run a kafka stateful set as root with a dynamic volume find the kafka.yaml file [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/mapr/kafka/kafka.yaml). We simply need to update the zookeeper configs on line 76 and replace mapr-examples with your namespace name. It should look like:

`          --override zookeeper.connect=zk-0.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-1.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-2.zk-svc.<NAMESPACE>.svc.cluster.local:2181 \`

Now we can launch the stateful set with:

`kubectl create -f kafka.yaml`

# Dynamic/NonRoot

## Zookeeper

## MapR
Nonroot dynamic provisioning currently does not work with mapr as the volume is getting mounted as root:root. A ticket has been opened with MapR to investigate further.

## Gluster FS

Dynamic GlusterFS storage uses a volume template that points at the gluster fs storage class. The storage class will dynamically provision a persistent volume and the volume template will dynamically create the persistent volume claim. The zookeeper yaml for starting zookeeper with glusterfs storage as a non root user can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/glusterfs/nonroot/zookeeper/zkDeploy.yaml)

To launch the stateful set run:

`kubectl create -f zkDeploy.yaml`

## Kafka

## MapR
Nonroot dynamic provisioning currently does not work with mapr as the volume is getting mounted as root:root. A ticket has been opened with MapR to investigate further.

## Gluster FS


Dynamic GlusterFS storage uses a volume template that points at the gluster fs storage class. The storage class will dynamically provision a persistent volume and the volume template will dynamically create the persistent volume claim. The kafka yaml for starting up a three node kafka cluster with glusterfs storage as a non root user can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/glusterfs/nonroot/kafka/kafka.yaml)

The yaml will need to be udpated to reflect the namespace where your zookeeper is running. To do so edit line 76 so it looks like the below with \<NAMESPACE\> replaced by your namespace:


`          --override zookeeper.connect=zk-0.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-1.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-2.zk-svc.<NAMESPACE>.svc.cluster.local:2181 \`

Now run a:
`kubectl create -f kafka.yaml`

# Resources

https://github.com/kubernetes/contrib/tree/master/statefulsets/kafka

https://github.com/kubernetes/contrib/tree/master/statefulsets/zookeeper
