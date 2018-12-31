## VM with Apache, static file

This example is meant to run in a Redbox account.  It creates a basic Ubuntu VM with Apache and all dependent resources.  Network security group rules prevent access to the VM (on port 443) from anywhere except Optum networks.  Port 80 is open only to redirect HTTP to HTTPS (via Apache config).


### To run this demo:

**Note:** THIS MAY DELETE EXISTING RESOURCES IN AZURE! If Terraform has built resources already (and saved their state in the terraform.tfstate file, it will delete those resources while building this demo. It is advised to start with a empty resource group or run this example in a different tfstate file or environment (examples are provided in Step 4).

1. In the root directory of your Redbox repo directory, clone this repo (using ONLY ONE of the two commands below depending on whether you're using HTTPS or SSH):
```
git clone https://github.optum.com/CommercialCloud-EAC/azure_iac_workshop.git
git clone git@github.optum.com:CommercialCloud-EAC/azure_iac_workshop.git
```

2. Remove the .git subdirectory in azure_iac_workshop (NOT the one for your Redbox!!)
```
rm -rf azure_iac_workshop/.git
```

3. Set the following variables in terraform.tfvars (in each demo directory):
   - **resource_group_name** -- set this to the name of your Redbox resource group name
   - **email_address** -- (optional) leave this commented


4. Edit Optumfile.yml and modify as follows, paying close attention to indentations.  The 'stateFile' key will be under 'remoteState', and the 'terraformDirectory' key will be at the same level as 'remoteState'.

This will change the behavior of Jenkins to run out of a subdirectory of your Redbox GitHub repo, and to use a unique Terraform state file, making it easier to work with multiple projects at once. Choose one of the demos in the azure_iac_workshop directory as follows (you can leave old lines in this file but commented out with a hash(#)):

__Add New Environment to run the VM with Apache Demo__
```
pipeline:
  terraform:
    azure:
      node: docker-cc-azure-slave
      subscriptions:
        redbox:
          credentials:
            servicePrincipal: Redbox-ServicePrincipal
      environments:
        rjury2-01:                        # This will be your own Resource Group name
          subscription: redbox
          tfvars:
            file: ./terraform.tfvars
          prompt: false
          remoteState:
            resourceGroup: rjury2-01      # This will be your own Resource Group
            storageAccount: zzbjjiewhhos  # This will be your own Storage Account
            location: eastus              # This will be your own Storage Account's Location
## Additions below
        rjury2-01-demo-1:                 # Environment names are used for Jenkins Locking, use a name with your Resource Group prefixed (rjury2-01-demo-x) 
          subscription: redbox
          tfvars:
            file: ./terraform.tfvars
          prompt: false
          remoteState:
            resourceGroup: rjury2-01      # Use the same Resource Group as the initial environment
            storageAccount: zzbjjiewhhos  # Use the same Storage Group as the initial environment
            location: eastus              # Use the same Location as the initial environment
            stateFile: demo1.tfstate
          terraformDirectory: azure_iac_workshop/demo1-vm_with_apache/ 
```

5. Commit all changes, and push to GitHub:
```
git add .
git commit -m 'demo1'
git push
```

6. Jenkins console output will have the URL of the server it created
