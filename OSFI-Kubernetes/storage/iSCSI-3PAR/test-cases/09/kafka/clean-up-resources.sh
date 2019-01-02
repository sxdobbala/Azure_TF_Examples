kubectl delete pvc datadir-kafka-0 -n ns-iscsi1
kubectl delete pvc datadir-kafka-1 -n ns-iscsi1
kubectl delete pvc datadir-kafka-2 -n ns-iscsi1
kubectl delete -f yamls
