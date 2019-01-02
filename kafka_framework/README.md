# Kafka Framework Automation Scripts
This project contains scripts that deploys and Updates the framework using Layer 7 proxy. All the user needs to do is create a jenkins pipeline that uses the provided jenkins pipeline files and update all json/conf files for spinning up these easy to use Frameworks.

## Obtaining Credentials to Consume APIs
Please follow [API Consumer: Getting Started](https://www.optumdeveloper.com/content/odv-optumdev/optum-developer/en/getting-started/apis/api-consumers.html) Optum Developer link to obtaining Credentials to Consume APIs.

## Running the Install pipeline

1. Login to [Jenkins](https://jenkins.optum.com/central)

2. Add a your credentials for github to the jenkins global credentials store if you have not already done so:
  Jenkins -> Credentials -> System -> Global credentials (unrestricted) -> add credentials
 <p align="center">
  <img src="images/create_password.PNG" width="700"/>
</p> 

3.Get API Key and Secret by:
   * Logging into API Manager
   * Navigate to Application on Left Pane
   * Hover Over Application Gear
   * Select Edit in Gear Drop-Down
   
 <p align="center">
  <img src="images/api-manager-app-instructions.JPG" width="700"/>
</p>

   * Navigate to Auth Tab in Window
   * Copy Key and Secret for future reference

 <p align="center">
  <img src="images/api-manager-key-secret.JPG" width="700"/>
</p>

4. Add API Key and API Secret Credentials in Jenkins by navigating to 
Credentials -> System -> Global credentials (unrestricted) -> add credentials

**Kind: Secret Text**

**ID: ClientSecret & ClientKey**

 <p align="center">
  <img src="images/api-key-secret.JPG" width="700"/>
</p>

5. After finished you should have 2 Secret Texts (ClientSecret & ClientKey) and 1-2 Username Password (Github and Docker) similar to below.

<p align="center">
  <img src="images/jenkins-credentials.JPG" width="700"/>
</p>

6. Update the Client ID in OSFI Mesos Application
 <p align="center">
  <img src="images/FrmClientId.JPG" width="200"/>
</p> 
## Setup and Run the Deployment Pipeline

1. Go back to the Jenkins home screen by hitting the jenkins logo in the top corner, navigate to your folder and then hit new item:
<p align="center">
  <img src="images/new_item.PNG" width="700"/>
</p>

2. Give your pipeline a name and select the pipeline option and then hit OK
<p align="center">
  <img src="images/create_pipeline.PNG" width="700"/>
</p>

3. Now configure your pipeline so that it looks the same as below while pointing to your forked repository:
<p align="center">
  <img src="images/pipeline_1.PNG" width="700"/>
</p>
<p align="center">
  <img src="images/pipeline_2.PNG" width="700"/>
 </p>
 <p align="center">
  <img src="images/pipeline_3.PNG" width="700"/>
 </p>
 
4. Save your pipeline

5. Edit the options.json file located in the install folder of the master
  
  Important Configs to note
   - service.name - The service name must be unique
   - brokers.disk_type - ROOT or MOUNT. If set to MOUNT a disk has to be provisioned on the servers that is free for use. If set to ROOT volume is cleaned up periodically by garbage collection
   - brokers.disk - If using MOUNT, set size to 500000.
   - broker.count - Specify the number of brokers needed the default is 3. If the broker count is less then 3 then the confluent_metrics_reporter_topic_replicas and offsets_topic_replication_factor need to be reduced as well.
   
6. Now navigate to your framework in Jenkins and run Build Now
 <p align="center">
  <img src="images/build_now.PNG" width="700"/>
 </p>
 
 7. Click on the latest build in the build history which can be seen in the above screenshot
 
 8. Open up the console log
  <p align="center">
  <img src="images/console_output.png" width="700"/>
 </p>
 
 9. Wait until you see the dns, addresses, vip, and zookeeper
  <p align="center">
  <img src="images/desired_output.png" width="700"/>
 </p>
 
 10. Your service is now running! :tada:
 
## Accessing Framework from OSFI Mesos Application
1.To see your Framework , log into the OSFI Mesos Application environment and you should see your Framework ID and Framewrok Name in the list of Frameworks deployed.
<p align="center">
  <img src="images/Framwrk.JPG" width="700"/>
 </p>
 
2. Find Your Application listed in one of the 2 HA-Proxy URLs for your mesos environment. 

| Mesos Cluster | Public Nodes | HA-Proxy URLs |
|:-----:| ----------------------------- | --------------- |
| CTC | 10.205.220.32,10.205.220.53 | [LB-1](http://10.205.220.32:9090/haproxy?stats), [LB-2](http://10.205.220.53:9090/haproxy?stats) |

3. Access Tenant Application via Marathon-LB
 
 ## Uninstall/delete Framework Instructions
Please contact BDPaaS Admin for uninstalling or deleting your framework application.
