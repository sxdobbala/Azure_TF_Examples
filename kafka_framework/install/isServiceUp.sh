get_authtoken() {
           AUTH_TOKEN=`curl -s -k -X POST "$L7AUTH_URL" \
           -d "grant_type=client_credentials&client_id=$CLIENT_KEY&client_secret=$CLIENT_SECRET" \
           | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
}

get_broker_count(){
brokerCount=`curl -s -k -X GET "$L7BASE_URL/$service_name/v1/brokers" \
             -H "scope: oob" \
             -H "actor: default" \
             -H "timestamp: 10000" \
             -H "Content-Type:application/json" \
             -H "Authorization: Bearer $AUTH_TOKEN" | grep -v "wait a bit and try again" | wc -l`
}

get_broker_endpoints(){
brokerE=`curl -s -k -XGET "$L7BASE_URL/$service_name/v1/endpoints/broker" \
             -H "scope: oob" \
             -H "actor: default" \
             -H "timestamp: 10000" \
             -H "Content-Type:application/json" \
             -H "Authorization: Bearer $AUTH_TOKEN"`
}
get_zookeeper(){
echo "Zookeeper" `curl -s -k -X GET $L7BASE_URL/$service_name/v1/endpoints/zookeeper \
             -H "scope: oob" \
             -H "actor: default" \
             -H "timestamp: 10000" \
             -H "Content-Type:application/json" \
             -H "Authorization: Bearer $AUTH_TOKEN"`
}

service_name=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
count=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['brokers']['count']"`

PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH
let count=$count+2
source ./server.conf
sleep 60
get_authtoken
get_broker_count

while [ $brokerCount -ne $count ]
do
sleep 10
get_broker_count
let c=$brokerCount-2
if [ $c -lt 0 ]; then
c=0
fi
echo $c "Broker(s) up"
done

get_broker_endpoints

dns=`echo $brokerE |  python -c "import sys, json; print json.load(sys.stdin)['dns']" |  sed "s/u//g"`
address=`echo $brokerE | python -c "import sys, json; print json.load(sys.stdin)['address']" | sed "s/u//g"`
vip=`echo $brokerE | python -c "import sys, json; print json.load(sys.stdin)['vip']" | sed "s/u//g"`
echo "dns servers" $dns
echo "addresses" $address
echo "vip" $vip
get_zookeeper
