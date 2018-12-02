#!/bin/bash

# python, pip, flask

apt-get install -y mysql-client libmysqlclient-dev python3-pip unzip

pip3 install uwsgi flask flask-mysql

# app files

mkdir /app

cd /app
wget https://github.com/russjury/azure-vote-app-mysql-ssl/archive/master.zip
unzip master.zip
rm master.zip
mv azure-vote-app-mysql-ssl-master/* .
rmdir azure-vote-app-mysql-ssl-master

cat >/etc/default/votingapp <<'EOM'
MYSQL_USER="${db_username}"
MYSQL_PASSWORD="${db_password}"
MYSQL_DATABASE="${db_database}"
MYSQL_HOST="${db_host}"
MYSQL_PORT=3306
MYSQL_SSL_CA=/app/BaltimoreCyberTrustRoot.crt.pem
EOM
chmod 600 /etc/default/votingapp


# create database

mysql --user=${db_username} --password=${db_password} --host=${db_host} --ssl-mode=VERIFY_CA --ssl-ca=/app/BaltimoreCyberTrustRoot.crt.pem < /app/azurevote.sql


# service

cat >/etc/systemd/system/votingapp.service <<'EOM'
[Unit]
Description=Voting App
After=multi-user.target

[Service]
EnvironmentFile=-/etc/default/votingapp
ExecStart=/usr/bin/python3 /app/main.py
#KillMode=process
Type=idle

[Install]
WantedBy=multi-user.target
EOM

systemctl daemon-reload
systemctl enable votingapp
systemctl start votingapp

