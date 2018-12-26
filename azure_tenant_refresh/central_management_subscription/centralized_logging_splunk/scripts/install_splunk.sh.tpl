#!/bin/bash
#
# This script must be executed by root.

SPADMUSER="${spadmuser}"
SPADMPASS="${spadmpass}"

export SPLUNK_HOME="/opt/splunk"

TGZ_FILE="splunk-7.1.1-8f0ead9ec3db-Linux-x86_64.tgz"
ADDON_SPL_FILE="TA-Azure_Monitor_1_2_8.spl"
CREDS_SPL_FILE="splunkclouduf.spl"

#############################################################################################

FILE_DIR="/tmp"

# create splunk group/user
groupadd -r splunk
useradd -rmg splunk -d $${SPLUNK_HOME} splunk

# download/install Node.js 6.x repo
#curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -

# install Node.js
apt-get install -y nodejs npm

# install splunk tgz
cd $${SPLUNK_HOME}
tar --strip-components=1 -xzf $${FILE_DIR}/$${TGZ_FILE}
chown -R splunk:splunk $${SPLUNK_HOME}

# create user seed file
touch $${SPLUNK_HOME}/etc/system/local/user-seed.conf
chown splunk:splunk $${SPLUNK_HOME}/etc/system/local/user-seed.conf
chmod 0600 $${SPLUNK_HOME}/etc/system/local/user-seed.conf
cat > $${SPLUNK_HOME}/etc/system/local/user-seed.conf <<EOM
[user_info]
USERNAME = $${SPADMUSER}
PASSWORD = $${SPADMPASS}
EOM

# accept license and enable splunk at boot time
$${SPLUNK_HOME}/bin/splunk enable boot-start -user splunk --accept-license
systemctl start splunk

# install Azure add-on (https://github.com/Microsoft/AzureMonitorAddonForSplunk)
su - splunk -c "$${SPLUNK_HOME}/bin/splunk install app $${FILE_DIR}/$${ADDON_SPL_FILE} -auth $${SPADMUSER}:$${SPADMPASS}"
ERR=$?
if [ $ERR -gt 0 ]; then
  echo ERROR installing $${ADDON_SPL_FILE}
fi

# install credentials file for splunk cloud
su - splunk -c "$${SPLUNK_HOME}/bin/splunk install app $${FILE_DIR}/$${CREDS_SPL_FILE} -auth $${SPADMUSER}:$${SPADMPASS}"
ERR=$?
if [ $ERR -gt 0 ]; then
  echo ERROR installing $${CREDS_SPL_FILE}
fi

# install dependencies
cd $${SPLUNK_HOME}/etc/apps/TA-Azure_Monitor/bin
bash linux_dependencies.sh

# install node modules in the add-on's app folder (after add-on SPL is installed)
cd $${SPLUNK_HOME}/etc/apps/TA-Azure_Monitor/bin/app
npm install

# reset file owners one last time
chown -R splunk:splunk $${SPLUNK_HOME}

# restart splunk
$${SPLUNK_HOME}/bin/splunk restart


# cleanup
rm -f $${SPLUNK_HOME}/etc/system/local/user-seed.conf
rm $${FILE_DIR}/$${TGZ_FILE}
rm $${FILE_DIR}/$${CREDS_SPL_FILE}
rm $${FILE_DIR}/$${ADDON_SPL_FILE}

echo " "
echo "******************************************"
echo "***                                    ***"
echo "***   Splunk Install/Config Finished   ***"
echo "***                                    ***"
echo "***                                    ***"
echo "******************************************"
echo " "
