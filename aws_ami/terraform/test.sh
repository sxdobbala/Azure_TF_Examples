#/bin/bash

function super_curl(){
	local url=$1
	local file="${url##*/}"
	if ! curl -f --ipv4 -Lo "${file}" --connect-timeout 20 --retry 5 --retry-delay 10 "${url}"; then
        echo "== Failed to download ${url}. Retrying. =="
    else
          echo "== Downloaded ${file} =="
    fi
}

# Install SSM agent. 
super_curl "https://amazon-ssm-us-east-1.s3.amazonaws.com/latest/linux_amd64/amazon-ssm-agent.rpm"