import java.lang.Math
import hudson.model.*

// vars/AWSPipeline.groovy
def call() {
    if (env.BRANCH_NAME) {
        node('docker-ruby-slave') {
            checkout scm

            def optumfile = readYaml file: 'Optumfile.yml'

            final def accounts = optumfile.pipeline.terraform.aws.accounts
            final def environments = optumfile.pipeline.terraform.aws.environments

            environments.each { envKey, envConfig ->

                stage('Configure AWS')

                    if (envConfig.prompt) {
                      timeout(time: 30, unit: 'DAYS') {
                        input("Deploy to ${envKey}?")
                      }
                    }

                    def creds = accounts[envConfig.account].jenkins.credentials
                    env.acc = accounts[envConfig.account].account
                    env.role = accounts[envConfig.account].roleArn

                    // Check whether to use the sandox certificate or the non-prod/prod certificate for authentication
                    env.planFlagsAuth =  " -e prod "
                    if(accounts[envConfig.account]?.sandbox) {
                        if(accounts[envConfig.account].sandbox == true) {
                            env.planFlagsAuth = " -e sandbox "
                        }
                    }

                    withCredentials([usernamePassword(credentialsId: creds.usernamePassword, 
                        usernameVariable: 'AWS_CLI_username', passwordVariable: 'AWS_CLI_password')])
                    {
                        // Tries twice to authenticate the AWS account and create a credentials file
                        retry(2)
                        {
                            sh '''
                            # Export Python 3 and execute the jenkins mixin scripts
                            export PYTHON_VERSION=3.6
                            export TERRAFORM_VERSION=0.11.5
                            . /etc/profile.d/jenkins.sh

                            # Make empty credentials files for AWS
                            export AWS_LOC=$HOME/.aws

                            # Install and execute a virtual environment to run and collect the AWS credentials
                            pip install virtualenv
                            virtualenv venv
                            source venv/bin/activate

                            # Download python script and files to authenticate to AWS
                            export AUTH_LOC=$HOME/aws-cli-saml
                            rm -rf $AUTH_LOC
                            mkdir $AUTH_LOC

                            cd $AUTH_LOC
                            for file in authenticate_py3.py prod.cer requirements.txt sandbox.cer; do \
                                curl https://github.optum.com/raw/CommercialCloud-EAC/python-scripts/master/aws-cli-saml/$file > $AUTH_LOC/$file
                            done;
                            pip install -r requirements.txt
                            python3 authenticate_py3.py ${planFlagsAuth} -u $AWS_CLI_username -p $AWS_CLI_password -r ${role}
                            '''
                        }
                    }

                stage("Deploy ${envKey}")

                    env.planFlags =  "-out=tfplan -input=false"

                    if (envConfig.tfvars?.file) {
                        def tfVarsFile = "-var-file=${WORKSPACE}/${envConfig.tfvars.file}"
                        echo tfVarsFile
                        env.planFlags += " ${tfVarsFile}"
                    }

                    env.planFlagsBootstrap =  "-auto-approve=true"
                    env.acc = accounts[envConfig.account].account
                    env.nameSpace = "bootstrap"
                    env.region = "us-east-1"

                    if (envConfig.bootstrapVars?.nameSpace) {
                        env.nameSpace = "${envConfig.bootstrapVars.nameSpace}"
                        echo "${nameSpace}"
                    }
                    env.planFlagsBootstrap += " -var name_space=${nameSpace}"

                    if (envConfig.bootstrapVars?.region) {
                        env.region = "${envConfig.bootstrapVars.region}"
                        echo "${region}"
                    }
                    env.planFlagsBootstrap += " -var aws_region=${region}"

                    // Tries twice for initializing the terraform files
                    retry(2)
                    {
                        sh '''
                        # Export Python 3 and execute the jenkins mixin scripts
                        export PYTHON_VERSION=3.6
                        export TERRAFORM_VERSION=0.11.5
                        . /etc/profile.d/jenkins.sh

                        # Set up the Bootstrap and run it through as a bash script
                        export BOOTSTRAP_LOC=$HOME/.bootstrap
                        export AWS_PROFILE=saml
                        ## Only prepare if the resources do not already exists
                        aws s3 ls ${acc}-tfstate-${nameSpace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
                        aws --region ${region} dynamodb scan --table-name ${acc}-tflock-${nameSpace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
                        curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/init.sh | bash -s
                        '''
                    }

                    // Apply the terraform code
                    sh '''
                    # Export Python 3 and execute the jenkins mixin scripts
                    export PYTHON_VERSION=3.6
                    export TERRAFORM_VERSION=0.11.5
                    export AWS_PROFILE=saml
                    . /etc/profile.d/jenkins.sh

                    # Terraform plan and apply caller
                    terraform plan ${planFlags}
                    terraform apply -input=false -auto-approve=true tfplan
                    '''
            }
        }
    }
}
