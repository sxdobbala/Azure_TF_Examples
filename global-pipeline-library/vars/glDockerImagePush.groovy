import com.optum.jenkins.pipeline.library.docker.Docker

/**
 * Push the Docker image to the registry defined in tag parameter
 *
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 * @param dockerCredentialsId String Required Credentails to push to the DTR
 * @param dockerHost String Defaults to docker.optum.com
 * @param extraPushOptions String Optional Push options.
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		tag = "docker.optum.com/estarr/anthillagent:30"
 * Will result in the following docker commands
 *     	docker push docker.optum.com/estarr/anthillagent:30
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the target image repository.
 *
 * */
def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.pushDockerImage(config)
}
