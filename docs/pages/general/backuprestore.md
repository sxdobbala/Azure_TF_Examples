## How To Backup Original Environment
There is a Jenkins job, [project-backup](https://jenkins.optum.com/cloudscaffolding/job/project-backup/), that runs weekly and backs up the necessary objects from our dev, test, & prod environments onto a [private Git repo](https://github.optum.com/cloud-scaffolding/project-backup-files).



## How To Recreate An Environment From A Backup Yaml File
We have to clone the desired backup from the repository, to our local machine, and then create a project, using our [DTC UI](https://dtc.optum.com). Once the project is created in the desired cluster, we have to login to openshift cluster, using our username and password

 

oc login <template_cluster_name>

switch to your project:

oc project <project_name>

Give the necessary approval/access to your project if needed: pbidevelop, codegen_nonprod

oc policy add-role-to-user admin <template_given_approval> -n <project_name>

select your project from openshift and click on add 

 



Click on Import YAML/JSON

Click Browse and navigate to where your yamls files were stored from the clone repository and select the file which contains buildConfig, imageStream and Service.

Click Create and Add Template popup, select process template and click continue

Add Label



Click create and click Continue to Overview

The project will be created. Now you select add to project and click import YAML/JSON to add the secrets, routes and pvc separately to the project.

Click overview -> ProjectName -> Action, then select edit from the dropdown. Edit deployment config, check if the correct image stream tag is selected and save.

Next click on storage, and delete all the persistent volume claims present and manually recreate the deleted claims and give it the right size/capacity.

We align the desired Quotas, by updating the project through our [DTC api](https://dtc.optum.com) using the put request.

Go to the https://jenkins.optum.com/cloudscaffolding/ and build dtc, mix-master, and codegen respectively with their build parameters.
