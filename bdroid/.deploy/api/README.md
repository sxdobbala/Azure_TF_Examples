API
===
Deploying bdroid API

### Build
```bash
# build app docker image
docker build --tag=bdroid-api -f .deploy/api/prod.dockerfile . 
```

### Run
```bash
docker run -it --env TYPEORM_HOST=localhost -p 3000:3000  bdroid-api
```

### Test

> The app will be available at http://localhost:3000

```bash
# test
curl -v -X GET \
  http://localhost:3000/myapi/tenant \
| jq .
```


### Deploy

#### Docker Push
```bash
# login to hub.docker.com to push docker image
docker login

# tag
docker tag bdroid-api xmlking/bdroid-api:0.1.4-SNAPSHOT
docker tag xmlking/bdroid-api:0.1.4-SNAPSHOT  xmlking/bdroid-api:latest

# push
docker push xmlking/bdroid-api:0.1.4-SNAPSHOT
docker push xmlking/bdroid-api:latest
```

#### OpenShift Deployment
> Deploy bdroid-api to OpenShift

```bash
# login
oc login <my OpenShift URL>
# oc login  https://console.starter-us-west-1.openshift.com
oc project bdroid
cd .deploy/api

# create app (first time deployment)
oc new-app -f api.tmpl.yml -p APPNAME=bdroid-api -n bdroid

# follow next steps if you want completely delete and deploy.
# delete only deploymentConfig
oc delete all -l app=bdroid-api -n bdroid

# delete fully
oc delete all,configmap,secret -l app=bdroid-api -n bdroid

# redeploy
# From OpenShift Console UI
Applications > Deployments > bdroid > Deploy 
```
 
#### Kubernetes Deployment
> assume you already setup `bdroid` context

> make sure  `Env`, docker image `Version` are correct in `api.yml`

```bash
## view all preset contexts
kubectl config get-contexts
# switch to `bdroid` contexts
kubectl config use-context bdroid

## create (first time deployment)
kubectl create -f ./api.yml
kubectl describe deployment api

## checking
# see logs
kubectl logs -f my-pod
# Once you’ve created a Service of type NodePort, you can use this command to find the NodePort
kubectl get service api --watch
# to get <NODE> names 
kubectl get pod  -o wide

## delete
kubectl delete -f ./api.yml

## redeploy (new image)
update tag in api.yml and delete and create again.
```

#### Kubernetes Commands 
```bash
kubectl get deployment -o wide
kubectl get pods -o wide
kubectl get service -o wide

kubectl exec -it my-pod  -- /bin/bash
kubectl logs -f my-pod
kubectl logs my-pod --previous 
kubectl logs my-pod -c my-container
```


