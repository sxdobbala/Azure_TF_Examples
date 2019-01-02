# Spark Framework Automation Scripts
This project contains scripts that deploys and Updates the framework using Layer 7 proxy. All the user needs to do is create a jenkins pipeline that uses the provided jenkins pipeline files and update all json/conf files for spinning up these easy to use Frameworks

## Obtaining Credentials to Consume APIs
Please follow [API Consumer: Getting Started](https://www.optumdeveloper.com/content/odv-optumdev/optum-developer/en/getting-started/apis/api-consumers.html) Optum Developer link to obtaining Credentials to Consume APIs.

## Running the Install pipeline

1. Login to [Jenkins](https://jenkins.optum.com/central)

2. Add a your credentials for github to the jenkins global credentials store if you have not already done so:
  Jenkins -> Credentials -> System -> Global credentials (unrestricted) -> add credentials
 <p align="center">
  <img src="images/create_password.PNG" width="700"/>
</p> 

 3. Get API Key and Secret by:
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

5. Update the Client ID in OSFI Mesos Application
 <p align="center">
  <img src="images/FrmClientId.JPG" width="200"/>
</p> 

6. Go back to the Jenkins home screen by hitting the jenkins logo in the top corner, navigate to your folder and then hit new item:
<p align="center">
  <img src="images/new_item.PNG" width="700"/>
</p>

7. Give your pipeline a name and select the pipeline option and then hit OK
<p align="center">
  <img src="images/create_pipeline.PNG" width="700"/>
</p>

8. Now configure your pipeline so that it looks the same as below while pointing to your forked repository:
<p align="center">
  <img src="images/Configuration1.JPG" width="700"/>
</p>
<p align="center">
  <img src="images/Configuration2.JPG" width="700"/>
 </p>
 <p align="center">
  <img src="images/Configuration3.JPG" width="700"/>
 </p>
 
9. Save your pipeline

10. Edit the options.json file located in the install folder of the master
  
  Important Configs to note
   - service.name - The service name must be unique
  
11. Now navigate to your framework in Jenkins and run Build Now
 <p align="center">
  <img src="images/Spark_Installation_pipeline.JPG" width="700"/>
 </p>
 
 12. Click on the latest build in the build history which can be seen in the above screenshot
 
 10. Open up the console log
  <p align="center">
  <img src="images/Concole_out1.JPG" width="700"/>
 </p>
 
 13. Wait until you see the dns, addresses, vip, and zookeeper
  <p align="center">
  <img src="images/Concole_out2.JPG" width="700"/>
 </p>
 
 14. Your service is now running! :tada:
 
## Accessing Spark from OSFI Mesos Application 
To see your Framework , log into the OSFI Mesos Application environment and you should see your Framework ID and Framewrok Name in the list of Frameworks deployed.
<p align="center">
  <img src="images/Framwrk.JPG" width="700"/>
 </p>

Congrats you can now access your Spark running in Mesos.

## Uninstall/delete Framework Instructions
Please contact BDPaaS Admin for uninstalling or deleting your framework application.
 
