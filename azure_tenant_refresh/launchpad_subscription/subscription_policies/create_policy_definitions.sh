#!/bin/sh

# Creates policy definitions

PROGNAME=$0
RC=0
policy_name=""

policy_name="audit_storage_firewall"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "Audit Storage Accounts without a Firewall" \
	--display-name "Audit Storage Accounts without a Firewall" \
	--rules "rules/$policy_name.json"
RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name='audit_unallowed_resources'
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits resources that are specifically not allowed." \
	--display-name "Audit unallowed resources" \
	--params '{
    			"listOfResourceTypesNotAllowed": {
        			"type": "array",
        			"metadata": {
            			"description": "The list of unallowed resource types.",
            			"displayName": "Not allowed resources types",
            			"strongType": "resourceTypes"
       				 }
    			}
			}' \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name='enforce_encryption_storage_account'
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "Enforce encryption storage account" \
	--display-name "Ensures file encryption for storage accounts" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="ensure_https_storage"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "Ensure https storage" \
	--display-name "Ensure https traffic only for storage account" \
	--rules  "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="non_gateway_subnets_require_nsg"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any non-gateway subnets that do not have an attached NSG." \
	--display-name "All non-gateway subnets require an attached NSG" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="audit_allow_rdp_inbound"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any inbound security rules on a NSG that allow rdp from the internet" \
	--display-name "Audit security rules that allow rdp inbound" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="audit_allow_ssh_inbound"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any inbound security rules on a NSG that allow ssh from the internet" \
	--display-name "Audit security rules that allow ssh inbound" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="audit_allow_sql_ports_inbound"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any inbound security rules on a NSG that allow sql ports from the internet" \
	--display-name "Audit security rules that allow sql ports inbound" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="audit_allow_all_ports_inbound"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any inbound security rules on a NSG that allow all ports (*) from the internet" \
	--display-name "Audit security rules that allow all ports inbound" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi

policy_name="audit_allow_port_ranges_inbound"
az policy definition create \
	--mode All  \
	--name $policy_name \
	--description "This policy audits any inbound security rules on a NSG that allow port ranges, i.e. 3000-4000" \
	--display-name "Audit security rules that allow port ranges inbound" \
	--rules "rules/$policy_name.json"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $policy_name
	fi