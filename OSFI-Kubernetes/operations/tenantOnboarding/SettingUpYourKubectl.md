# Configure KubeCtl for a cluster

An Edge Node is configured with Docker daemon and Kubectl client for the cluster. Tenants can leverage this edge node to build docker images, publish imaged to Optum Docker Registry, commit code to github and use Kubernetes command line tool 'kubectl' for kubernetes API server communication.

Edge Node: dbslp1748

Global Group for getting access to the edge node: k8s_prod_usr

This is the required process to interact with an OSFI kubernetes cluster from [`kubectl`](https://kubernetes.io/docs/reference/kubectl/overview/) 

## Requirements
- **`kubectl`**: installed on machine dbslp1748
- **cluster master `IP:Port` or `Host`**:
    - **CTC**: 10.202.2.252:6443
    - **ELR**: 10.49.2.252:6443
- **ca.crt**: ca.crt is located under
    - **CTC**: /k8s/conf/certs/ca.crt
    - **ELR**: /k8s/conf/certs/ca_elr.crt
- **service token**: 
    - admins: service account token can be retrieved from Kubernetes Dashboard
        1. Open the secret associated to the service account
        2. Click on the view icon next to `token`
    - users: Admin should provide a token
    
## Setting up

```bash
# Set Cluster
$ kubectl config set-cluster kubernetes --server='https://<masterIP>:<port>' --certificate-authority=<ca.crt path>

# Configure service account
$ kubectl config set-credentials <serviceaccount> --token=<token>

# Create and Specify Context
$ kubectl config set-context <serviceaccount>-context --cluster=kubernetes --namespace=<namespace> --user=<serviceaccount>

# Specify the current context to be the newly created context
$ kubectl config use-context <serviceaccount>-context

# Verify
$ kubectl get po
```


You can also view your kube config file in `$HOME/.kube/config`
  
  
For more info:
- https://docs.bitnami.com/kubernetes/how-to/configure-rbac-in-your-kubernetes-cluster/#use-case-1-create-user-with-limited-namespace-access
- https://kubernetes.io/docs/tasks/access-application-cluster/configure-access-multiple-clusters/

