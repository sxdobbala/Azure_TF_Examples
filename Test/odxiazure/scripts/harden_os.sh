#!/bin/bash -x
#
# THIS IS AN EXAMPLE OF WHAT COULD BE DONE, ONLY.  IT IS NOT AN ACTUAL 'OS HARDENING' SCRIPT.
#

sudo sed -i "s/PermitRootLogin.*/PermitRootLogin no/g" /etc/ssh/sshd_config

APTGET="$(which apt-get 2>/dev/null)"
YUM="$(which yum 2>/dev/null)"

if [ -e "$APTGET" ]; then
  #sudo apt full-upgrade -y
  sudo service ssh restart
elif [ -e "$YUM" ]; then
  sudo yum update -y
  sudo systemctl restart sshd
fi
