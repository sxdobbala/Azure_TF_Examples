framework_cleaner() {
curl -s -X POST "$master/marathon/v2/apps" \
 -d "@janitor.json" \
-H "Content-type: application/json"
if [ $? != 0 ]; then
let i=i+1
if [ $i -lt 5 ]; then
framework_cleaner
else
exit 1
fi
fi
}
PATH=/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
export PATH

source ../install/server.conf

i=0
framework_cleaner
if [ $? != 0 ]; then
exit 1
fi
count=`curl -s -XGET "$master/marathon/v2/apps" | grep janitor | wc -l`
while [ $count -ne 0 ]
do
sleep 10
count=`curl -s -XGET "$master/marathon/v2/apps" | grep janitor | wc -l`
echo "Cleaning Framework..."
done
echo "Framework Cleaned"
