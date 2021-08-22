@Library('com.optum.jenkins.pipeline.library@master') _
 
pipeline {
  agent {
    // your Docker template needs to have the azurecli, kubectl, and docker mixins loaded, see https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins for more info
    label 'docker-kitchensink-slave'
  }
 
  environment {
    PROJECT_NAME = 'dojo-pe'
    AZURECLI_VERSION = "2.23"
  }
 
  stages {

    stage ('Docker Build & Deploy') {
      steps {
        withCredentials([azureServicePrincipal('azure_o360-Delivery-Sandbox')]) {
          sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID'
        }
        sh """
          #!/bin/bash
          set +x
          . /etc/profile.d/jenkins.sh

          az -v
          az config set extension.use_dynamic_install=yes_without_prompt

          echo
          echo "cleaning out the directory"
          echo 

          rm -r *

          echo
          echo "pulling from blob storage"
          echo

          az storage blob download --account-name dojopesaprd --container-name artifacts --file _site-${params.VersionNo}.zip --name _site-${params.VersionNo}.zip

          echo
          echo "unzip build file"
          echo

          unzip -o _site-${params.VersionNo}.zip

        """

        glAzureBuildDeployDocker loginServer:'optumprovidereastusaf030d48.azurecr.io',
          credentialsId:'dojo-pe-acr-login',
          extraBuildOptions:'-t',
          image:"$env.PROJECT_NAME",
          tag:'latest'

        sh """
          #!/bin/bash
          set +x
          . /etc/profile.d/jenkins.sh

          echo
          echo "az command for refreshing app pools"
          echo

          az webapp restart --name dojo-pe-app-svc-prd --resource-group dojo-provider-engineering-prd

          echo
          echo "removing Dockerfile"
          echo

          rm -f Dockerfile

          echo
          echo "cleaning up search storage account"
          echo

          az storage blob directory delete -c dojopesearch-prd -d _site --account-name dojopesaprd --recursive

          echo
          echo "uploading search storage account"
          echo

          az storage azcopy blob upload --container 'dojopesearch-prd' --account-name 'dojopesaprd' --source '_site/' --recursive

          """
      }
    }
  }
}
