import java.lang.Math

// vars/TerraformPipeline.groovy
def call() {
  if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'wip/pipeline') {
    def optumfile, subscriptions, environments
    node('docker-ruby-slave') {
      checkout scm

      optumfile = readYaml file: 'Optumfile.yml'
      subscriptions = optumfile.pipeline.terraform.azure.subscriptions
      environments = optumfile.pipeline.terraform.azure.environments

      stash name: "resources", exludes: ".git/**/*.terraform/**/*,terraform.tfstate"
    }

    environments.each { envKey, envConfig ->
      stage("Deploy ${envKey}") {
        node('docker-ruby-slave') {
          unstash "resources"
          def creds = subscriptions[envConfig.subscription].credentials
          withCredentials([azureServicePrincipal(credentialsId: creds.servicePrincipal,
            subscriptionIdVariable: 'ARM_SUBSCRIPTION_ID',
            clientIdVariable: 'ARM_CLIENT_ID',
            clientSecretVariable: 'ARM_CLIENT_SECRET',
            tenantIdVariable: 'ARM_TENANT_ID')

            ]) {

              if (envConfig.prompt) {
                timeout(time: 30, unit: 'DAYS') {
                  input("Deploy to ${envKey}?")
                }
              }

              def planFlags =  "-out=tfplan -input=false"

              if (envConfig.tfvars?.file) {
                def tfVarsFile = "-var-file=\"${envConfig.tfvars.file}\""
                echo tfVarsFile
                planFlags += " ${tfVarsFile}"
              }

              sh """#!/bin/bash
              export TERRAFORM_VERSION=0.11
              . /etc/profile.d/jenkins.sh

              echo 'running setup...'
              terraform -v
              az -v
              az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} --tenant ${ARM_TENANT_ID}
              export TF_VAR_environment=${envKey}

              # extract out assumption this is in source code of app
              curl https://github.optum.com/raw/CommercialCloud-EAC/azure_launchpad_deprecated/master/scripts/prepare.sh | bash -s
              curl https://github.optum.com/raw/CommercialCloud-EAC/azure_launchpad_deprecated/master/scripts/init.sh | bash -s

              terraform plan ${planFlags}
              terraform apply -input=false -auto-approve=true tfplan
              """
            }
          }
        }
      }
    }
  }
