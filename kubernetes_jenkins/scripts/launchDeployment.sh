launch_deployment() {
#curl -X POST -k -H "Content-Type: application/yaml" -H "Authorization: Bearer $JWT" "https://$host:$port/apis/apps/v1/namespaces/$namespace/deployments" -d "@deployment.yaml"
#curl -X DELETE -k -H "Content-Type: application/yaml" -H "Authorization: Bearer $TOKEN" --data-binary @../jenkins-nginx-nodeport.yaml "https://$host:$port/api/v1/namespaces/$namespace/services"
#curl -X DELETE -k -H "Content-Type: application/yaml" -H "Authorization: Bearer $TOKEN" --data-binary @../deployment.yaml "https://$host:$port/apis/apps/v1/namespaces/$namespace/deployments"

curl -X POST -k -H "Content-Type: application/yaml" -H "Authorization: Bearer $TOKEN" --data-binary @../jenkins-nginx-nodeport.yaml "https://$host:$port/api/v1/namespaces/$namespace/services"
curl -X POST -k -H "Content-Type: application/yaml" -H "Authorization: Bearer $TOKEN" --data-binary @../deployment.yaml "https://$host:$port/apis/apps/v1/namespaces/$namespace/deployments"
if [ $? != 0 ]; then
let i=i+1
if [ $i -lt 5 ]; then
echo "Retrying Install"
launch_deployment
else
echo "Install Failed...Aborting"
exit 1
fi
fi
}

source ../deployment.conf
launch_deployment
if [ $? != 0 ]; then
exit 1
fi

