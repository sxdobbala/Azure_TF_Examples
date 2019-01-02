get_authtoken() {
           AUTH_TOKEN=`curl -k -X POST "$L7AUTH_URL" \
           -d "grant_type=client_credentials&client_id=$CLIENT_KEY&client_secret=$CLIENT_SECRET" \
           | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
         }

install_spark() {
curl -k -X POST "$L7BASE_URL" \
 -d "@marathon.json.tmp" \
 -H "scope: oob" \
 -H "actor: default" \
 -H "timestamp: 10000" \
 -H "Content-type: application/json" \
 -H "Authorization: Bearer $AUTH_TOKEN"

if [ $? != 0 ]; then
let i=i+1
if [ $i -lt 5 ]; then
echo "Retrying Install"
install_spark
else
echo "Install Failed...Aborting"
exit 1
fi
fi
}

prepare_application(){
service_name=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
echo "Starting spark with service name" $service_name

echo "Preparing Marathon API Request"
sed "s/service.name/$service_name/g" marathon.json > marathon.json.tmp
sed -i "s/service.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['cpus']"`/g" marathon.json.tmp
sed -i "s/service.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['mem']"`/g" marathon.json.tmp
sed -i "s/service.docker-image/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['docker-image']"`/g" marathon.json.tmp
sed -i "s/service.role/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['role']"`/g" marathon.json.tmp
}

clean_temp(){
    rm ./marathon.json.tmp
}

PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH

source ./server.conf
echo "Mesos Cluster Name:" $cluster_name
echo "Submitting Framework through Layer7 API" $L7BASE_URL

get_authtoken
prepare_application

echo "JSON file for the service"
cat marathon.json.tmp

install_spark
clean_temp



