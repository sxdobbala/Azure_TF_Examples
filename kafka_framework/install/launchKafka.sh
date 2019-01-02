get_authtoken() {
           AUTH_TOKEN=`curl -k -X POST "$L7AUTH_URL" \
           -d "grant_type=client_credentials&client_id=$CLIENT_KEY&client_secret=$CLIENT_SECRET" \
           | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
         }
         
install_kafka() {
curl -k -X POST "$L7BASE_URL" \
             -d "@marathon.json.tmp" \
             -H "scope: oob" \
             -H "actor: default" \
             -H "timestamp: 10000" \
             -H "Content-Type:application/json" \
             -H "Authorization: Bearer $AUTH_TOKEN"

     if [ $? != 0 ]; then
     let i=i+1
     if [ $i -lt 5 ]; then
     echo "Retrying Install"
     install_kafka
     else
     echo "Install Failed...Aborting"
     exit 1
     fi
     fi
}

prepare_application(){
        service_name=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
        count=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['count']"`
        echo "Starting kafka with" $count" brokers and service name" $service_name

echo "Preparing Marathon API Request"
sed "s/service.name/$service_name/g" marathon.json > marathon.json.tmp
sed -i "s/service.user/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['user']"`/g" marathon.json.tmp
sed -i "s/service.placement_constraint/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['placement_constraint']"`/g" marathon.json.tmp
sed -i "s/brokers.count/$count/g" marathon.json.tmp
sed -i "s/brokers.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['cpus']"`/g" marathon.json.tmp
sed -i "s/brokers.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['mem']"`/g" marathon.json.tmp
sed -i "s/brokers.disk_type/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['disk_type']"`/g" marathon.json.tmp
sed -i "s/brokers.disk/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['disk']"`/g" marathon.json.tmp
sed -i "s/kafka.offsets_topic_replication_factor/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['kafka']['offsets_topic_replication_factor']"`/g" marathon.json.tmp
sed -i "s/kafka.confluent_metrics_reporter_topic_replicas/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['kafka']['confluent_metrics_reporter_topic_replicas']"`/g" marathon.json.tmp
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

install_kafka
clean_temp




