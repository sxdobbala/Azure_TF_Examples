# Manual configuration of Splunk Heavy Forwarder

Once Splunk forwarder VM has been built by Terraform, there is a bit more manual configuration necessary. From the Terraform output, gather the resource_group_name, os_admin_username, os_admin_password, and forwarder_url (used later). NOTE: The Splunk VM takes a few minutes to fully install and configure Splunk once Terraform has built the VM.

1. Manually run (on a linux/mac administrative workstation with the Azure CLI) the script `create_splunk_sp.sh` like the example below, making sure to use the correct values (ask Commercial Cloud team for assistance if you're not sure of the correct values). The Subscription ID and Tenant ID are for the common logging/management subscription where the logging event hub and key vault reside. The values below masked by 'X' are created by the script and **should be kept in a secure location**. They are needed later in the Splunk web gui config.

The ObjectID (along with the TenantID) must be added to the [Jenkinsfile](https://github.optum.com/CommercialCloud-EAC/azure_tenant_refresh/blob/master/Jenkinsfile) in azure_tenant_refresh via Pull Request, so that proper permissions can be assigned to the SP.

```
$ ./create_splunk_sp.sh -s "200cfc6c-baea-49cd-b373-b0b8ea2f82a3" -t "5e87fb0c-99e9-499b-912f-b83caf5430be"
To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code PW38K43RL to authenticate.
User 'russ.jury@optumpoc.onmicrosoft.com' successfully authenticated.
Creating service principal 'splunkForwarder' in Azure AD tenant '5e87fb0c-99e9-499b-912f-b83caf5430be'.
Retrying role assignment creation: 1/36
Retrying role assignment creation: 2/36
Retrying role assignment creation: 3/36
name:              AzureActivityLog
SPNTenantID:       5e87fb0c-99e9-499b-912f-b83caf5430be
SPNApplicationID:  XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
SPNApplicationKey: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX=

ObjectID may be needed by Terraform that builds the Event Hub, but is not needed in Splunk's web configuration."
ObjectID:          XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
TenantID:          XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
```

2. Next, open the forwarder URL (from the Terraform output) in a browser (you must be on an Optum network to get to the Splunk endpoint), using the forwarder_admin_username and forwarder_admin_password (also in Terraform output). If you get a 503/"Service Unavailable" error, it may be that the Splunk VM hasn't finished configuring itself - however it shouldn't take longer than 5 minutes after Terraform is complete. The Splunk VM boot diagnostics screen will indicate when the installation is finished with the message "Splunk Install/Config Finished".

Once logged in, click "Settings" (top right black bar), and click "Data Inputs". Configure each type of Log/Metrics below:

### 2a. Activity Log

(assumes you're logged into the Splunk Forwarder web gui from step 4 above)

In the "Azure Monitor Activity Log" bar, click "+ Add New", and then enter the following information. For items in parenthesis, substitute the value of what it is describing. If you are unsure of any values, please contact the Commercial Cloud for help. Several of these come from the output of the Terraform that builds the Event Hub (part of azure_tenant_launchpad that is run by Jenkins).

```
name:  AzureActivityLog
SPNTenantID:   (Tenant ID, in form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
SPNApplicationId:  (Service Principal ID in form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx that was created in step 1 of this README)
SPNApplicationKey:  (Service Principal key for the ID above, created in step 1)
eventHubNamespace:  (Event Hub Namespace name that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
vaultName:  (Key Vault name that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
secretName:  (Key Vault Secret name (in the vault named above) that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
secretVersion:  (Key Vault Secret version of the secret named above - see Terraform output in Jenkins log)
```

Check "More settings", and in the Host field, enter the name of your tenant, in all lowercase, with no spaces. If you have questions about exactly how this should be formatted, please contact the Commercial Cloud team.

Double-check all the fields that you entered, and then click the green "Next" button (at the top). You can close the browser now (when it shows 4 buttons "Start Searching", "Add More Data", etc). You cannot search from this node (even though the app exists), as data is forwarded and not indexed locally.

### 2b. Diagnostic Log

(assumes you're logged into the Splunk Forwarder web gui from step 4 above)

In the "Azure Monitor Diagnostic Log" bar, click "+ Add New", and then enter the following information. For items in parenthesis, substitute the value of what it is describing. If you are unsure of any values, please contact the Commercial Cloud for help. Several of these come from the output of the Terraform that builds the Event Hub (part of azure_tenant_launchpad that is run by Jenkins).

```
name:  AzureDiagnosticLog
SPNTenantID:   (Tenant ID, in form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
SPNApplicationId:  (Service Principal ID in form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx that was created in step 1 of this README)
SPNApplicationKey:  (Service Principal key for the ID above, created in step 1)
eventHubNamespace:  (Event Hub Namespace name that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
vaultName:  (Key Vault name that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
secretName:  (Key Vault Secret name (in the vault named above) that was created via Terraform in azure_tenant_launchpad - see Terraform output in Jenkins log)
secretVersion:  (Key Vault Secret version of the secret named above - see Terraform output in Jenkins log)
```

Check "More settings", and in the Host field, enter the name of your tenant, in all lowercase, with no spaces. If you have questions about exactly how this should be formatted, please contact the Commercial Cloud team.

Double-check all the fields that you entered, and then click the green "Next" button (at the top). You can close the browser now (when it shows 4 buttons "Start Searching", "Add More Data", etc). You cannot search from this node (even though the app exists), as data is forwarded and not indexed locally.

### 2c. Metrics

TBD

## Troubleshooting Splunk Forwarder (assumes either Serial Console or SSH access is enabled - both are disabled by default)

### How to view what SPNApplicationKey/Secret you set (as it's masked out in

the GUI) - at least as of Splunk 7.1.1:

1. Turn on Debugging for ExecProcessor: **Note:** This will be reset to INFO when splunk restarts.

```
Settings->Server Settings->Server logging->ExecProcessor->DEBUG
```

2. Tail /opt/splunk/var/log/splunk/splunkd.log -- every 60s (assuming it is enabled and using default timing), azure_activity_log.sh will run and kick out what credentials it used in plain text to the log.

### Debugging Logs (assume you have access to the OS, which isn't currently enabled/supported)

To turn on debugging, in Splunk web gui navigate to:

```
Settings -> Server Settings -> Server logging -> ExecProcessor -> DEBUG
```

Then restart the Activity Log app by disabling/enabling the app:

```
Settings -> Data inputs -> Azure Monitor Activity Log -> Disable -- then Enable
```

Errors from splunkd.log (and their likely cause) are displayed below:

If Event Hub key (in Key Vault) is wrong:

```
> RX ERROR on hub: insights-operational-logs, err: AmqpProtocolError: amqp:unauthorized-access:An AMQP error occurred (condition='amqp:unauthorized-access').  TrackingId:551ad408701d4beb9656480883acd3ea_G27, SystemTracker:gateway5, Timestamp:6/19/2018 7:00:10 PM
```

If SPNApplicationID (ClientID) or SPNApplicationKey (ClientSecret) is wrong:

```
06-19-2018 18:59:48.012 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_activity_log.sh" Modular input azure_activity_log://optumpoc-centralus Error getting event hub creds: Error: Get Token request returned http error: 401 and server response: {"error":"invalid_client","error_description":"AADSTS70002: Error validating credentials. AADSTS50012: Invalid client secret is provided.\r\nTrace ID: a0e58a28-f3e9-470a-aaac-77dfbb1b4d00\r\nCorrelation ID: 40e06a3b-6ff1-4e56-a421-084f390753be\r\nTimestamp: 2018-06-19 18:59:47Z","error_codes":[70002,50012],"timestamp":"2018-06-19 18:59:47Z","trace_id":"a0e58a28-f3e9-470a-aaac-77dfbb1b4d00","correlation_id":"40e06a3b-6ff1-4e56-a421-084f390753be"}
```

If the SP doesn't have access to they Key Vault secret (check the key vault access policies to make sure the SP has 'get'):

```
06-19-2018 21:18:43.429 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_activity_log.sh" Modular input azure_activity_log://optumpoc-centralus Error getting event hub creds: StatusCodeError: 403 - {"error":{"code":"Forbidden","message":"Access denied","innererror":{"code":"AccessDenied"}}}
```

If activity logs aren't being forwarded (but splunk is connecting to the keyvault/event hub):

```
06-19-2018 21:35:33.226 +0000 DEBUG ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_activity_log.sh" Modular input azure_activity_log://optumpoc-centralus ==> Did not find hub: insights-operational-logs. Message: amqp:not-found:The messaging entity 'sb://centralus-200cfc6c.servicebus.windows.net/insights-operational-logs/consumergroups/$default/partitions/0' could not be found. TrackingId:6f8d22dbc2974559a2004baaf6710f8f_G32, SystemTracker:gateway5, Timestamp:6/19/2018 9:35:32 PM
```

This is just Azure/Add-on getting confused. To recover, you need to clone your data input and delete the old one (which effectively recreates/initiates the AMQP client):

```
06-19-2018 21:51:33.154 +0000 DEBUG ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_activity_log.sh" Modular input azure_activity_log://optumpoc-centralus ==> RX ERROR on hub: insights-operational-logs, err: AmqpProtocolError: com.microsoft:argument-error:The supplied offset '154448' is invalid.  The last offset in the system is '2384' TrackingId:c8ef0638-8537-477d-95b4-86e264adcc82_B24, SystemTracker:centralus-200cfc6c:eventhub:insights-operational-logs~16383, Timestamp:6/19/2018 9:51:32 PM Reference:6034d04c-9fd8-468d-bd3d-bedb288919d4, TrackingId:5a6dbd33-654c-4062-9179-4a0a40e3b44a_B24, SystemTracker:centralus-200cfc6c:eventhub:insights-operational-logs~16383|$default, Timestamp:6/19/2018 9:51:32 PM TrackingId:75d91c6249f14493a4b5a34240608fa6_G6, SystemTracker:gateway5, Timestamp:6/19/2018 9:51:32 PM
```

If diagnostic logs are enabled, but no messages have been sent (yet), you'll see something similar to this in splunkd.log. It is a [known issue](https://github.com/Microsoft/AzureMonitorAddonForSplunk/issues/8).

```
09-05-2018 19:40:15.499 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" /opt/splunk/etc/apps/TA-Azure_Monitor/bin/app/node_modules/amqp10/lib/frames.js:64
09-05-2018 19:40:15.499 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh"   stream.write(buffer, callback);
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh"     ^
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" TypeError: Cannot read property 'write' of null
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at Object.frames.writeFrame (/opt/splunk/etc/apps/TA-Azure_Monitor/bin/app/node_modules/amqp10/lib/frames.js:64:9)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at Connection.sendFrame (/opt/splunk/etc/apps/TA-Azure_Monitor/bin/app/node_modules/amqp10/lib/connection.js:329:10)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at ReceiverLink.Link.attach (/opt/splunk/etc/apps/TA-Azure_Monitor/bin/app/node_modules/amqp10/lib/link.js:152:27)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at Timeout._onTimeout (/opt/splunk/etc/apps/TA-Azure_Monitor/bin/app/node_modules/amqp10/lib/link.js:270:12)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at ontimeout (timers.js:386:11)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at tryOnTimeout (timers.js:250:5)
09-05-2018 19:40:15.500 +0000 ERROR ExecProcessor - message from "/opt/splunk/etc/apps/TA-Azure_Monitor/bin/azure_diagnostic_logs.sh" at Timer.listOnTimeout (timers.js:214:5)
```
