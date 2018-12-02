#** Terraform Robust Plan **
#  Sets terraform to use the correct workspace for the environment, and creates a plan in the current directory.
#  Will automatically re-provision temporary environments.

Param(
    [String]$planPath = "$env:TF_VAR_PROJECT_NAME-$env:Release.ReleaseName.plan",
    [String]$environment = $env:TF_VAR_ENVIRONMENT,
    [Boolean]$temporaryEnvironment = [String]::Equals($env:TF_VAR_USE_TEMPORARY_DATABASE,$true, [StringComparison]::OrdinalIgnoreCase),
    [String]$ARM_CLIENT_SECRET,
    [String]$TF_VAR_SQL_PASSWORD
)

#Secret values are not passed in by default, so require them as parameters
 $env:ARM_CLIENT_SECRET =$ARM_CLIENT_SECRET
 $env:TF_VAR_SQL_PASSWORD =$TF_VAR_SQL_PASSWORD

terraform init -input=false
if(!$?){
    throw "Errors occurred while initializing terraform"
}

if($temporaryEnvironment){
    terraform taint -input=false -allow-missing -module generate-random-name random_string.rest_of_string
    #Handler errors besides missing modules (allow missing is required to support the first-time run of a temporary environment)
    if(!$?){
        throw "Unable to regenerate temporary environment"
    }
    Write-Host "Temporary enviornment will be regenerated"
}

terraform validate -input=false
if(!$?){
    throw "Validation errors occurred"
}

terraform plan -out $planPath -input=false

if(!$?){
    throw "Error occured while planning"
}