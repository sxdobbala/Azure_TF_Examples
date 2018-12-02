# Azure Redbox Workshop

This repo contains example projects used for the Infrastructure as Code Workshop.  Examples here are designed to work within the constraints of Redbox, so should NOT include a Resource Group.


# Links and Commands in Slide Deck

The following are the links and commands from each slide in the workshop slide deck.


## Important URLs
Commercial Cloud Portal:  https://commercialcloud.optum.com

Azure Portal:  https://portal.azure.com

Github Redbox:  https://github.optum.com/CommercialCloud-Redbox-Azure

Jenkins Redbox:  https://jenkins.optum.com/redbox/job/commercialcloud-redbox-azure


## Prepare Redbox

Mac/Windows (using git-over-SSH):

`git clone git@github.optum.com:CommercialCloud-Redbox-Azure/<redbox name>.git`

**-- OR --**

Mac/Windows (using git-over-HTTPS):

`git clone https://github.optum.com/CommercialCloud-Redbox-Azure/<redbox name>.git`

#### Move existing Terraform files into a temporary subdir

Mac:
```
cd <redbox name>
mkdir temp
mv terraform.tfvars temp
mv *.tf temp
```

Windows:
```
cd <redbox name>
mkdir temp
move terraform.tfvars temp
move *.tf temp
```

## Push "empty" repo to git

Mac/Windows:
```
git status
git add .
git commit -m 'clean up'
git push
```

## Pull demo Terraform files into repo

Mac/Windows (using git-over-SSH):

`git clone --depth=1 --branch=master git@github.optum.com:CommercialCloud-EAC/azure_iac_workshop.git`

**-- OR --**

Mac/Windows (using git-over-HTTPS):

`git clone --depth=1 --branch=master https://github.optum.com/CommercialCloud-EAC/azure_iac_workshop.git`

Mac:
```
cd azure_iac_workshop
# pick one of these two examples to copy
cp -R demo1-vm_with_apache/* ..
#cp -R demo2-vm_with_mysql_voting_app/* ..
cd ..
```

Windows:
```
cd azure_iac_workshop
:: pick one of these two examples to copy
robocopy demo1-vm_with_apache .. /s /e
::robocopy demo1-vm_with_mysql_voting_app .. /s /e
cd ..
```

## Commit/push changes. Let Jenkins do its magic. Repeat.
Mac/Windows:
```
git status
git add .
git commit -m 'workshop demo'
git push
```
