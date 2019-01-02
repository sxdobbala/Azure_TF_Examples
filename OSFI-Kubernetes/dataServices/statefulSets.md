# Stateful Sets
Stateful Sets are objects within kubernetes that enables users to run data services within the cluster such as kafka, elasticsearch, and cassandra. The stateful sets require that a headless service and a pod disruption budget be configured prior to creation.

Stateful sets utilize several other concepts such as Pod Affinity, Pod Anti Affinity, and volume claim templates. These concepts as well as those mentioned above are outlined below.

## Headless Service
 The headless service will control the network that assists in the interaction of the components of the data service. (i.e. brokers in kafka). A service being headless means that cluster IP is set to none so that kube proxy does not attempt to load balance the traffic. What this effectively does is makes it so that each pod can be reached via dns independendently. In other words instead of reaching a service using dns via <service_name>.<namespace>.svc.cluster.local you can now reach it with <pod_name>.<service_name>.<namespace>.svc.cluster.local.
 
 
## Pod Disruption Budget
A Pod disruption budget is used to guarantee that at any given point in time that X number of nodes are running. In kubernetes it is expected that pods can move around or be evicted at any given point. The pod disruption budget helps control this behaviour.

For example if we have a 3 node kafka cluster it would be devastating for all 3 or even 2 of the 3 nodes to be moved at the same time. In order to solve this we would create a pod disruption budget and specify that a minimum of 2 nodes in the group need to be running at all times. This would mean that if node 3 were to go down and begin moving to a new node the cluster would not evict node 1 or node 2 until node 3 was back online.

## Pod Affinity

Pod Affinity creates a rule that favors placing a pod on a node when certain conditions are met. For example if you have application a and application b that both work closely together it could be beneficial to have them both running on the same node for heightened performance. To achieve this you could create a pod affinity that specifies that kubernetes should attempt to place application b on a node where application a is already running whenever possible.

## Pod Anti Affinity

Pod Anti Affinity creates a rule that disfavours placing a pod on a node when certain conditions are met. For example if you have a 3 node kafka cluster it probably would be bad if all three 3 kafka pods got placed on the same physical node. To prevent this from happening a pod anti affinity rule could be created that specifies that if one kafka pod is already running on a physical node that kuberenetes attempts to not place another instance of kafka onto the same physical node.

## Volume Claim Templates

A volume claim template is used to dynamically create persistent volume claims. This can be very useful as it lets you easily scale up your applications. In most instances you would want your template pointing at a storage class. The storage class then in turn would dynamically create a persistent volume. The architecture behind this looks like the below:

<p align="center">
  <img src="../images/statefulSetDynamic.PNG" width="700"/>
</p>


## Storage Options Available

Currently the OSFI team is supporting three storage options for use depnding on the stateful set. 
 * GlusterFS
 * ISCSI
 
### GlusterFS 
GlusterFS is an open source distributed file system. It can be access by using the glusterfs-storage storage class to dynamically provision volumes. This has been tested and confirmed to work with Kafka.


#### Running Kafka on Gluster

In order to run kafka on the cluster you first need to start up an instance of zookeeper for your kafka cluster to use. 
An example zookeeper stateful set yaml that dynamically provisions glusterfs volumes can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/glusterfs/nonroot/zkDeploy.yaml).

Please note that you will need to update the yaml to specify your image and security context. The image provided is created from the contents of this [directory](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/storage/nonroot_docker_images/zookeeper). To use this dockerfile you simply update the zk_user and zk_group in the dockerfile(lines 3 and 4) as well as the zkGenConf.sh(lines 16 and 17) with your user and group and then subsequently edit lines 51-54 in the Dockerfile so that they match your uid and gid.(this can be obtained by running id from a unix server). Now you simply build the image and push it to DTR.

Once the image is checked in you can update the image in the yaml as well as change the runAsUser and fsGroup in the yaml to match the gid and uid you built into the image. Then launch your zookeeper stateful set with:

`kubectl create -f zkDeploy.yaml`

After zookeeper is running we can now start up kafka. The kafka stateful set that uses glusterfs as its backend can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/storage/dynamic/glusterfs/nonroot/kafka.yaml)

Please note that you will need to update the yaml to specify your image and security context. The image provided is created from the contents of this [directory](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/storage/nonroot_docker_images/kafka). To use this dockerfile you simply update the KAFKA_USER and KAFKA_GROUP in the Dockerfile(lines 3 and 4) and subsequently edit lines 27-30 in the Dockerfile so that they match your uid and gid.(this can be obtained by running id from a unix server). Now you simply build the image and push it to DTR.

Once the image is checked in you can update the image in the yaml as well as change the runAsUser and fsGroup in the yaml to match the gid and uid you built into the image. 

After you have made these changes one more edit needs to be made in the kafka yaml you need to edit the namespace of your zookeeper on line 80 so that it looks like the below with <NAMESPACE> replaced by your namespace:

`          --override zookeeper.connect=zk-0.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-1.zk-svc.<NAMESPACE>.svc.cluster.local:2181,zk-2.zk-svc.<NAMESPACE>.svc.cluster.local:2181 \`

Then launch your kafka stateful set with:

`kubectl create -f kafka.yaml`

### ISCSI

Coming soon
