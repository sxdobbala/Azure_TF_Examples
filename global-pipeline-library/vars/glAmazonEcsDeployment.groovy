import com.optum.jenkins.pipeline.library.amazon.Amazon

/**
* Forces a new deployment of ECS cluster instance so that the instance's cluster uses the latest ECR Docker Image
* @param ecsCluster String The name of the ECS cluster that you are wanting to redeploy .
* @param ecsService String The name of the ECS cluster service that you are wanting to use latest docker image.
* @param credentialsId String Required Credentials to push to AWS.
* @param region String Your ECS's cluster region.
*        Example: 'us-east-1'
*/
def call(Map<String, Object> config){
  Amazon amazon = new Amazon(this)
  amazon.ecsForceDeployment(config)
}
