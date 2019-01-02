kubectl describe pv iscsi-pv-chap-lun-7
kubectl describe pvc iscsi-pvc-chap-lun-7 -n ns-iscsi1
kubectl get svc  -n ns-iscsi1 
kubectl get po -n ns-iscsi1 -o wide
