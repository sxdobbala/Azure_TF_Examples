kubectl describe pv iscsi-pv-chap-lun-0
kubectl describe pvc iscsi-pvc-chap-lun-0 -n ns-iscsi1
kubectl get po -n ns-iscsi1 -o wide
