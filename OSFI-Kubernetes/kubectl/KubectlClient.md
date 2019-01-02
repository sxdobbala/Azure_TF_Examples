To setup a Client machine within kubernetes please follow the steps outlined below.

# On Master
If a namespace for the tenant isn't created run the below command:

`kubectl create namespace <namespace>`

If a service account isn't already created run the below command:

`kubectl create serviceaccount <name> -n <namespace>`

You will need to grab the token from the master with:

`kubectl -n <NAMESPACE> describe secret $(kubectl -n <NAMESPACE> get secret | grep <NAME> | awk '{print $1}')`

Configure service account to use token:

`kubectl config set-credentials <user> --token=<TOKEN>`

Create cluster context with desired namespace and user:

`kubectl config set-context <user>-context --cluster=<clustername> --namespace=<namespace> --user=<user>`

Tie the service account to the edit clusterrole in the development namespace with a rolebinding:

`kubectl create -f <user>_role.yaml -n <namespace>`

user_role.yaml

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: rb-manager
subjects:
- kind: ServiceAccount
  namespace: development
  name: <ServiceAccount> 
roleRef:
  kind: ClusterRole
  name: edit
apiGroup: rbac.authorization.k8s.io
```

Tie the service account to the read only namespace clusterrole with a clusterrolebinding


`kubectl create -f <user>_namespace.yaml`

user_namespace.yaml:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: rb-manager-nsr
subjects:
- kind: ServiceAccount
  namespace: development
  name: <ServiceAccount>
roleRef:
  kind: ClusterRole
  name: namespace-read
apiGroup: rbac.authorization.k8s.io
```


If the namespace-read isn't created yet then run:

`kubectl create -f namespace-read.yaml`

namespace-read.yaml
```yaml
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  # "namespace" omitted since ClusterRoles are not namespaced
  name: namespace-read 
rules:
- apiGroups: [""]
  resources: ["namespaces"]
verbs: ["get", "watch", "list"]
```

Now we simply need to scp the ca.crt file for the cluster to the client machine:
 
  
`scp /etc/kubernetes/pki/ca.crt <msid>@<clientHost>:/desired/path`

# On Client:

## Download kubectl:

On the client machine the kubectl will need to be installed and moved to /usr/bin

`curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl`

## Install kubectl:

Add execute permissions to kubectl:

`chmod o+x kubectl`

Move kubectl to /usr/bin:

`mv kubectl /usr/bin`

Verify kubectl installed by running:

`which kubectl`

## Configure Cluster

Now configure the kubectl to use the cluster and ca.crt file we scp from master:

`kubectl config set-cluster kubernetes --server='https://<masterIP>:<port>' --certificate-authority=/path/to/ca.crt`

## Configure ServiceAccount:

Cconfigure the service account with the below command. 

`kubectl config set-credentials <name> --token=<token>`


## Create and Specify Context:

Now configure the context for this kubectl to use the created user and namespace defined in your rolebindings:

`kubectl config set-context <serviceaccount>-context --cluster=<clustername> --namespace=<namespace> --user=<serviceaccount> `

Specify the current context to be the newly created context:

`kubectl config use-context <serviceaccount>-context`

## Verify: 

Verify the cluster is configured with given context by running:

`kubectl get po`

You can also view your kube config file in:

`/home/<msid>/.kube/config`
  
  
For more info:
https://docs.bitnami.com/kubernetes/how-to/configure-rbac-in-your-kubernetes-cluster/#use-case-1-create-user-with-limited-namespace-access

https://kubernetes.io/docs/tasks/access-application-cluster/configure-access-multiple-clusters/

