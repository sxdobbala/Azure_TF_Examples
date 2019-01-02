PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH

source ../install/server.conf

service_name=`cat ../install/options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
uninstall_command=`curl -s -X POST $master/package/uninstall \
 -H "accept: application/vnd.dcos.package.uninstall-response+json;charset=utf-8;version=v1" \
 -H "content-type: application/vnd.dcos.package.uninstall-request+json;charset=utf-8;version=v1" \
 -d "{ \"packageName\": \"confluent-kafka\",  \"appId\": \"/$service_name\" }" > response.json.tmp`

eval "$uninstall_command"
response=`cat response.json.tmp | python -c "import sys, json; print json.load(sys.stdin)['results']" | wc -l`
echo $response

while [ $response -ne 1 ]; 
do

cat response.json.tmp | python -c "import sys, json; print json.load(sys.stdin)['type']"
eval "$uninstall_command"
response=`cat response.json.tmp | python -c "import sys, json; print json.load(sys.stdin)['results']" | wc -l`
echo $response
sleep 10

done

rm response.json.tmp
