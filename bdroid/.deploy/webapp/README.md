WebApp
======

Dockerize Angular WebApp

### Deploy

#### Build Nginx
> Build and publish custom nginx image for OpenShift (One time only)
```bash
docker build -t openshift-nginx -f .deploy/webapp/nginx.dockerfile .
docker tag openshift-nginx xmlking/openshift-nginx:1.14-alpine
docker push xmlking/openshift-nginx

# also tag as `latest` and push
docker tag xmlking/openshift-nginx:1.14-alpine xmlking/openshift-nginx:latest
docker push xmlking/openshift-nginx:latest
```

#### Build App
> Build bdroid docker image
```bash
# build app docker image
docker build --tag=bdroid -f .deploy/webapp/prod.dockerfile . 
```

#### Docker Push
> Push bdroid docker image
```bash
# login to hub.docker.com to push docker image
docker login

# tag  
docker tag bdroid xmlking/bdroid:1.0.0-SNAPSHOT
docker tag xmlking/bdroid:1.0.0-SNAPSHOT  xmlking/bdroid:latest

# push
docker push xmlking/bdroid:1.0.0-SNAPSHOT
docker push xmlking/bdroid:latest
```

#### OpenShift Deployment
> Deploy bdroid app to OpenShift
```bash
# login with your ID
oc login <my OpenShift URL>
# oc login  https://console.starter-us-west-1.openshift.com
oc project bdroid
cd .deploy/webapp

# create app (first time deployment)
oc new-app -f webapp.tmpl.yml -p APPNAME=webapp -n bdroid

# follow next steps if you want completely delete and deploy.
# delete only deploymentConfig
oc delete all -l app=webapp -n cockpit

# delete fully
oc delete all,configmap,secret -l app=webapp -n bdroid

# redeploy
from OpenShift Console UI, 
Applications > Deployments > webapp > Deploy 
```

 
### K8S Commands
```bash
kubectl get services
kubectl get pods
kubectl logs -f  keycloak-server-6-grfmg
```

### Run 
Run docker locally for testing.
```bash
docker run -it  -p 4200:8080 -v .deploy/webapp/nginx.conf:/etc/nginx/conf.d/nginx.conf cockpit
```

The app will be available at http://localhost:4200

You can tweak the nginx config  ```nginx.conf``` for your specific needs.

### SSH
```bash
# SSH to the running container (CONTAINER ID from `docker ps` command)
docker exec -it <CONTAINER ID> sh
# if you started via docker-compose
docker-compose exec web sh
```

###Maintenance
```bash
docker container prune
docker image prune
```


### Ref
* If you get 137 error
  * https://samwize.com/2016/05/19/docker-error-returned-a-non-zero-code-137/
  * https://docs.docker.com/docker-for-mac/#advanced
