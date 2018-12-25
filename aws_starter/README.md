# AWS Starter

__Note: This only creates the state file for the resources that are configured in the Terraform Project and not the Backend resources (S3 Bucket and DynamoDB) that is created via the [_prepare.sh_](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap/blob/master/scripts/prepare.sh) under [_aws_boostrap_](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap) in the [_AWSPipeline_](https://github.optum.com/CommercialCloud-EAC/jenkins-pipeline/blob/master/vars/AWSPipeline.groovy). Unless the Backend resources are explicitly created in this project as well or the aws_bootstrap was utilized in this project, then the _prepare.sh_ is not needed.__

A starter project for teams looking to use terraform with the commercial cloud pipeline and modules.

> If you're looking to add a new commercial cloud terraform module please see the [module_skeleton](https://github.optum.com/CommercialCloud-EAC/terraform_module_skeleton) repository.

## Getting Started

1. Ensure [prerequisites](#prerequisites) are met 
2. Fork this repository
3. Configure Jenkins with either a Multibranch pipeline or a Github organization to pick up the forked repository
4. Configure Jenkins to have the proper credentials defined for your use case per the [Commercial Cloud Terraform Pipeline](https://github.optum.com/CommercialCloud-EAC/jenkins-pipeline) configuration
5. Run the build on forked repositoy master branch and your off to the races!

## Configuration

The `Optumfile.yaml` and `Jenkinsfile` are related to the [Commercial Cloud Terraform Pipeline](https://github.optum.com/CommercialCloud-EAC/jenkins-pipeline).  Further information can be found there.

## Running Locally

### Tooling

* [Install Terraform](https://www.terraform.io/intro/getting-started/install.html)
* [Install AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)

### Authenticate AWS CLI via SAML

[aws-cli-saml repository](https://github.optum.com/CommercialCloud-EAC/python-scripts/tree/master/aws-cli-saml)

If you don't have credentials set for your AWS CLI, see the above repository to execute a Python (3.6) script to create temporary credentials.

An example usage of this script to authenticate to a non-sandbox account is given below.

```bash
virtualenv --python=$(which python3) venv
source venv/bin/activate
pip install -r requirements.txt
python ./authenticate_py3.py
** follow prompts as needed **
aws ec2 describe-instances --profile saml
export AWS_PROFILE=saml # To set the just generated 'saml' credentials for all your AWS CLI commands
```


#### Run terraform_module

__Please see the [aws_bootstrap repository](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap) to get an understanding of the module, along with the [prepare and init bash scripts](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap/tree/master/scripts).__

##### Create or Updating the Terraform Project 

_Note: This only creates the state file for the resources that are configured in the Terraform Project and not the Backend resources (S3 Bucket and DynamoDB) that was created via the __prepare.sh__. Unless the Backend resources were explicitly configured in this project as well or the aws_bootstrap was utilized in this project, then the __prepare.sh__ is not needed._

```bash
# Set up the Bootstrap and run it through as a bash script
$ export BOOTSTRAP_LOC=$HOME/.bootstrap # Location of where to run aws_bootstrap
$ export acc="123456789012" # AWS account ID
$ export nameSpace="example" # Name Space used for aws_bootstrap and backend
$ export region="us-east-1" # Region used for the aws_bootstrap and backend
$ export planFlagsBootstrap="-auto-approve=true -var name_space=${nameSpace} -var aws_region=${region}"
$ export WORKSPACE=`pwd` # Assign this environment variable to the location of the aws_starter or your terraform project that you would like to set up the backend
## Only run the prepare.sh script below if the backend resources (S3 Bucket and DynamoDB Table) has not been setup before
## Else continue to the init.sh
$ curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/master/scripts/prepare.sh | bash -s
## This init.sh must be ran every time when using the backend the first or any other time, this allows the project to get the most up to date state file
$ curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/master/scripts/init.sh | bash -s
$ terraform plan # Ensure plan is correct and the "local" state file was updated
$ terraform apply # Follow prompts
```

##### Destroying the Terraform Project

_Note: This only destroys the resources that are configured in the Terraform Project and not the Backend resources (S3 Bucket and DynamoDB) that was created via the __prepare.sh__. Unless the Backend resources are explicitly configured in this project as well or the aws_bootstrap was utilized in this project, then the __prepare.sh__ is not needed._

```bash
# Set up the Bootstrap and run it through as a bash script
$ export BOOTSTRAP_LOC=$HOME/.bootstrap # Location of where to run aws_bootstrap
$ export acc="123456789012" # AWS account ID
$ export nameSpace="example" # Name Space used for aws_bootstrap and backend
$ export region="us-east-1" # Region used for the aws_bootstrap and backend
$ export planFlagsBootstrap="-auto-approve=true -var name_space=${nameSpace} -var aws_region=${region}"
$ export WORKSPACE=`pwd` # Assign this environment variable to the location of the aws_starter or your terraform project that you would like to set up the backend
## This init.sh must be ran every time when using the backend the first or any other time, this allows the project to get the most up to date state file
$ curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/master/scripts/init.sh | bash -s
$ terraform plan # Ensure plan is correct and the "local" state file was updated
$ terraform destroy # Follow prompts
```

## Prerequisites

### Obtaining an AWS Subscription

The formal request for public cloud usage within Optum starts with this [form](https://cci.ideas.aha.io/ideas/new).

### AWS Service Account

Please refer to the [__Preqequisites - Service Account__](https://github.optum.com/CommercialCloud-EAC/aws_bootstrap/blob/master/README.md) section of the aws_bootstrap README.