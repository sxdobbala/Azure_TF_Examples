Below are the instrutions on how to download and install the kubernetes dasboard for your cluster:

# Prerequisites:

In order for the Kubernetes Dashboard to actually display data heapster needs to be installed in the cluster.

## Download/Install Heapster with Influfdb backend

Get the link for latest tar file from here:

`https://github.com/kubernetes/heapster/releases`

Then run wget <link>

`wget https://github.com/kubernetes/heapster/archive/v1.5.2.tar.gz`

Now we untar heapster with:

`tar -zxvf heapster.tar`

Now simply navigate to the heapster directory:

`cd heapster-<version>`

Since the architecture for our servers is amd64 we do not need to edit the yamls in deploy/kube-config/influxdb. If it isn't amd64 update image names in all the yaml files.
To install influxdb and heapster run the below commands:

`kubectl create -f deploy/kube-config/influxdb/`

`kubectl create -f deploy/kube-config/rbac/heapster-rbac.yaml`

To verify it is up run:

` kubectl get pods --namespace=kube-system`

You should see one heapster, one influxdb, and one grafana pod.

# Download/Install Kubernetes Dashboard

In order to install the kubernetes dashboard we need to download the dashboard yaml and edit the service section to add a destination port.

`wget https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml`

Now open up the yaml with vi and edit the service section so that it has the external IP of the master:

```yaml
kind: Service
apiVersion: v1
metadata:
  labels:
    k8s-app: kubernetes-dashboard
  name: kubernetes-dashboard
  namespace: kube-system
spec:
  ports:
    - port: 443
      targetPort: 8443
  selector:
    k8s-app: kubernetes-dashboard
  externalIPs:
  - MASTERIP
 ```
Now run:

`kubectl apply -f kubernetes-dashboard.yaml`

To verify it is up run:

`kubectl get po -n kube-system`

You should see a kubernetes-dashboard pod.

# Creating an Admin User
In order to create an admin user create the below admin-user.yaml:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  labels:
    k8s-app: kubernetes-dashboard
  name: admin-user
  namespace: kube-system
  ```
  
Now run :

`kubectl create -f admin-user.yaml`

Now we simply need to tie the clusterrole cluster-admin to the newly created service account with the below admin-crb.yaml:

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kube-system
  ```
Now run:

`kubectl create -f admin-crb.yaml`

We are now ready to login to the dashboard which is running at https://masterIP
In our cluster go to:

`https://10.178.129.151`

Now select token and grab your token from the kubectl by running the below:

`kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')`


 # Creating a Cluster Role Binding on Cluster Setup - To Allow list all namespaces in the Dashboard
 
 If the namespace read clusterrole and namespace-allserviceaccounts-crb clusterrolebinding have already been created on the cluster skip to the next section!
 
 If the user attempts to login to the dashboard now with the service account token they will not see any namespaces and will 
 have to type in there namespace. Additionally they won't be able to submit jobs to there namespace via the dashboard ui. 
 To resovle this we will create a clusterrole to give read access to all namespaces for all service accounts.
 
 Create the namespace-read.yaml:
 
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
  Now run:
  
  `kubectl create -f namespace-read.yaml`
  
  
  Now we simply need to bind the new clusterrole to the service account with allserviceaccounts_namespace.yaml:
  
  ```yaml  
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
    name: namespace-allserviceaccounts-crb
subjects:
  - kind: Group
    name: system:authenticated
    apiGroup: rbac.authorization.k8s.io
roleRef:
    kind: ClusterRole
    name: namespace-read 
    apiGroup: rbac.authorization.k8s.io
  ```
  
  Now run:
  
  `kubectl create -f allserviceaccounts_namespace.yaml`
  
  
