@Library('com.optum.jenkins.pipeline.library@master') _

pipeline {
  agent {
    // your Docker template needs to have the azurecli, kubectl, and docker mixins loaded, see https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins for more info
    label 'docker-kitchensink-slave'
  }

  environment {
    PROJECT_NAME = 'gogo'
  }

  stages {
    stage ('Build') {
      steps {
        sh """"
	  . /etc/profile.d/jenkins.sh
	  CGO_ENABLED=0 GOOS=linux go build -o $env.PROJECT_NAME -a -tags netgo -ldflags '-w' .
        """
      }
    }

    stage ('Docker build & deploy') {
      steps {
        glAzureBuildDeployDocker loginServer:'ACR_URL',
          credentialsId:'ACR_CREDENTIALS',
          extraBuildOptions:'-t',
          image:"$env.PROJECT_NAME",
          tag:'latest'
      }
    }

    stage ('Deploy to k8 cluster') {
      steps {
        glAzureKubeDeploy resourceGroup:'K8_RESOURCE_GROUP',
          clusterName:'K8_CLUSTER',
          appName:"$env.PROJECT_NAME",
          loginServer:'ACR_URL',
          deployConfig:'deployment.yaml',
          azureClientId:'ARM_CLIENT_ID',
          azureClientSecret:'ARM_CLIENT_SECRET',
          azureTenant:'ARM_TENANT_ID'
      }
    }
  }

  post {
    always {
      echo 'This will always run'
    }
    success {
      echo 'This will run only if successful'
    }
    failure {
      echo 'This will run only if failed'
    }
    unstable {
      echo 'This will run only if the run was marked as unstable'
    }
    changed {
      echo 'This will run only if the state of the Pipeline has changed'
      echo 'For example, if the Pipeline was previously failing but is now successful'
    }
  }

}
