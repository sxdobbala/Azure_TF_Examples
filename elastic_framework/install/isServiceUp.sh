get_authtoken() {
           AUTH_TOKEN=`curl -s -k -X POST "$L7AUTH_URL" \
           -d "grant_type=client_credentials&client_id=$CLIENT_KEY&client_secret=$CLIENT_SECRET" \
           | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
}
get_status(){
status=`curl -s -I -XGET "$L7BASE_URL/$service_name" \
             -H "scope: oob" \
             -H "actor: default" \
             -H "timestamp: 10000" \
             -H "Content-Type:application/json" \
             -H "Authorization: Bearer $AUTH_TOKEN" | grep -i "HTTP/1.1 200 OK" | wc -l`
}

service_name=`cat options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"` 
PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH
source ./server.conf
sleep 60
get_authtoken
get_status

echo "Deploying service...please wait "

if [ $status -eq 0 ]; then
  echo "Elastic Search is up & Running"
else
  echo "Elastic Search not running"
fi

