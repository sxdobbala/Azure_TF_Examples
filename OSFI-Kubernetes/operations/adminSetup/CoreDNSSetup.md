# Core DNS
Kubernetes is making a shift from Kube DNS to Core DNS as it is a more optimized DNS application. Reasons as to why this is the case can be found
in the [official proposal](https://github.com/johnbelamaric/community/blob/7d3bbcee83e5d0417ba241ea830f0108741e0c85/contributors/design-proposals/network/coredns.md). 

# Setting up Core DNS
To install Core DNS we need to download the deploy.sh and coredns.yaml.sed from the [official github](https://github.com/coredns/deployment/tree/master/kubernetes).

We then simply run: 

```
$ ./deploy.sh | kubectl apply -f -
$ kubectl delete --namespace=kube-system deployment kube-dns
```


# Confirming it is up

In order to verify core DNS is running run the below and verify you see 2 core dns pods running and 0 kube dns pods:

```
# kubectl get po -n kube-system | grep dns
coredns-dccf866f7-n8xh6                     1/1       Running   0          15d
coredns-dccf866f7-sk74s                     1/1       Running   0          15d

```

Once core dns is up and kube dns is down. Attempt to contact a pod via dns. 

`nslookup <service_name>.<namespace>.svc.cluster.local`

Assuming it resolves core dns is up and running

Additionally to get the pod:

`<port_name>.<port_protocol>.<service_name>.<namespace>.svc.cluster.local

# Upgrading in Kubernetes 1.10+
In 1.10 you can upgrade directly using kubeadm which will use kube dns's config map to preserve configurations. 

[More Info](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)
# New Install in Kubernetes 1.9+
If you are setting up a kubernetes cluster with 1.9 or 1.10 you can enable the feature gate with kubeadm and it will set up core dns for you instead of kube dns with the below command: 

`kubeadm init --feature-gates=CoreDNS=true`




