 # Pod Security Policies  
 Pod Security Policies in Kubernetes allow restrictions to be places around pods. Such as what users and groups are allowed to run
 pods and if pods can be run in priveleged mode. A common use of this would be to prohibit pods from being run as the root
 user which could cause security implications.
 ## Before we Start
 Before we start we need to verify that the development and mapr-examples namespaces are created and the default service accounts for both have been tied to the admin role for the namespace.
 
 `kubectl create namespace development`
 
 `kubectl create namespace mapr-examples`
 
 
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: rb-default-mapr
  namespace: mapr-examples
subjects:
- kind: ServiceAccount 
  name: default
  namespace: mapr-examples 
roleRef:
  kind: ClusterRole
  name: admin 
  apiGroup: rbac.authorization.k8s.io
```

`kubectl create -f mapr-examples-crb.yaml`


```yaml

apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: rb-default-development
  namespace: development
subjects:
- kind: ServiceAccount 
  name: default
  namespace: development
roleRef:
  kind: ClusterRole
  name: admin 
  apiGroup: rbac.authorization.k8s.io
  ```
  
  `kubectl create -f development-crb.yaml`


Now that all of this is created it is important to note that we need to impersonate with every command we run to launch a deployment/pod/replicaset/daemonset otherwise it will also pull in the pod security policy of the kubernetes admin which is the admin policy on the cluster.

To do so add --as system:serviceaccount:<namespace>:<serviceaccount> to the end of a command.


For Example:
`kubectl create -f jupyter-pod.yaml  -n development --as system:serviceaccount:development:default`



## Creating a Pod Security Policy
Before enabling pod security policies within the cluster you first have to define some for the apiserver to launch. In order to do so please create the below policies
and bindings:

The below yaml creates a pod secuirty policy that allows access to everything within a cluster.
admin_policy.yaml
```yaml
apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: admin-policy 
spec:
  privileged: true 
  runAsUser:
    rule: RunAsAny
  seLinux:
    rule: RunAsAny
  fsGroup:
    rule: RunAsAny
  supplementalGroups:
    rule: RunAsAny
  volumes:
  - '*'
  hostNetwork: true
  hostPorts:
  - min: 0
    max: 65535
  hostIPC: true
  hostPID: true
  ```

Now that we have a pod security policy in place we need to create a cluster role that enables the use of the pod security policy which can be seen in the below yaml.

adminpolicy_cr.yml
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: admin-policyrole
  labels:
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
rules:
- apiGroups:
  - extensions
  resourceNames:
  - admin-policy
  resources:
  - podsecuritypolicies
  verbs:
  - use
 ```
 
 Now we simply create a cluster role binding to tie the out of the box node and kubelet groups and service accounts to the cluster role that has access to the admin pod security policy we just created. 
 
 node-kubelet-psp-rb.yaml
 
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: node-psp
  namespace: kube-system
  annotations:
    kubernetes.io/description: 'Allow nodes to create privileged pods. Should
      be used in combination with the NodeRestriction admission plugin to limit
      nodes to mirror pods bound to themselves.'
  labels:
    addonmanager.kubernetes.io/mode: Reconcile
    kubernetes.io/cluster-service: 'true'
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: admin-policy
subjects:
  - kind: Group
    apiGroup: rbac.authorization.k8s.io
    name: system:nodes
  - kind: User
    apiGroup: rbac.authorization.k8s.io
    # Legacy node ID
    name: kubelet
  ```
  
We now need to create a clusterrolebinding for certain serviceaccounts within the kube-system namespace. If we tie a pod security policy to all of the service accounts in the namespace we experience issues with all pods getting the admin policy applied. Hence I have created a script to create a clusterrolebinding for a specific set of service accounts.

Simply run the psp_crb_gen.sh script on the server where kubectl is installed. It can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/psp_crb_gen.sh) and update the path of [sa.txt](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/sa.txt) to the path where you store the sa.txt file on the server. The script assumes that [admin-default-crb.yaml](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/admin-default-crb.yaml) is in the same directory as the script.

To verify that pod security policies can be used by service accounts we can use the below kubectl command and replace policy name, service account name, and namespace:

`kubectl auth can-i use podsecuritypolicy/<policyname> --as system:serviceaccount:<namespace>:<serviceaccount>`
  
  ## Enabling Pod Security Policies
  
Before enabling pod security policies please make sure to create pod security policies for all out of the box service accounts so that the apiserver will start up properly.
  
Edit the apiserver manifest located here /etc/kubernetes/manifests/kube-apiserver.yaml. Edit the admission controllers to look as the below:
  
  `--admission-control=Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,DefaultTolerationSeconds,NodeRestriction,ResourceQuota,PodSecurityPolicy`
  
  Save the file and monitor :
  
  `journalctl -fu kubelet`
  
  Until you stop seeing connection refused.
  
  At this point in time only system service accounts can launch pods into the cluster. In order to allow other service accounts to launch pods you would need to tie them to the admin pod security polciy or create a new pod security policy.
  
  
## Example User Pod Security Policy and Mapping

User Pod Security Policy for a default user. No root, hostPID, hostNetwork, hostIPC, priveleged, or Host Port Access. Additionially restricted to ceratin volume types:


```yaml
apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: default-user
spec:
  privileged: false
  allowPrivilegeEscalation: false
  hostNetwork: false
  hostIPC: false
  hostPID: false
  runAsUser:
    rule: MustRunAsNonRoot
  seLinux:
    rule: RunAsAny
  fsGroup:
    rule: 'MustRunAs'
    ranges:
    - min: 1
      max: 65535
  supplementalGroups:
    rule: 'MustRunAs'
    ranges:
    - min: 1
      max: 65535
  volumes:
  - 'configMap'
  - 'emptyDir'
  - 'projected'
  - 'downwardAPI'
  - 'persistentVolumeClaim'
  - 'glusterfs'
  - 'iscsi'
  - 'flexVolume'
  - 'secret'
  allowedFlexVolumes:
  - driver: 'mapr.com/maprfs'
  ```

Sample cluster role  for default-user policy:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: default-user-role
  labels:
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
rules:
- apiGroups:
  - extensions
  resourceNames:
  - default-user
  resources:
  - podsecuritypolicies
  verbs:
  - use
  ```
  
  Sample Cluster role binding for default user which is appleid to all service accounts:
  
  
  ``` yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
    name: psp-allserviceaccounts-crb
subjects:
  - kind: Group
    name: system:serviceaccounts
    apiGroup: rbac.authorization.k8s.io
roleRef:
    kind: ClusterRole
    name: default-user-role
    apiGroup: rbac.authorization.k8s.io
```

Sample Cluster Role binding for all authenticated users:

```yaml

apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: psp-allauthenticated-crb
subjects:
- kind: Group
  name: system:authenticated
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole
  name: default-user-role
  apiGroup: rbac.authorization.k8s.io
```


Now all pods run in the cluster that do not have admin access will have the above pod security policy applied.

## Running a pod as non root

In order to run a pod as a non root user a security context has to be provided in the deployment template or the pod itself. The deployment should look similar to the below:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jupyter-pod
spec:
  selector:
    matchLabels:
      app: jupyter-pod
  replicas: 1 
  template:
    metadata:
      labels:
        app: jupyter-pod
    spec:
      securityContext: 
        fsGroup: 54619
        runAsUser: 496761
      containers:
      - name: jupyter-pod
        image: docker.optum.com/bd_docid/jupyter-dl-notebook:nonroot 
        env:
        - name: JUPYTERPWD
          value: "jupyterPasswrd"
        - name: PORT0
          value: "8898"
        resources:
         limits: 
          alpha.kubernetes.io/nvidia-gpu: 1
         requests:
           memory: 32Gi
           cpu: 16000m 
        securityContext:
          privileged: false
        ports:
        - containerPort: 8888 
```
`kubectl create -f jupyter-pod.yaml  -n development --as system:serviceaccount:development:default`

Note that if the uid doesn't already exist in the docker image the username will be null which could cause issues.

To verify the default-user psp was used run the below:
 
 `kubectl describe po jupyter | grep psp`

## Attempting to Run with priveleged, root, HostIPC, HostNetwork or HostPID

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jupyter-pod
spec:
  selector:
    matchLabels:
      app: jupyter-pod
  replicas: 1 
  template:
    metadata:
      labels:
        app: jupyter-pod
    spec:
# Remove this context to test as root user
      securityContext: 
        fsGroup: 54619  
        runAsUser: 496761 
# Test with true and hostNetwork false, hostPID false, priveleged False  
      hostIPC: true 
# Test with true and hostIPC false, hostPID false, priveleged False. When true also tests host port
      hostNetwork: true   
# Test with true and hostIPC false, hostNetwork false, priveleged False  
      hostPID: true 
      containers:
      - name: jupyter-pod
        image: docker.optum.com/bd_docid/jupyter-dl-notebook:nonroot 
        env:
        - name: JUPYTERPWD
          value: "atlsmsos1234"
        - name: PORT0
          value: "8898"
        resources:
         limits: 
          alpha.kubernetes.io/nvidia-gpu: 1
         requests:
           memory: 32Gi
           cpu: 16000m 
        securityContext:
 # Test with true and hostIPC false, hostNetwork false, hostPID False  
          privileged: true 
        ports:
        - containerPort: 8888 
```
`kubectl create -f jupyter-pod.yaml  -n development --as system:serviceaccount:development:default`

Results:


|Test Case|Result|Log Location|
| --------- | ------ | ------------ |
| hostPID | Deployment does not spin up pod | kubectl describe replicaset |
| priveleged | Deployment does not spin up pod | kubectl describe replicaset |
| hostNetwork | Deployment does not spin up pod | kubectl describe replicaset |
| hostIPC | Deployment does not spin up pod | kubectl describe replicaset |
| hostPort | Deployment does not spin up pod | kubectl describe replicaset |
| root | Pod Spins up and fails | kubectl describe po |

In all use cases the pod did not spin up successfully and in all but the case where the pod started as root the logging for failure is found in the replicaset describe command. Most uers would expect to find the error in pod or deployment. We need to make it clear that this error is found in the replicaset.


## Test Case where user has two conflicting policies

If a user is assigned both the default user and a super user policy the expected result would be that the super user policy would take precedence based off of kubernetes documentation. To test this set up a 2nd pod security policy for the rbacdev user and switch priveleged to true and allowPrivelegeEscalation to true.

```yaml

apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: super-user 
spec:
  privileged: true
  allowPrivilegeEscalation: true 
  hostNetwork: false
  hostIPC: false
  hostPID: false
  runAsUser:
    rule: MustRunAsNonRoot
  seLinux:
    rule: RunAsAny
  fsGroup:
    rule: RunAsAny
  supplementalGroups:
    rule: RunAsAny
  volumes:
  - 'configMap'
  - 'emptyDir'
  - 'projected'
  - 'downwardAPI'
  - 'persistentVolumeClaim'
  - 'glusterfs'
  - 'iscsi'
  - 'flexVolume'
  - 'secret'
  allowedFlexVolumes:
  - driver: 'mapr.com/maprfs'
  ```


Now create a cluster role and cluster role binding that gives the rbacdev serviceaccount access to newly created podsecurity policy.
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: super-user-policyrole
  labels:
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
rules:
- apiGroups:
  - extensions
  resourceNames:
  - super-user 
  resources:
  - podsecuritypolicies
  verbs:
  - use
 ```
 
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: crb-rbacdev-super-user
subjects:
- kind: ServiceAccount 
  name: rbacdev
  namespace: development
roleRef:
  kind: ClusterRole
  name: super-user-policyrole
  apiGroup: rbac.authorization.k8s.io
  ```




Then simply attempt to run a pod and add serviceAccountName into the spec and switch priveleged to true. This is outlined below:

```yaml

kind: Deployment
metadata:
  name: jupyter-pod
spec:
  selector:
    matchLabels:
      app: jupyter-pod
  replicas: 1 
  template:
    metadata:
      labels:
        app: jupyter-pod
    spec:
      serviceAccountName: rbacdev
      securityContext: 
        fsGroup: 54619
        runAsUser: 496761
      containers:
      - name: jupyter-pod
        image: docker.optum.com/bd_docid/jupyter-dl-notebook:nonroot 
        env:
        - name: JUPYTERPWD
          value: "jupyterPasswrd"
        - name: PORT0
          value: "8898"
        resources:
         limits: 
          alpha.kubernetes.io/nvidia-gpu: 1
         requests:
           memory: 32Gi
           cpu: 16000m 
        securityContext:
          privileged: true 
        ports:
        - containerPort: 8888 
```
`kubectl create -f jupyter-pod.yaml  -n development --as system:serviceaccount:development:rbacdev`


As expected the pod launches. We can verify the psp that was used with:
 
 `kubectl describe po jupyter | grep psp`
 
 If you attempt to run without including the serviceAccountName in the pod it will fail as it attempts to use the default-user service account.


## Pod Security Policies With MapR Flex Volume
In order for the mapr flex volume daemonset and replicaset to launch as needed to create flex volume provisioners. A pod security policy needs to be created for the accounts that run these pods.

The policies are as below:

```yaml
apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: mapr-kdf-psp
spec:
  volumes:
    - 'configMap'
    - 'emptyDir'
    - 'projected'
    - 'secret'
    - 'downwardAPI'
    - 'persistentVolumeClaim'
    - 'hostPath'
    - 'flexVolume'
  allowedHostPaths:
    - pathPrefix: "/opt"
    - pathPrefix: "/usr/libexec/kubernetes/kubelet-plugins/volume/exec/"
    - pathPrefix: "/etc/kubernetes"
    - pathPrefix: "/etc/localtime"
  allowedFlexVolumes:
    - driver: mapr.com/maprfs
  runAsUser:
    rule: 'RunAsAny'
  seLinux:
    rule: 'RunAsAny'
  supplementalGroups:
    rule: 'RunAsAny'
  fsGroup:
    rule: 'RunAsAny'
 ```
 
 
 Cluster Role:
 
 ```yaml
 apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: mapr-kdf-policyrole
  labels:
    kubernetes.io/cluster-service: "true"
    addonmanager.kubernetes.io/mode: Reconcile
rules:
- apiGroups:
  - extensions
  resourceNames:
  - mapr-kdf-psp 
  resources:
  - podsecuritypolicies
  verbs:
  - use
  ```
  
  Cluster Role Binding:
  
  ```yaml
  
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: crb-mapr-psp
subjects:
- kind: Group 
  name: system:serviceaccounts:mapr-system 
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole
  name: mapr-kdf-policyrole
  apiGroup: rbac.authorization.k8s.io
```

Now with this in place we can install the mapr flex volume plugin.

Given the default users pod security policy they should be able to create mapr flex volumes. To test this we run the below pod after creating the mapr ticket secret.

```yaml

apiVersion: v1
kind: Pod
metadata:
  name: test-secure-prod
  namespace: mapr-examples
spec:
  securityContext:
    runAsUser: 60217
    fsGroup: 53134
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
    volumeMounts:
    - mountPath: /mapr
      name: maprvolume
  volumes:
    - name: maprvolume
      flexVolume:
        driver: "mapr.com/maprfs"
        readOnly: false 
        options:
          volumePath: "/datalake/corporate/ses_dlpoc/tmp"
          cluster: "datalake_prod"
          cldbHosts: "dbslp0294 dbslp0496 dbslp0532"
          securityType: "secure"
          ticketSecretName: "dlpocid-ticket-secret-prod"
          ticketSecretNamespace: "mapr-examples"
 ```
 `kubectl create -f jupyter-pod.yaml  -n mapr-examples --as system:serviceaccount:mapr-examples:default`

 The pod launches and we can see the correct policy was being used by running:
 
 `kubectl describe po test-secure-prod | grep psp`

# Attempting to use a volume type which is unauthorized

In order to test this we need to run the below yaml:


```yaml

apiVersion: v1
kind: Pod
metadata:
  name: test-secure-prod
  namespace: mapr-examples
spec:
  securityContext:
    runAsUser: 60217
    fsGroup: 53134
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
    volumeMounts:
    - mountPath: /mapr
      name: maprvolume
  volumes:
    - name: maprvolume 
      nfs:
        # Use real NFS server address here.
        server: CHANGEME
        # Use real NFS server export directory.
        path: "/mapr/datalake/corporate/ses_dlpoc/tmp"
        readOnly: true
        
  ```
   `kubectl create -f jupyter-pod.yaml  -n mapr-examples --as system:serviceaccount:mapr-examples:default`

  As expected we see that the pod failed to create due to nfs volume.
