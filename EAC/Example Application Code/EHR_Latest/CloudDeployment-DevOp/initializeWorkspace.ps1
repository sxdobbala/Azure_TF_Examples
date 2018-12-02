# Initializes the workspace that will be used for terraform
#   Terraform requires that the absolute path remain constant between planning and applying, so this is required in order for humans to manually validate the plan.
Param(
    [String]$WorkingDirectory
)
if(-not (Test-Path $WorkingDirectory)){
    new-item -path $WorkingDirectory -type Directory
}

#TODO: Confirm that this is required
$isEncrypted = (Get-ItemProperty $WorkingDirectory).Attributes -bAnd [System.IO.FileAttributes]::Encrypted 

if(-not ( $isEncrypted )){
     #Force the folder to be encrypted
     cipher.exe /e $WorkingDirectory
     
     #Check and make sure changing folder attributes succeeded
     if(-not ((Get-ItemProperty $WorkingDirectory).Attributes -bAnd [System.IO.FileAttributes]::Encrypted )){
        throw "$WorkingDirectory must be encrypted because it contains sensitive information"
     }
}