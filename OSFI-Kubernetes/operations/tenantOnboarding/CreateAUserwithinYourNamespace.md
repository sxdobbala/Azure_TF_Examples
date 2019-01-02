
# Creating a New ServiceAccount within Your namespace

As a namespace admin user you can run the below to create another service account for your namespace. It is highly recommended to not give out admin access to other service accounts and to only provide the edit or view cluster roles within the namespace. Additionally you can create custom roles and assign them to users within your namespace.


First a service account needs to be created and assigned to a namespace. To do so run the below:

`kubectl create serviceaccount <NAME>`

# Creating a role binding

Now we need to create a rolebinding to give the newly created service account edit permissions over the namespace

Create serviceaccount_rb.yaml:
```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: rb-<NAME>
subjects:
- kind: ServiceAccount
  name: <NAME>
  namespace: <NAMESPACE>
roleRef:
  kind: ClusterRole
  name: developer
  apiGroup: rbac.authorization.k8s.io
```

Now run:

`kubectl create -f serviceaccount_rb.yaml -n <NAMESPACE>`

The service account token can be retrieved and provided to the user with:

`kubectl -n <NAMESPACE> describe secret $(kubectl -n <NAMESPACE> get secret | grep <NAME> | awk '{print $1}')`
