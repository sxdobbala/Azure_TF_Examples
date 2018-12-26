// vars/LaunchPadPipeline.groovy
def call() {
    if (env.BRANCH_NAME) {
        String tfPrefix = 'subscription'

        pipeline {
            environment { 
                ARM_CLIENT_ID = credentials('testing-ARM_CLIENT_ID') 
                ARM_CLIENT_SECRET = credentials('testing-ARM_CLIENT_SECRET')
                ARM_TENANT_ID = credentials('testing-ARM_TENANT_ID')
                ARM_SUBSCRIPTION_ID = credentials('testing-ARM_SUBSCRIPTION_ID')
                
            }
            agent { label 'docker-ruby-slave' }
            stages {
                stage('Deploy Non-Prod') {
                    steps {
                        sh """#!/bin/bash
                            export TERRAFORM_VERSION=0.11
                            . /etc/profile.d/jenkins.sh

                            echo 'running setup...'
                            terraform -v
                            az -v
                            az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} --tenant ${ARM_TENANT_ID}
                            export TF_VAR_bootstrap_tfstate_resource_group_name=${tfPrefix}tfstate
                            sh ./scripts/prepare.sh
                            sh ./scripts/init.sh
                            terraform plan -out=tfplan -input=false terraform_module
                            terraform apply -input=false -auto-approve=true tfplan
                        """
                    }
                     post { 
                        failure { 
                            script {
                                if (fileExists('tfplan')) {
                                    sh """#!/bin/bash
                                        export TERRAFORM_VERSION=0.11
                                        . /etc/profile.d/jenkins.sh

                                        az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} --tenant ${ARM_TENANT_ID}
                                        export TF_VAR_bootstrap_tfstate_resource_group_name=bobtfstate
                                        terraform apply -input=false -auto-approve=true tfplan
                                        """
                                }
                            }
                        }
                    }
                }
            }
            post { 
                always { 
                     sh """#!/bin/bash
                        export TERRAFORM_VERSION=0.11
                        . /etc/profile.d/jenkins.sh

                        # ensure we first have synced state from remote state
                        sh ./scripts/prepare.sh
                        sh ./scripts/init.sh
                        terraform refresh terraform_module
                        
                        # Remove backend configuration in order to use the default local backend
                        # instead of azure
                        sed -i 's/azurerm/local/g' terraform_module/backend.tf
                        terraform init -force-copy terraform_module
                        terraform destroy -force terraform_module
                        """
                }
            }
        }
    }
}