Terraform to set up/maintain centralized logging event hub (1 per tenant)

Manual steps required before using this Terraform:

1) Create an Azure AD group (in each Azure AD instance).  Members of this group will be able to write to the Centralized Logging Event Hub.  The UUID of this group is used as variable (input) to Terraform (See main [README.md](../README.md). 

2) Create a Service Principal for each subscription to be used for exporting the Activity Log; add each SP to the group created in step 1.


