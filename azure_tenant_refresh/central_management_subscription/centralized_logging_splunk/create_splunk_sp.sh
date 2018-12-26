#!/bin/bash
#
# Modified from original script at https://github.com/Microsoft/AzureMonitorAddonForSplunk/blob/master/scripts/azure-setup.sh

showErrorAndUsage() {
  echo
  if [[ "$1" != "" ]]
  then
    echo "  error:  $1"
    echo
  fi

  echo "usage:  $(basename ${0}) [options]"
  echo "options (no spaces allowd in any values):"
  echo "  -s <subscription id>     : [Required] Azure subscription Id."
  echo "  -t <tenant id>           : [Required] Azure Active Directory / Tenant Id."
  echo "  -n <namespace>           : [Optional] Namespace for unique naming of the Service Principal" 
  exit 1
}

SUBSCRIPTION_ID=""
TENANT_ID=""
NAMESPACE=""

JQ=$(which jq)
if [ "$JQ" == "" -o ! -e "$JQ" ]; then
  echo "jq (https://github.com/stedolan/jq) command not found.  Install jq (and make sure it is in PATH), then retry."
  echo
  exit 10
fi

while getopts ':s:t:n:' opt; do
    case $opt in
        s)
            SUBSCRIPTION_ID=$OPTARG
            ;;
        t)
            TENANT_ID=$OPTARG
            ;;
        n)
            NAMESPACE=$OPTARG
            ;;
        ?)
            showErrorAndUsage
            ;;
    esac
done


if [[ $SUBSCRIPTION_ID == "" || $TENANT_ID == "" ]]
then
    echo "Error ==>"
    echo "    SUBSCRIPTION_ID:     [$SUBSCRIPTION_ID]"
    echo "    TENANT_ID:           [$TENANT_ID]"
    showErrorAndUsage
fi

# turn on error protection (stops script immediately if anything from here on returns non-zero)
set -e

SERVICE_PRINCIPAL_NAME="splunkForwarder$NAMESPACE"

# Authenticate to Azure as an interactive user and set the target subscription.
SUBSCRIPTIONS=$(az login --tenant $TENANT_ID)
az account set --subscription $SUBSCRIPTION_ID
AZURE_USER=$(az account show --query user.name)
AZURE_USER=${AZURE_USER//[\"]/}
echo "User '${AZURE_USER}' successfully authenticated."


# Create a service principal and assign it to the Reader role for the subscription.
CLIENT_SECRET=$(openssl rand -base64 32)
echo "Creating service principal '${SERVICE_PRINCIPAL_NAME}' in Azure AD tenant '${TENANT_ID}'."
SPN_APP_ID=$(az ad sp create-for-rbac \
    --name $SERVICE_PRINCIPAL_NAME \
    --password $CLIENT_SECRET \
    --role Reader \
    --scopes /subscriptions/$SUBSCRIPTION_ID \
    --query appId)
SPN_APP_ID=${SPN_APP_ID//[\"]/}

SPN_OBJ_ID=$(az ad sp show --id $SPN_APP_ID --subscription $SUBSCRIPTION_ID | $JQ -r '.objectId')

echo "name:              AzureActivityLog"
echo "SPNTenantID:       $TENANT_ID"
echo "SPNApplicationID:  $SPN_APP_ID"
echo "SPNApplicationKey: $CLIENT_SECRET"
echo ""
echo "ObjectID may be needed by Terraform that builds the Event Hub, but is not needed in Splunk's web configuration."
echo "ObjectID:          $SPN_OBJ_ID"
echo "TenantID:          $TENANT_ID"
