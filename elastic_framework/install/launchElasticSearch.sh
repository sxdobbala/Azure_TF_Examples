get_authtoken() {
           AUTH_TOKEN=`curl -k -X POST "$L7AUTH_URL" \
           -d "grant_type=client_credentials&client_id=$CLIENT_KEY&client_secret=$CLIENT_SECRET" \
           | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
         }

install_elastic() {
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
	install_elastic
	else
	echo "Install Failed...Aborting"
	exit 1
	fi
	fi
}

prepare_application(){
	service_name=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
	echo "Creating ES with service name" $service_name

	echo "Preparing Marathon API Request"
	sed "s/service.name/$service_name/g" marathon.json > marathon.json.tmp
	sed -i "s/service.user/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['user']"`/g" marathon.json.tmp
	sed -i "s/marathon-lb.port/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['marathon-lb']['port']"`/g" marathon.json.tmp
	sed -i "s/service.log_level/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['log_level']"`/g" marathon.json.tmp
	sed -i "s/service.security.transport_encryption.enabled/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['security']['transport_encryption']['enabled']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['cpus']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['mem']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.disk/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['disk']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.type/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['disk_type']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.transport_port/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['transport_port']"`/g" marathon.json.tmp
	#sed -i "s/master_nodes.placement/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['placement']"`/g" marathon.json.tmp
	sed -i "s/master_nodes.heap.size/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['master_nodes']['heap']['size']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.count/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['count']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['cpus']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['mem']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.disk/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['disk']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.type/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['disk_type']"`/g" marathon.json.tmp
	#sed -i "s/data_nodes.placement/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['placement']"`/g" marathon.json.tmp
	sed -i "s/data_nodes.heap.size/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['data_nodes']['heap']['size']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.count/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['count']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['cpus']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['mem']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.disk/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['disk']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.type/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['disk_type']"`/g" marathon.json.tmp
	#sed -i "s/ingest_nodes.placement/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['placement']"`/g" marathon.json.tmp
	sed -i "s/ingest_nodes.heap.size/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['ingest_nodes']['heap']['size']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.count/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['count']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.cpus/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['cpus']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.mem/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['mem']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.disk/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['disk']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.type/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['disk_type']"`/g" marathon.json.tmp
	#sed -i "s/coordinator_nodes.placement/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator']['placement']"`/g" marathon.json.tmp
	sed -i "s/coordinator_nodes.heap.size/`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['coordinator_nodes']['heap']['size']"`/g" marathon.json.tmp
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

install_elastic
clean_temp


