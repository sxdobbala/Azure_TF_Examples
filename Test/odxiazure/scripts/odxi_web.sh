#!/bin/bash

## Start of Script
sudo yum update -y
sudo yum install httpd -y

## Create Cacheusr credentials ##
echo 0 > /sys/fs/selinux/enforce
useradd cacheusr; echo cioxAdmin123|passwd --stdin cacheusr

## Make Directories ##
mkdir /hs_install/
mkdir -p /cachesys/mgr
mkdir -p /ocie/certificates/public
mkdir -p /ocie/certificates/root_ca
mkdir -p /ocie/scripts

## Install PIP and AWS
curl -O https://bootstrap.pypa.io/get-pip.py
python get-pip.py --user
export PATH=~/.local/bin:$PATH
source ~/.bash_profile
pip install awscli --upgrade --user

## Transfer items from S3 ##
aws s3 cp s3://andries-odxi-testing/HS-2017.1.1.111.0.17521-hscore15.03_hsaa15.03_hspi15.03_hsviewer15.03_linkage15.03-b8382-lnxrhx64.tar.gz /hs_install/
aws s3 cp s3://andries-odxi-testing/cache.key /cachesys/mgr
aws s3 cp s3://andries-odxi-testing/cinstall_silent_ws.sh /hs_install/
aws s3 cp s3://andries-odxi-testing/index.html /ocie/scripts
aws s3 cp s3://andries-odxi-testing/httpd.conf /ocie/scripts
aws s3 cp s3://andries-odxi-testing/CSP.ini /ocie/scripts

## Clean up the install directory and untar files ##
cd /hs_install/
tar --strip-components=1 -zxvfHS-2017.1.1.111.0.17521-hscore15.03_hsaa15.03_hspi15.03_hsviewer15.03_linkage15.03-b8382-lnxrhx64.tar.gz

## Add user group(s) ##
groupadd odxidev
usermod -g "odxidev" cacheusr
usermod -aG wheel cacheusr

## Adjust owner and permissions of required folders ##
cd /
chown cacheusr cachesys
chown cacheusr hs_install
chown cacheusr ocie
chgrp odxidev cachesys
chgrp odxidev hs_install
chgrp odxidev ocie
#newgrp odxidev
#echo "newgroup odxidev"

## Run Cache Installation with the following choices as shown below ##
cd /hs_install
sudo -u cacheusr bash << EOF
echo "cioxAdmin123" | sudo -S bash cinstall_silent_ws.sh ODXIWEBSERVER cioxAdmin123;
EOF

## Move the "prepared" CSP.ini, cache.cpf, and httpd conf files to their appropriate directories ##
cd /ocie/scripts/
sudo \cp CSP.ini /opt/cspgateway/bin/
echo "moved CSP.ini in place"
sudo \cp index.html /var/www/html/
echo "moved index.html in place"
sudo \cp httpd.conf /etc/httpd/conf/
echo "moved httpd.conf in place"

## Setup the required CSP Gateway Permissions ##
chown apache /opt/cspgateway/bin/*
chmod 750 /opt/cspgateway/bin/*
chmod 660 /opt/cspgateway/bin/CSP.ini
chmod 640 /opt/cspgateway/bin/CSP.log
chown apache /opt/cspgateway/bin/CSP.ini
chgrp odxidev /opt/cspgateway/bin/CSP.ini
echo "changed ownerships"

## Start up Apache and make sure it turns on automatically ##
service httpd start
chkconfig httpd on
sudo echo 0 > /sys/fs/selinux/enforce
