#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.azure

import com.optum.jenkins.pipeline.library.utils.docker.DockerCapabilities

class Azure implements Serializable {
  def jenkins

  Azure() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
        'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Azure(jenkins) {
    this.jenkins = jenkins
  }

  /**
   * Build and deploy a Docker image to an Azure Container Registry (ACR)
   * This assumes that you already have an Azure Container Registry stood up in your subscription, see this Terraform module:
   * https://github.optum.com/CommercialCloud-EAC/azure_container_registry
   *
   * @param loginServer String Login server name for the Azure Container Registry
   * @param credentialsId String Credentials to push to your ACR, should point to Jenkins credentials id
   * @param image String Name of your docker image; An image name may contain lowercase and uppercase letters,
   *          digits, underscores, periods and dashes. An image name may not start with a period or a dash and may
   *          contain a maximum of 128 characters.
   * @param tag String The unique identifier for this instance of the image, defaults to Jenkins build number; same
   *          requirements as image name
   * @param dockerVersion String Optional docker version (in the form xx.xx) used for docker CLI commands (e.g., pushing image to DTR)
   * @param baseDir String Directory of Dockerfile, defaults to the current directory
   * @param extraBuildOptions String Optional Push options
   * @param requirePull Boolean Pull the base image? False may be necessary when base image is
   *          private and is pulled separately
   *
   * Build usage : docker build [OPTIONS] PATH | URL | -
   * Tag usage   : docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
   * Login usage : docker login [OPTIONS] [SERVER]
   * Push usage  : docker push [OPTIONS] NAME[:TAG]
   */
  def buildDeployDocker(Map<String, Object> params) {
    def defaults = [
      loginServer       : null, // required
      credentialsId     : null, // required
      image             : null, // required
      tag               : "${jenkins.env.BUILD_NUMBER}",   // optional
      dockerVersion     : "${jenkins.env.DOCKER_VERSION}", // optional (drives if --password_stdin (preferred security best practice) can be used for docker login)
      baseDir           : '.', // optional
      extraBuildOptions : '', // optional
      extraPushOptions  : '', //optional
      requirePull       : true, // optional
    ]
    def config = defaults + params
    jenkins.echo "buildDeployDocker args: $config"

    if (!config.loginServer) {
      jenkins.error "The login server is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.credentialsId) {
      jenkins.error "The credentials id to push to azure container registry is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.image) {
      jenkins.error "The image name is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.dockerVersion || config.dockerVersion == "null") {
      config.dockerVersion = "" // Not set in ENV -- revert to blank
    }

    String pullOption = config.requirePull ? '--pull' : ''

    // build image
    jenkins.command(
      "docker build $pullOption ${config.extraBuildOptions} ${config.image}:${config.tag} ${config.baseDir}"
    )

    // tag image
    jenkins.command(
      "docker tag ${config.image}:${config.tag} ${config.loginServer}/${config.image}:${config.tag}"
    )

    // azure login, push image
    jenkins.withCredentials([[$class           : 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId,
                              usernameVariable : 'AZURE_USER', passwordVariable: 'AZURE_PASS']]) {

      // https://github.optum.com/jenkins-pipelines/global-pipeline-library/issues/322
      // Docker best practices note that using --password (-p) via the CLI is insecure.
      def loginCmd = "docker login -u ${jenkins.env.AZURE_USER} -p '${jenkins.env.AZURE_PASS}' ${config.loginServer}"

      if (DockerCapabilities.supportsPasswordStdin(config.dockerVersion)) {
        // use login with --password-stdin
        loginCmd = "echo -n '${jenkins.env.AZURE_PASS}' | docker login -u ${jenkins.env.AZURE_USER} --password-stdin ${config.loginServer}"
      }

      def pushCmd = "docker push ${config.extraPushOptions} ${config.loginServer}/${config.image}:${config.tag}"
      jenkins.command(loginCmd)
      jenkins.command(pushCmd)
    }
  }

  /**
   * Deploy a Docker image in your Azure Container Registry to a Kubernetes cluster
   * Assumes you already have a Container service set up in a resource group on an Azure subscription, see this
   * Terraform module: https://github.optum.com/CommercialCloud-EAC/azure_aks
   *
   * @param resourceGroup String Name of the Azure resource group your Kubernetes cluster runs on
   * @param clusterName String Name of the Kubernetes cluster
   * @param deployConfig String The path to the yaml template in your source control/Jenkins workspace
   * @param appName String Name of your application to be deployed and updated in your Kubernetes cluster
   *          Used to check if a deployment exists so we can update your Kubernetes deployment, or create a deployment
   * @param loginServer String Login server name for the Azure Container Registry
   * @param azureClientId String Your Azure ARM Client ID, points to a StringBinding Jenkins credential
   * @param azureClientSecret String Your Azure ARM Client Secret, points to a StringBinding Jenkins credential
   * @param azureTenant String The tenant value is the Azure Active Directory tenant associated with the service principal,
   *          points to a StringBinding Jenkins credential
   *
   */
  def deploy(Map<String, Object> params) {
    def defaults = [
      resourceGroup     : null, // required
      clusterName       : null, // required
      deployConfig      : null, // required
      appName           : null, // required
      loginServer       : null, // required
      azureClientId     : null, // required
      azureClientSecret : null, // required
      azureTenant       : null, // required
      containerName     : null, // optional
      imageName         : null, // optional
      imageTag          : null  // optional
    ]
    def config = defaults + params
    jenkins.echo "deploy args: $config"

    if (!config.resourceGroup) {
      jenkins.error "The resource group is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.clusterName) {
      jenkins.error "The name of your Kubernetes cluster is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.deployConfig) {
      jenkins.error "The path to your yaml deployment config must be specified; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.appName) {
      jenkins.error "The name of your app is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.loginServer) {
      jenkins.error "The login server is required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.azureClientId) {
      jenkins.error "Azure client id required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.azureClientSecret) {
      jenkins.error "Azure client secret required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.azureTenant) {
      jenkins.error "Azure tenant id required; see https://github.optum.com/jenkins-pipelines/global-pipeline-library/tree/master/examples/azure"
    }
    if (!config.containerName) {
      config.containerName = config.appName
    }
    if (!config.imageName) {
      config.imageName = config.appName
    }
    if (!config.imageTag) {
      config.imageTag = "latest"
    }

    // azure login
    jenkins.withCredentials([[$class: 'StringBinding', credentialsId: config.azureClientId, variable: 'ARM_CLIENT_ID'],
                             [$class: 'StringBinding', credentialsId: config.azureClientSecret, variable: 'ARM_CLIENT_SECRET'],
                             [$class: 'StringBinding', credentialsId: config.azureTenant, variable: 'ARM_TENANT_ID']]) {
      jenkins.command(
        "az login --service-principal -u ${jenkins.env.ARM_CLIENT_ID} -p '${jenkins.env.ARM_CLIENT_SECRET}' --tenant ${jenkins.env.ARM_TENANT_ID}"
      )
    }

    // set kube credentials
    jenkins.command(
      "az aks get-credentials --resource-group ${config.resourceGroup} --name ${config.clusterName}"
    )

    // check if deployment exists
    // deploy if deployment doesn't exist, and update the image if deployment does exist
    def deployCmd = """
      if kubectl get deployment ${config.appName} | grep -w '${config.appName}'; then
        echo "Deployment exists, updating image..."
        kubectl set image deployment/${config.appName} ${config.containerName}=${config.loginServer}/${config.imageName}:${config.imageTag}
      else
        echo "Deployment does not exist, applying config..."
        kubectl apply -f ${config.deployConfig}
      fi
        echo "Deployment or redeployment complete"
    """
    jenkins.command(deployCmd)
  }
}
