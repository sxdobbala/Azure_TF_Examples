# Service Discovery
Services can be discovered internally using a combination of services and core dns. Core dns and services are both built into the cluster. You simply need to set up a service and then core dns will make these services available.

## Cluster IP
To expose a service internally you will first need to create a cluster ip service type. This makes your service availabe within the cluster on the assigned cluster ip.

```yaml
kind: Service
apiVersion: v1
metadata:
  labels:
    app: <service label>
  name: <service name>
  namespace: <namespace>
spec:
  ports:
    - port: <desired port>
      name: <port name>
      protocol: <protocol type TCP OR UDP>
      targetPort: <container port>
  selector:
    app: <value of your app label>
```

Once this is up you can pull the cluster ip with the below command and access it on the port defined in the yaml

`kubectl get svc -n <namespace> | grep <service name> | awk '{print $3}'`

Additionally if you don't remember what port you defined you can run the below to extract it:

 `kubectl get svc -n <namespace> | grep <service name> | awk '{print $5}'`
 
 Now your pod can be accessed from anywhere within the cluster on that ip:port. The IP or port could change if the service were to go restart. To solve this we recommend using core dns.
 
 ## Core DNS
 
 Once you have a service running of any type within the cluster you can access it via a dns service provided by kubernetes called core dns.
 
 To retrieve the cluster ip we simply put the hostname as:
 
 `<service_name>.<namespace>.svc.cluster.local`
 
 To retrieve the port we simply put the port as:
 
 `_<port-name>._<port-protocol>.<service name>.<namespace>.svc.cluster.local`
 
 Now to connect to a container from another container we no longer need to know the cluster ip and port we can simply call:
 
 `http://<service_name>.<namespace>.svc.cluster.local:_<port-name>._<port-protocol>.<service name>.<namespace>.svc.cluster.local`
