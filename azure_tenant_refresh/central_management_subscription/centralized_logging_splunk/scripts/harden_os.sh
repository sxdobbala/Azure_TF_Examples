#!/bin/bash -x

sudo sed -i "s/PermitRootLogin.*/PermitRootLogin no/g" /etc/ssh/sshd_config

APTGET="$(which apt-get 2>/dev/null)"
YUM="$(which yum 2>/dev/null)"

if [ -e "$APTGET" ]; then
  sudo service ssh restart
  sudo $APTGET update
  sudo $APTGET upgrade -y
elif [ -e "$YUM" ]; then
  sudo systemctl restart sshd
  sudo yum update -y
else
  echo "Cannot update OS"
  exit 255
fi
