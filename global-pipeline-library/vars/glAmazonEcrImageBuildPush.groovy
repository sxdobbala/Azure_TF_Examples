import com.optum.jenkins.pipeline.library.amazon.Amazon

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
def call(Map<String, Object> config){
  Amazon amazon = new Amazon(this)
  amazon.ecrBuildPush(config)
}
