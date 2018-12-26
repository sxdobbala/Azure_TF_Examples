#!/bin/sh

# Creates policy initiatives

PROGNAME=$0
SubID=$1
RC=0
sec=""

# Policy Umbrella (non-critical)

sec="Create policy initiative"
az policy set-definition create \
	--name "umbrella_policy_initiative" \
	--display-name "Optum Policy Umbrella" \
	--description "Policy Umbrella with built-in and custom policies"  \
	--definitions '[
			{
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/06a78e20-9358-41c9-923c-fb736d382a4d"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/0961003e-5a0a-4549-abde-af6a37f2724d"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/17k78e20-9358-41c9-923c-fb736d382a12"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/201ea587-7c90-41c3-910f-c280ae01cfd6"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/44452482-524f-4bf4-b852-0bff7cc4a3ed"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/47a6b606-51aa-4496-8bb7-64b11cf66adc"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/760a85ff-6162-42b3-8d70-698e268f648c"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/a7ff3161-0087-490a-9ad9-ad6217f4f43a"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/abcc6037-1fc4-47f6-aac5-89706589be24"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/af6cd1bd-1635-48cb-bde7-5b15693900b9"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/af8051bf-258b-44e2-a2bf-165330459f9d"
	    },
	    {
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/e1e5fd5d-3e4c-4ce1-8661-7d1873ae6b15"
	    },
	    {
	        "parameters": {
	            "listOfAllowedLocations": {
	                "value": [
	                    "centralus",
	                    "eastus",
	                    "eastus2",
	                    "northcentralus",
	                    "southcentralus",
	                    "westcentralus",
	                    "westus",
	                    "westus2"
	                ]
	            }
	        },
	        "policyDefinitionId": "/providers/Microsoft.Authorization/policyDefinitions/e56962a6-4747-49cd-b67b-bf8b01975c4c"
	    },
	    {
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/enforce_encryption_storage_account"
	    },
	    {
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/ensure_https_storage"
	    },
	    {
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_storage_firewall"
	    },
	    {
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/non_gateway_subnets_require_nsg"
	    },
	    {
	        "parameters": {
	            "listOfResourceTypesNotAllowed": {
	                "value": [
	                     "Microsoft.ClassicCompute/capabilities",
	                     "Microsoft.ClassicCompute/checkDomainNameAvailability",
	                     "Microsoft.ClassicCompute/domainNames",
	                     "Microsoft.ClassicCompute/domainNames/slots",
	                     "Microsoft.ClassicCompute/domainNames/slots/roles",
	                     "Microsoft.ClassicCompute/moveSubscriptionResources",
	                     "Microsoft.ClassicCompute/operatingSystemFamilies",
	                     "Microsoft.ClassicCompute/operatingSystems",
	                     "Microsoft.ClassicCompute/operatingStatuses",
	                     "Microsoft.ClassicCompute/quotas",
	                     "Microsoft.ClassicCompute/resourceTypes",
	                     "Microsoft.ClassicCompute/validateSubscriptionMoveAvailability",
	                     "Microsoft.ClassicCompute/virtualMachines",
	                     "Microsoft.ClassicCompute/virtualMachines/diagnosticSettings",
	                     "Microsoft.ClassicCompute/virtualMachines/metricDefinitions",
	                     "Microsoft.ClassicCompute/virtualMachines/metrics",
	                     "Microsoft.ClassicInfrastructureMigrate/classicInfrastructureResources",
	                     "Microsoft.ClassicNetwork/capabilities",
	                     "Microsoft.ClassicNetwork/gatewaySupportedDevices",
	                     "Microsoft.ClassicNetwork/networkSecurityGroups",
	                     "Microsoft.ClassicNetwork/operations",
	                     "Microsoft.ClassicNetwork/quotas",
	                     "Microsoft.ClassicNetwork/reservedIps",
	                     "Microsoft.ClassicNetwork/virtualNetworks",
	                     "Microsoft.ClassicNetwork/virtualNetworks/remoteVirtualNetworkPeeringProxies",
	                     "Microsoft.ClassicNetwork/virtualNetworks/virtualNetworkPeerings",
	                     "Microsoft.ClassicStorage/capabilities",
	                     "Microsoft.ClassicStorage/checkStorageAccountAvailability",
	                     "Microsoft.ClassicStorage/disks",
	                     "Microsoft.ClassicStorage/images",
	                     "Microsoft.ClassicStorage/operations",
	                     "Microsoft.ClassicStorage/osPlatformImages",
	                     "Microsoft.ClassicStorage/publicImages",
	                     "Microsoft.ClassicStorage/quotas",
	                     "Microsoft.ClassicStorage/storageAccounts",
	                     "Microsoft.ClassicStorage/storageAccounts/services",
	                     "Microsoft.ClassicStorage/storageAccounts/servcies/diagnosticSettings",
	                     "Microsoft.MarketplaceApps/classicDevServices",
	                     "Microsoft.Web/classicMobileServices"
	                ]
	            }
	        },
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_unallowed_resources"
	    },
		{
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_allow_port_ranges_inbound"
	    }
	]'

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $sec
	fi

# Critical Policy Umbrella
sec="Create critical policy initiative"
az policy set-definition create \
	--name "critical_umbrella_policy_initiative" \
	--display-name "Optum Critical Policy Umbrella" \
	--description "Critical Policy Umbrella with built-in and custom policies"  \
	--definitions '[
	    {
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_allow_rdp_inbound"
	    },
		{
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_allow_ssh_inbound"
	    },
		{
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_allow_sql_ports_inbound"
	    },
		{
	        "policyDefinitionId": "/subscriptions/'$SubID'/providers/Microsoft.Authorization/policyDefinitions/audit_allow_all_ports_inbound"
	    }
	]'

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $sec
	fi