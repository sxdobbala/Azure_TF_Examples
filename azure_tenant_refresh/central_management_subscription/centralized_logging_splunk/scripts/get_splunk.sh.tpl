#!/bin/bash

# make sure this doesn't begin with a '?' - Azure Portal stupidly includes this...
SAS_TOKEN="${sas_token}"

FILE_URL="https://optumcc.blob.core.windows.net/splunk"
FILES="splunk-7.1.1-8f0ead9ec3db-Linux-x86_64.tgz TA-Azure_Monitor_1_2_8.spl splunkclouduf.spl"
FILE_DIR="/tmp"

echo "Getting Splunk binaries and supporting files..."

for FILE in $FILES; do
  wget -qO $FILE_DIR/$FILE "$FILE_URL/$FILE?$${SAS_TOKEN}"
done

echo "done"

echo "Checking binaries..."

RETURN=0

for FILE in $FILES; do
  size=$(stat -t $FILE_DIR/$FILE | awk '{print $2}')
  if [ $size -lt 10 ]; then
    echo "Error downloading $FILE.  Check filename, file url, and make sure sas token is still valid."
    RETURN=$(($RETURN + 1))
  fi
done

exit $RETURN
