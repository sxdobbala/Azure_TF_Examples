# Before You Enable Pod Security Policies

All yaml files can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/yamls/psp) and all scripts can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/tree/master/psp_scripts).

Before enabling pod security policies within the cluster you first have to define some for the apiserver to launch. In order to do so please create the below policies
and bindings:

The below yaml creates a pod secuirty policy that allows access to everything within a cluster.


admin_policy.yaml
```yaml
apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: admin-policy
  annotations:
    seccomp.security.alpha.kubernetes.io/allowedProfileNames: '*'
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
  allowedCapabilities:
  - '*'
  hostNetwork: true
  hostPorts:
  - min: 0
    max: 65535
  hostIPC: true
  hostPID: true
  ```
`kubectl create -f admin_policy.yaml`

Now that we have a pod security policy in place we need to create a cluster role that enables the use of the pod security policy which can be seen in the below yaml.

adminpolicy_cr.yaml
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
 `kubectl create -f adminpolicy_cr.yaml`
 
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
  
`kubectl create -f node-kubelet-psp-rb.yaml`
  
We now need to create a clusterrolebinding for certain serviceaccounts within the kube-system namespace. If we tie a pod security policy to all of the service accounts in the namespace we experience issues with all pods getting the admin policy applied. Hence I have created a script to create a clusterrolebinding for a specific set of service accounts.

Simply run the psp_crb_gen.sh script on the server where kubectl is installed. It can be found [here](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/psp_crb_gen.sh) and update the path of [sa.txt](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/sa.txt) to the path where you store the sa.txt file on the server. The script assumes that [admin-default-crb.yaml](https://github.optum.com/kubernetes/OSFI-Kubernetes/blob/master/psp_scripts/admin-default-crb.yaml) is in the same directory as the script.

To verify that pod security policies can be used by service accounts we can use the below kubectl command and replace policy name, service account name, and namespace:

`kubectl auth can-i use podsecuritypolicy/<policyname> --as system:serviceaccount:<namespace>:<serviceaccount>`
  
  # Enabling Pod Security Policies
  
Before enabling pod security policies please make sure to create pod security policies for all out of the box service accounts so that the apiserver will start up properly.
  
Edit the apiserver manifest located here /etc/kubernetes/manifests/kube-apiserver.yaml. Edit the admission controllers to look as the below:
  
  `--admission-control=Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,DefaultTolerationSeconds,NodeRestriction,ResourceQuota,PodSecurityPolicy`
  
  Save the file and monitor :
  
  `journalctl -fu kubelet`
  
  Until you stop seeing connection refused.
  
  Optionally restart the kubelet
  
  `systemctl restart kubelet`
  
  At this point in time only system service accounts can launch pods into the cluster. In order to allow other service accounts to launch pods you would need to tie them to the admin pod security polciy or create a new pod security policy.
  
  
# Default User Pod Security Policy and Mapping

User Pod Security Policy for a default user. No root, hostPID, hostNetwork, hostIPC, priveleged, or Host Port Access. Additionially restricted to ceratin volume types:

default-user-psp.yaml
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
  
`kubectl create -f default-user-psp.yaml`

Cluster role  for default-user policy:

default-user-cr.yaml
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
`kubectl create -f default-user-cr.yaml`

Cluster Role binding for all authenticated users of cluster:

default-user-crb.yaml
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

`kubectl create -f default-user-crb.yaml`

Now all pods run in the cluster that do not have admin access will have the above pod security policy applied.


# Optional MapR Pod Security Policies

In order for the mapr flex volume daemonset and replicaset to launch as needed to create flex volume provisioners. A pod security policy needs to be created for the accounts that run these pods.

The policies are as below:

mapr-psp.yaml
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
`kubectl create -f mapr-psp.yaml` 
 
 Cluster Role:
 
 mapr-cr.yaml
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
  `kubectl create -f mapr-cr.yaml`
  
  Cluster Role Binding:
  
  mapr-crb.yaml
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

`kubectl create -f mapr-crb.yaml`

Now with this in place we can install the mapr flex volume plugin.

Given the default users pod security policy they should be able to create mapr flex volumes. To test this we run the below pod after creating the mapr ticket secret.
