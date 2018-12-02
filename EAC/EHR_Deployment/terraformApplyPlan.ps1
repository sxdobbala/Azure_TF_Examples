#** Terraform Apply Plan **
#  Simple script to apply a saved plan. Must be run in the same folder and by the same machine as the original plan (due to how terraform's "apply" was implemented)

Param(
    [String]$planPath = "$env:TF_VAR_PROJECT_NAME-$env:Release.ReleaseName.plan",
    [String]$ARM_CLIENT_SECRET
)
$env:ARM_CLIENT_SECRET =$ARM_CLIENT_SECRET

terraform apply -input=false $planPath

if(!$?){
    throw "Error occurred applying the plan, the application may be unusable!"
}

#TODO: Store the url associated with the app in a variable
$hostname = terraform output "hostname"
#TODO: Store the app service name, so we can use it in the deployment step
# (Not sure this will work)
$serviceName = terraform output "servicename"

#Stores the results into TFS variables so they can be used in future steps
Write-Host "##vso[task.setvariable variable=applicationhostname]" $hostname
Write-Host "##vso[task.setvariable variable=applicationservicename]" $serviceName