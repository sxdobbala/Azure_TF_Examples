PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH

source ../install/server.conf
service_name=`cat ../install/options.json | python -c "import sys, json; print json.load(sys.stdin)['service']['name']"`
count=`curl -s -XGET $master/marathon/v2/apps | grep $service_name | wc -l`
while [ $count -ne 0 ]
do
sleep 10
count=`curl -s -XGET $master/marathon/v2/apps | grep $service_name | wc -l`
echo "Uninstalling..."
done
if [ $count -eq 0 ]
then
echo "App removed successfully id: $service_name"
fi
echo "Uninstall complete"
