
# ADMIN - Install Helm:

## Install Helm 

https://github.com/kubernetes/helm/blob/master/docs/install.md

```
cd /k8s/helm/

wget https://storage.googleapis.com/kubernetes-helm/helm-v2.9.0-linux-amd64.tar.gz

tar -zxvf helm-v2.9.0-linux-amd64.tar.gz

export PATH=$PATH:/k8s/helm/linux-amd64

helm version

```
At this point 'helm version' only displays the client version as we haven't initialized the server side component of helm (Tiller). 
We plan to run Tiller in the tenant namespace. 

Please follow the steps to create a namespace [CreatingANamespaceAdmin.md](adminSetup/CreatingANamespaceAdmin.md)

# TENANT

## Helm setup

Helm is already installed at /k8s/helm/linux-amd64 , run the below command or set in profile.

```
export PATH=$PATH:/k8s/helm/linux-amd64

```

If you run the 'helm version' command it should just display the clinet version

Initialize helm with your service account and namespace (Make sure the service account has the admin rights in the namespace)

```
helm init --service-account <NAME> --tiller-namespace <NAMESPACE>

helm version --tiller-namespace <NAMESPACE>

Example output

mk7@dbslp1404:/home/mk7
$ helm version --tiller-namespace ns-hub-demo
Client: &version.Version{SemVer:"v2.9.0", GitCommit:"f6025bb9ee7daf9fee0026541c90a6f557a3e0bc", GitTreeState:"clean"}
Server: &version.Version{SemVer:"v2.9.0", GitCommit:"f6025bb9ee7daf9fee0026541c90a6f557a3e0bc", GitTreeState:"clean"}

```
Check if the tiller POD is running
```
kubectl get po

$ kubectl get po
NAME                             READY     STATUS    RESTARTS   AGE
tiller-deploy-85689d4655-nt2k2   1/1       Running   0          21m

```

## Setup security to tiller 

Ref: https://engineering.bitnami.com/articles/helm-security.html

```
kubectl --namespace=<NAMESPACE> patch deployment tiller-deploy --type=json --patch='[{"op": "add", "path": "/spec/template/spec/containers/0/command", "value": ["/tiller", "--listen=localhost:44134"]}]'

```
## Setup Jupyeter Hub

Most of the commands and set up instrctions are refered from here http://zero-to-jupyterhub.readthedocs.io/en/latest/setup-jupyterhub.html

### Prerequisites for Jupyterhub

* Setup a persistance volume with BDPaaS to access the data

Ref: [MapR Static volumes](../storage/mapr/README.md)

** update config.yaml
* If you want to allow only specific users to login into your jupyer hub set up a service account update the [config.yaml](config.yaml) with USER, PASSWORD and GROUP NAME,  else remove those lines.

      lookup_dn_search_user: "cn=<USER>,ou=Unix,ou=ServiceAccounts,ou=UHT,ou=UHG,dc=ms,dc=ds,dc=uhc,dc=com"
      lookup_dn_search_password: "<PASSWORD>"
      allowed_groups: ['CN=<<GROUP NAME>>,CN=Users,DC=ms,DC=ds,DC=uhc,DC=com']
* If you want to add your private key and public cert for SSL, get the pem files from cert services and copy the file content in the config.yaml file. careful about the line indents !!
* Change the https port
* 
* Update the Helm repo to get the Jupyterhub

```
helm repo add jupyterhub https://jupyterhub.github.io/helm-chart/
helm repo update

helm repo list
```

Get [config.yaml](config.yaml) which has the necessary configuration to Run the jupyetrhub, run the below command by replacing with your NAMESPACE

```

helm install jupyterhub/jupyterhub --version=v0.7-d540c9f --name jupyterhub --namespace <NAMESPACE> --tiller-namespace <NAMESPACE> -f config.yaml

```

check if the PODs are running, you should see at least 3 pods running

```
kubectl get po

$ kubectl get po
NAME                             READY     STATUS    RESTARTS   AGE
hub-6698b47f4f-dn65c             1/1       Running   0          56m
proxy-6847b5bdb7-kvbhj           1/1       Running   0          56m
tiller-deploy-85689d4655-nt2k2   1/1       Running   0          21m

```

To upgrade or remove jupyetrhub completly from Namespace use below commands

```
helm upgrade jupyterhub jupyterhub/jupyterhub --version=v0.7-d540c9f -f config.yaml --tiller-namespace <NAMESPACE>

helm del --purge jupyterhub --tiller-namespace <NAMESPACE>

```
# Building Single user notebooks

Jupyterhub version should match between the hub image and notebooks, current helm chart uses (0.9.*) 

Notes for getting new images - 

* UID and GID injection

# Running Deep Learning notebooks

# Running Spark notebooks connecting to BDPaaS

To run Spark notebooks against BDPaaS (yarn-client mode) we need 3 extra ports per application as BDPaaS nodes has to talk back with the Driver program running in side the kubernetes POD.

* Choose the appropriate image that has correct version of Spark binaries.
* Contact admin team for the ports assigned to your team.
* run the [spark-service-ports.yaml](spark-service-ports.yaml) file with the ports assigned to you.
* configure the ports while creating the spark context.
      chnage the PORT1, PORT2 and PORT3 as configured in the spark-service-ports.yaml
      change the queuename, and EXTERNAL IP to the correct values
```
import os
import socket
conf = {'spark.yarn.queue':'<queuename>', 
        'spark.executor.memory':'4G', 
        'spark.executor.cores':2,
        'spark.executor.instances':4, 
        'spark.ui.port': <PORT1>, 
        'spark.driver.port': <PORT2>, 
        'spark.driver.blockManager.port':<PORT3>,
        'spark.driver.host':"<EXTERNAL IP>",
        'spark.driver.bindAddress':socket.gethostbyname(socket.gethostname()),
        'spark.driver.maxResultSize ':'4G',
        'spark.driver.memory':'10G'}
sc, spark = setupSparkContext(conf) 
sqlContext = spark._wrapped

```

# Other Useful commands

## Auto completion of helm and kubectl

source <(helm completion bash)

source <(kubectl completion bash)

