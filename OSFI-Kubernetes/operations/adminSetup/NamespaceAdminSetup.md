Below are instructions for creating a namespace admin service account for logging into the kubernetes dashboard/setting up clients:
# Creating the Namespace

If the user does now have a namespace yet run the below:

`kubectl create namespace <NAMESPACE>`

# Creating the service account

First a service account needs to be created and assigned to a namespace. To do so run the below:

`kubectl create serviceaccount <NAME> -n <NAMESPACE>`

# Creating a role binding for service account

Now we need to create a rolebinding to give the newly created service account edit permissions over the namespace
 
 Create serviceaccount_rb.yaml:
 
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
   name: rb-<TENANT NAME>-admin-sa
subjects:
- kind: ServiceAccount
   name: <TENANT-SERVICE-ACCOUNT>
   namespace: <NAMESPACE>
roleRef:
   kind: ClusterRole
   name: admin
   apiGroup: rbac.authorization.k8s.io
  ```
 Now run:
 
 `kubectl create -f serviceaccount_rb.yaml -n <NAMESPACE>`
 
 # Create a role binding for dashboard users
 
 Tenant admins are assigned to admin role - a cluster role that allows read/write access to most resources in a namespace 
 
  Create admin_rb.yaml:
  
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: rb-<TENANT-NAME>-admin-user
subjects:
- kind: Group
  name: <TENANT-NAME>-admin
  apiGroup: rbac.authorization.k8s.io
  namespace: <NAMESPACE>
roleRef:
  kind: ClusterRole
  name: admin 
  apiGroup: rbac.authorization.k8s.io
```
 Now run:
 
 `kubectl create -f admin_rb.yaml -n <NAMESPACE>`
 
 Regular dashboard users are assigned to developer role - a custom cluster role created by removing access to secrets
 
 Create developer_rb.yaml:
 
 ```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: rb-<TENANT-NAME>-developer
subjects:
- kind: Group
  name: <TENANT-NAME>-developer
  apiGroup: rbac.authorization.k8s.io
  namespace: <NAMESPACE>
roleRef:
  kind: ClusterRole
  name: developer 
  apiGroup: rbac.authorization.k8s.io
```
 Now run:
 
 `kubectl create -f developer_rb.yaml -n <NAMESPACE>`
 
  # Retrieving the token
  
  We can now pull the token via the cli and share it to the tenant admin:
  
  `kubectl -n <NAMESPACE> describe secret $(kubectl -n <NAMESPACE> get secret | grep <NAME> | awk '{print $1}')`
  
 
 
 
 
 



