
Start with 

Kubectl get pods --all-namespaces -o wide
kubectl get pods -n <namespace> -o wide

Check the events, container id, messages using describe

kubectl describe pod <pod> -n <namespace>
