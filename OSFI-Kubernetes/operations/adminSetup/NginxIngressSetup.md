# Ingress Controllers
Within Kubernetes there are various ways to expose a service to the outside world. The preferred method however is to use an ingress controller
that redirects traffic to internal services. Routing rules are provided to the ingress controller in the form of the Kubernetes Ingress object.
The ingress object lets you route traffic based off of path and host header to different services within the cluster.

In the below sections we will walk through setting up and using the nginx ingress controller.

**NOTE: Replace Contents of \<NAMESPACE\> AND \<INGRESS CLASS\> with appropriate variables.**

# Install the Nginx Ingress Controller
## Creating Cluster Role (One time per cluster)
Assuming RBAC is enabled a cluster role will need to be created for use by the nginx-ingress service account(s). This cluster role will only 
need to be created once on every cluster. 

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRole
metadata:
  name: nginx-ingress-clusterrole
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - endpoints
      - nodes
      - pods
      - secrets
    verbs:
      - list
      - watch
  - apiGroups:
      - ""
    resources:
      - nodes
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - services
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - "extensions"
    resources:
      - ingresses
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - ""
    resources:
        - events
    verbs:
        - create
        - patch
  - apiGroups:
      - "extensions"
    resources:
      - ingresses/status
    verbs:
      - update
```

## Create a service account

Every nginx ingress controller will need a service account created in its respective namespace. 

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nginx-ingress-serviceaccount
  namespace: <NAMESPACE>
```

## Create a Cluster Role Binding

Now that we have the newly created service account we need to tie it the cluster role binding.

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: nginx-ingress-clusterrole-kube-system-binding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: nginx-ingress-clusterrole
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: <NAMESPACE>
```
## Create a Role
We now need to create a role that gives access to permissions specific to the namespace that the ingress controller will run in. In order to do so we will need to create an ingress-class name for the namespace. For ease we can just use the namespace name. If a 2nd controller is needed for one namespace then we simply edit this and every reference to <INGRESS CLASS>. Please update <NAMESPACE> and <INGRESS CLASS> Below.

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: Role
metadata:
  name: nginx-ingress-role
  namespace: kube-system 
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - pods
      - secrets
      - namespaces
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - configmaps
    resourceNames:
      # Defaults to "<election-id>-<ingress-class>"
      # Here: "<ingress-controller-leader>-<nginx>"
      # This has to be adapted if you change either parameter
      # when launching the nginx-ingress-controller.
      - "ingress-controller-leader-<INGRESS CLASS>"
    verbs:
      - get
      - update
  - apiGroups:
      - ""
    resources:
      - configmaps
    verbs:
      - create
  - apiGroups:
      - ""
    resources:
      - endpoints
    verbs:
      - get
```
    
## Create a Rolebinding
Now that we have a role created we need to tie this role to the nginx ingress controller service account.
    
```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: nginx-ingress-role-nisa-binding
  namespace: kube-system 
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: nginx-ingress-role
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: <NAMESPACE>
```

## Create configmaps
A configmap needs to be created in the namepsace that can provide nginx configurations if needed.
```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: nginx-configuration
  namespace: <NAMESPACE> 
  labels:
    app: ingress-nginx
```
Config maps also need to be created for tcp and udp services.

```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: tcp-services
  namespace: <NAMESPACE> 
```

```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: udp-services
  namespace: <NAMESPACE> 
```
## Starting the Default Backend
The ingress controller has a default backend application that runs that will display a 404 message if the router does not find an ingress rule that matches
To launch the default backend launch the below deployment and service.

```yaml
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: default-http-backend
  labels:
    app: default-http-backend
  namespace: <NAMESPACE> 
spec:
  replicas: 1
  selector:
    matchLabels:
      app: default-http-backend
  template:
    metadata:
      labels:
        app: default-http-backend
    spec:
      terminationGracePeriodSeconds: 60
      containers:
      - name: default-http-backend
        # Any image is permissible as long as:
        # 1. It serves a 404 page at /
        # 2. It serves 200 on a /healthz endpoint
        image: gcr.io/google_containers/defaultbackend:1.4
        livenessProbe:
          httpGet:
            path: /healthz
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 30
          timeoutSeconds: 5
        ports:
        - containerPort: 8080
        resources:
          limits:
            cpu: 10m
            memory: 20Mi
          requests:
            cpu: 10m
            memory: 20Mi
---

apiVersion: v1
kind: Service
metadata:
  name: default-http-backend
  namespace: <NAMESPACE> 
  labels:
    app: default-http-backend
spec:
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: default-http-backend
 ```
## Launching the Nginx Ingress Controller
Now that we have a default backend running and all the required roles assigned to the service account we can launch the controller itself. Specify the ingress class name in the --ingress-class=<INGRESS CLASS> cli flag. Additionally to restrict the ingress controller to a tenants namespace specify the --watch-namespace=<NAMESPACE> cli flag

```
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: nginx-ingress-controller
  namespace: <NAMESPACE>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ingress-nginx
  template:
    metadata:
      labels:
        app: ingress-nginx
      annotations:
        prometheus.io/port: '10254'
        prometheus.io/scrape: 'true'
    spec:
      serviceAccountName: nginx-ingress-serviceaccount
      containers:
        - name: nginx-ingress-controller
          image: quay.io/kubernetes-ingress-controller/nginx-ingress-controller:0.14.0
          args:
            - /nginx-ingress-controller
            - --default-backend-service=$(POD_NAMESPACE)/default-http-backend
            - --configmap=$(POD_NAMESPACE)/nginx-configuration
            - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
            - --udp-services-configmap=$(POD_NAMESPACE)/udp-services
            - --annotations-prefix=nginx.ingress.kubernetes.io
            - --ingress-class=<INGRESS CLASS>
            - --watch-namespace=<NAMESPACE>
            - --force-namespace-isolation
          env:
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
          ports:
          - name: http
            containerPort: 80
          - name: https
            containerPort: 443
          livenessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            initialDelaySeconds: 10
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 1
          readinessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 1
          securityContext:
            runAsNonRoot: false
```


## Exposing the Nginx Ingress Controller
We now will expose the ingress controller to the outside world with a node port service.

```
kind: Service
metadata:
  name: ingress-nginx
  namespace: <NAMESPACE> 
spec:
  type: NodePort
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
  - name: https
    port: 443
    targetPort: 443
    protocol: TCP
  selector:
    app: ingress-nginx
 ```

# Verify the controller is up
To verify the controller is up and working run:

`kubectl get svc -n <NAMESPACE> | grep ingress-nginx`

Then go to any node in cluster on port  3XXXX which can be pulled from the svc description. http://<node>:<port>

You should see: 
default backend-404

This is because no ingress rules are currently defined so the default backend is hit.

# Creating your first Ingress

In order to create our first ingres rule we need to have a deployment and service to route the ingress rule to. To do so we will launch our sample nginx application and expose it using a clusterIP service.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx
  namesapce: <NAMESPACE>
spec:
  selector:
    matchLabels:
      run: nginx
  replicas: 2
  template:
    metadata:
      labels:
        run: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
---

apiVersion: v1
kind: Service
metadata:
   name: my-nginx
   labels:
     app: my-service
   namespace: <NAMESPACE>
spec:
   ports:
     - port: 9000
       targetPort: 80
   selector:
     run: nginx
```

Creating an ingress rule:

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: default-ingress
  namespace: <NAMESAPCE> 
  annotations:
    kubernetes.io/ingress.class: "<INGRESS-CLASS>"
spec:
  backend:
   serviceName: my-nginx 
   servicePort: 80
```

Now if we go to the same url we went to earlier and saw default backend we should see our familiar Welcome to Nginx screen.

# Creating an Ingress Rule that routes by hostname

To test the below we will create a DNS alias on optum navigator called nginx-k8s.optum.com and one called jupyter-k8s.optum.com both of which will route to our master FQDN.

We now need to spin up a jupyter notebook deployment and cluster ip service with the below:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jupyter-pod
  namespace: <NAMESPACE>
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
      containers:
      - name: jupyter-pod
        image: docker.optum.com/peds/retain-notebook 
        env:
        - name: USER
          value: "<MSID>"
        - name: USER_ID
          value: "<uid from id command>"
        - name: GROUP
          value: "<UNIX GROUP>"
        - name: GROUP_ID
          value: "<gid from id command>"
        - name: JUPYTERPWD
          value: "<custom password>"
        - name: PORT0
          value: "8898"
        resources:
         limits: 
          nvidia.com/gpu: 1
         requests:
           memory: 32Gi
           cpu: 16000m 
        securityContext:
          privileged: true
---

kind: Service
apiVersion: v1
metadata:
  labels:
    app: jupyter-pod
  name: jupyter
  namespace: <NAMESPACE>
spec:
  ports:
    - port: 9001
      targetPort: 8898
  selector:
    app: jupyter-pod
```

Now we simply create a rule like the below that will route to nginx if the hostname is nginx-k8s.optum.com and jupyter if the hostname is jupyter-k8s.optum.com.

```
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: nginx-ingress
  namespace: <NAMESPACE>
  annotations:
    kubernetes.io/ingress.class: "<INGRESS-CLASS>"
spec:
 rules: 
  - host: 'nginx-k8s.optum.com' 
    http:
     paths:
     - backend:
         serviceName: my-nginx
         servicePort: 80
  - host: 'jupyter-k8s.optum.com'
    http:
     paths:
     - backend:
         serviceName: jupyter
         servicePort: 8898 
```
Replace the 3XXXXX port with the port your ingress controller is running on.
Now go to your browser at http://nginx-k8s.optum.com:<3XXXXX> and you should see the nginx welcome page and then go to http://jupyter-k8s.optum.com:<3XXXXX> and you should see the login page for jupyter.

# Additional Annotations

To use secure(https) backends please add the annotation:
nginx.ingress.kubernetes.io/secure-backends: "true"

To rewrite the path of the uri when using path routing use:
nginx.ingress.kubernetes.io/rewrite-target: /

# Creating a TLS Endpoint
To create a TLS ingress rule you need to create a TLS secret in kubernetes to use with the ingress rule:
`kubectl create secret tls <SECRET NAME> --cert=/path/to/cert --key=/path/to/public/key

Then your ingress rule would look similar to:

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress
  namespace: <NAMESPACE> 
  annotations: 
   nginx.ingress.kubernetes.io/secure-backends: "true"
   kubernetes.io/ingress.class: "<INGRESS CLASS>"
spec:
 tls:
 - hosts:
   - nginx-k8s.optum.com
   secretName: <SECRET NAME>
 rules:
 - host: 'nginx-k8s.optum.com'
   http:
    paths:
    - backend:
       serviceName: my-nginx
       servicePort: 80
```
