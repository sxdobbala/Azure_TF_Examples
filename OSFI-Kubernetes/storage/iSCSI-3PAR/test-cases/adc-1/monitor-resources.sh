kubectl describe pv iscsi-pv-chap-lun
kubectl describe pvc iscsi-pvc-chap-lun -n ns-iscsi1
kubectl get po -n ns-iscsi1 -o wide
