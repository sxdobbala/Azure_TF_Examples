#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.amazon

class Amazon implements Serializable {
  def jenkins

  Amazon() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Amazon(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Builds your Docker file and pushes it up to AWS ECR
 * @param ecrName String The name of your ECR where your image is stored that you are wanting to build.
 * @param ecrUrl String The ECR url for the docker image instance, without the https://.
 *        Example: '1234567890.dkr.ecr.us-east-1.amazonaws.com'
 * @param region String Your ECR's region.
 *        Example: 'us-east-1'
 * @param credentials String Required Jenkins Credentials to push to AWS.
 * @param tag String Specify name of image stream tag
 */

  def ecrBuildPush(Map<String, Object> params) {
    def defaults = [
      ecrName      : null, // required
      ecrUrl       : null, // required
      region       : null, // required for AWS
      credentialsId: null, // required for AWS; should be the AWScredential id store inside of Jenkins, must of the type AWS.
      tag          : 'latest' // optional
    ]
    def config = defaults + params
    jenkins.echo "AWS ECR Login arguments: $config"

    if (!config.ecrName) {
      jenkins.error "ecrImage required, should be the same as ecr name"
    }

    if (!config.ecrUrl) {
      jenkins.error "ecrUrl required, must be the ecr url without https://"
    }

    if (!config.region) {
      jenkins.error "Region required, should the region where your ecr is located"
    }

    if (!config.credentialsId) {
      jenkins.error "Credential id required, should be store in Jenkins"
    }

    jenkins.echo "Building and pushing docker image"

    jenkins.withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId,
                              usernameVariable: 'AWS_ACCESS_KEY', passwordVariable: 'AWS_SECRET_KEY']]) {

      def awsCred = """

          aws configure set aws_access_key_id ${jenkins.env.AWS_ACCESS_KEY}
          aws configure set aws_secret_access_key ${jenkins.env.AWS_SECRET_KEY}
          aws configure set region $config.region
          """
      jenkins.command(awsCred)

      def buildpushImage = """

            aws ecr get-login --no-include-email --region $config.region | sh

            docker build -t $config.ecrName .

            docker tag $config.ecrName:$config.tag $config.ecrUrl/$config.ecrName:$config.tag

            docker push $config.ecrUrl/$config.ecrName:$config.tag

          """
      jenkins.command(buildpushImage)
    }

  }

  /**
   * Forces a new deployment of ECS cluster instance so that the instance's cluster uses the latest ECR Docker Image
   * @param ecsCluster String The name of the ECS cluster that you are wanting to redeploy .
   * @param ecsService String The name of the ECS cluster service that you are wanting to use latest docker image.
   * @param credentialsId String Required Credentials to push to AWS.
   * @param region String Your ECS's cluster region.
   *        Example: 'us-east-1'
   */

  def ecsForceDeployment(Map<String, Object> params) {

    def defaults = [
      ecsCluster   : null, // required for AWS
      ecsService   : null, // required for AWS
      credentialsId: null, // required for AWS; should be the credential id store inside of Jenkins
      region       : null //required for AWS
    ]

    def config = defaults + params
    jenkins.echo "AWS ECS Login arguments: $config"

    if (!config.ecsCluster) {
      jenkins.error "ecsCluster required, should be your the name of your ECS Cluster"
    }

    if (!config.ecsService) {
      jenkins.error "ecsService required, must be the service from the cluster specified above"
    }

    if (!config.credentialsId) {
      jenkins.error "Credential id required, should be store in Jenkins"
    }

    if (!config.region) {
      jenkins.error "Region required, should the region where your AWS ECS is located"
    }

    jenkins.echo "Setting AWS variables, signing into AWS"

    jenkins.withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId,
                              usernameVariable: 'AWS_ACCESS_KEY', passwordVariable: 'AWS_SECRET_KEY']]) {

      def awsECSScripts = """

            aws configure set aws_access_key_id ${jenkins.env.AWS_ACCESS_KEY}
            aws configure set aws_secret_access_key ${jenkins.env.AWS_SECRET_KEY}
            aws configure set region $config.region

            aws ecs update-service --cluster $config.ecsCluster --service $config.ecsService --force-new-deployment

        """
      jenkins.command(awsECSScripts)

      jenkins.echo "Done updating services, cluster deploying"

    }

  }
}
