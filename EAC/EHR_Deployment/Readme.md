Prototype of Powershell scripts and Terraform templates to provision UR Advisor. Powershell scripts should be run in order:  
1- initializeWorkspace  
2- terraformCreatePlan  
3- terraformApplyPlan  
For prod environments, manual review of the plan can occur review between steps 2 and 3.