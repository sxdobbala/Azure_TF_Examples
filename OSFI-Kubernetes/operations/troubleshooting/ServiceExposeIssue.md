
## Issue was reported as Node Port is not working.

```bash

1. Created deployment YAML file:  simple-deploy.yml
2. Ran $ kubectl apply -f simple-deploy.yml

$ cat simple-deploy.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: westeros-deploy
spec:
  replicas: 3
  minReadySeconds: 10
  selector:
    matchLabels:
      app: paulsapp
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    metadata:
      labels:
        app: paulsapp
    spec:
      containers:
      - name: paulsapp
        image: docker.optum.com/mydatastudio/nodek8sapp:latest
        ports:
        - containerPort: 8080
plopez10@dbslp1748.uhc.com:/home/plopez10
$ kubectl apply -f simple-deploy.yml
deployment.apps "westeros-deploy" created

3. Ran $ kubectl get pods
$ kubectl get pods
NAME                               READY     STATUS    RESTARTS   AGE
westeros-deploy-5dcddc74f6-95wzm   1/1       Running   0          1m
westeros-deploy-5dcddc74f6-9sjfr   1/1       Running   0          1m
westeros-deploy-5dcddc74f6-qzh2g   1/1       Running   0          1m

4. Ran $ kubectl expose deployment westeros-deploy --type=NodePort --name=mynodeport
$ kubectl expose deployment westeros-deploy --type=NodePort --name=mynodeport
service "mynodeport" exposed

5. Ran $ kubectl get pods --selector="app=paulsapp" --output=wide

$ kubectl get pods --selector="app=paulsapp" --output=wide
NAME                               READY     STATUS    RESTARTS   AGE       IP             NODE
westeros-deploy-5dcddc74f6-95wzm   1/1       Running   0          4m        10.244.7.54    ctc2hz1-01-s12.uhc.com
westeros-deploy-5dcddc74f6-9sjfr   1/1       Running   0          4m        10.244.4.118   ctc2hz1-01-s13.uhc.com
westeros-deploy-5dcddc74f6-qzh2g   1/1       Running   0          4m        10.244.8.30    ctc2hz1-01-s09.uhc.com

6. $ kubectl get services
$ kubectl get services
NAME         TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
mynodeport   NodePort   10.104.251.237   <none>        8080:31416/TCP   2m

7. Browse to ctc2hz1-01-s12.uhc.com:31416
Firefox error: Unable to connect
Safari error: Safari cannot connect to the server

Note: I deployed the same image in Openshift and it works file:  http://simple-spa-mydatastudio-ara.ocp-ctc-core-nonprod.optum.com/

```

To troubleshoot this issue, first describe the node port service and make sure endpoints are registered.

kubectl describe svc mynodeport -n westeros

if end points are not registered then check:
  the selectors are matching with 


