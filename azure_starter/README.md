# Azure Redbox Starter

## OEA Workbench Workshop

### Directions

1. Go to your redbox GitHub repo
2. Create a new branch called `oea_workbench`
3. Copy all of the contents of the `Jenkinsfile` in this repo to the `Jenkinsfile` in your `oea_workbench` branch in your redbox repo
4. Commit and push these changes
5. This will kick off a pipeline run that makes a call to the OEA Workbench API
6. Head to `https://jenkins.optum.com/redbox/job/CommercialCloud-Redbox-Azure/`
7. Find your redbox account
8. You should see a pipeline run for the `oea_workbench` branch
9. This run should be successful (turn green)
10. Head to the Azure Portal and find your redbox resource group (logout and login if you cannot see it)
11. Wait for the resources to be provisioned in your redbox (there will be a VM created that is called `{redbox-account}vm`)
12. Click on this VM
13. Find and copy the `DNS` name into the search bar of a new browser tab. Add `https://` to the beginning
14. Navigate to this URL
15. If successful, you will see the jupyter login screen
