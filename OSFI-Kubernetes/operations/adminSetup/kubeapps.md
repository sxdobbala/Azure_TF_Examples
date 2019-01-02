# Deploying Kubeapps

## Helm 

This is a cluster wide helm, make sure it is secure

### Create a new service account
TODO - reason?

kubectl create sa -n kube-system tiller

kubectl create clusterrolebinding tiller --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

### Setup SSL

https://github.com/helm/helm/blob/master/docs/tiller_ssl.md

helm init --service-account=tiller  --tiller-tls --tiller-tls-cert ./tiller.cert.pem --tiller-tls-key ./tiller.key.pem --tiller-tls-verify --tls-ca-cert ca.cert.pem

helm version --tls

## Chart repo

Create the PV to store charts

Create below deployment

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: optum-helm-charts
spec:
  selector:
    matchLabels:
      app: optum-helm-charts
  replicas: 1
  template:
    metadata:
      labels:
        app: optum-helm-charts
    spec:
      securityContext:
        runAsUser: 60217
        fsGroup: 53134
      containers:
      - name: optum-helm-charts
        image: bitnami/nginx
        resources:
         limits:
           memory: 8Gi
           cpu: 2
         requests:
           memory: 4Gi
           cpu: 1
        ports:
        - containerPort: 8080
        volumeMounts:
        - mountPath: /app
          name: mapr
      volumes:
      - name: mapr
        persistentVolumeClaim:
          claimName: pvc-mapr-dlpoc-charts

---
apiVersion: v1
kind: Service
metadata:
  name: optum-helm-charts-svc
  labels:
    app: optum-helm-charts
spec:
  type: NodePort
  ports:
  - port: 8080
    nodePort: 31080
  selector:
    app: optum-helm-charts
    
```

## Kubeapps 

kubectl create ns kubeapps
helm install --name kubeapps ./kubeapps-0.1.0.tgz -f config-kubeapps.yaml --namespace kubeapps --tls

```
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: kubeapps-repositories-read-all
  namespace: kubeapps
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: kubeapps-repositories-read
subjects:
- apiGroup: rbac.authorization.k8s.io
  kind: Group
  name: system:authenticated

```
## Depoying New charts
