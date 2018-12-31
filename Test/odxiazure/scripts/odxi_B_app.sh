#!/bin/bash

## Make Directories ##
mkdir /hs_install/

## Install PIP and AWS
curl -O https://bootstrap.pypa.io/get-pip.py
python get-pip.py --user
export PATH=~/.local/bin:$PATH
source ~/.bash_profile
pip install awscli --upgrade --user
aws s3 cp s3://andries-odxi-testing/manifest_test/Install_app.sh /hs_install/
sh /hs_install/Install_app.sh ODXIAPPSERVERB >> output.log
