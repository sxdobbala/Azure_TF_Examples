# Test iSCSI Storage plugin/driver from Kubernetes Cluster


## Prerequistes:
* K8s cluster is confgiured with iSCSI on 3Par
* iSCSI - 3Par CLI/UI tool(s) are in place to provision a storage volume 
* Security is configured to consume the Storage Volume

## Assumption
* RBAC inside a storage volume is not covered in these testing plans

##### The testing effort can be split into two categories Static and Dynamic provsioning of storage

## Static Volume 
##### Prerequistes
* iSCSI Admin team provisioned couple of storage volumes
* create a namepsace to execute all pods
* Use System account (SA) to execute the command NOT admin user
* Security - Create Secrets for different types of users
   * Redonly Credential
   * Read/Write Credential
 
##### Test #1 - Directly mount the volume
* Create a simple busybox or any image container to mount the volume by providing all details+secret
* Get into the running container 
* Execute df command to review the mounted path

##### Test #2 - Use PV (persistence volume) and PVC (persistence volume claim) to mount the volume
* Create a PV with Secret detail with Name spaces
* Create PVC in the namespace
* Create a simple busybox or any image container to mount the volume using PVC
* Get into the running container 
* Execute df command to review the mounted path

##### Test #3 - Make sure data is persistence
* Create a simple busybox or any image container to mount the volume by providing all details+secret
* Get into the running container 
* create a file with some content
* Exit the container
* Delete the pod/container
* Create container with mount the same path by providing all details+secret
* Review your created file

##### Test #4 - Try to mount with NO security(secret)
* Create a simple busybox or any image container to mount the volume by providing all details NO Secret
* The container should fail to start

##### Test #5 - Test Persistence Mode - ReadWriteOnce
* Create a simple busybox or any image container to mount the volume using PVC
* use replicas or create another container
* only one container should come up

##### Test #6 - Test Persistence Mode - ReadOnlyMany - NOT Applicable- only on container can be mounted
* Create a simple busybox or any image container to mount the volume using PVC
* use replicas or create another container
* All containers should come up
* Get into the running container
* Get to mounted path
* Try to create or existing file
* The operation should fail

##### Test #7 - Test Persistence Mode - ReadWriteMany - NOT Applicable- only on container can be mounted
* Create a simple busybox or any image container to mount the volume using PVC
* use replicas or create another container
* The container deploy should fail or only one container should come up
* This behavior is not supported in iSCSI


##### Test #8 - Mounting the same volume with diffrerent paths in multiple containers-ReadOnlyMany Mode  - NOT Applicable- only on container can be mounted
* Create a simple busybox or any image container to mount the volume by providing all details+secret
* Create another simple busybox or any image container to mount a subpath(1) the volume by providing all details+secret
* Create another simple busybox or any image container to mount a subpath(2) the volume by providing all details+secret
* All containers should come up
* Get into the running container
* Get to mounted path
* Should be able to view the content of the file

##### Test #9 - Run Kafka using Multiple volume
* Need multiple Storage volume for this test 
* Create PV for each Storage volume
* Deplpoy Kafka cluster using multiple 
* Producer and comsumer a sample message
* Delete the Kakfa cluster
* Start the Kafka cluster
* You should be able to read the earlier produced messages


## Dynamic Volume Provisoing 
* TBD - based on availibility of Driver/Plugin


