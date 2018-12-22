import com.optum.jenkins.pipeline.library.docker.Docker

/**
 * Builds Docker Image, Tags and then will Push the Image to the Docker Trusted Registry which is docker.optum.com by default
 *
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 * @param baseDir String Defaults to the current directory.
 * @param extraBuildOptions String
 * @param dockerCredentialsId String Required Credentails to push to the Docker registry
 * @param dockerHost String Defaults to docker.optum.com
 * @param extraPushOptions String Optional Push options.
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		dockerCredentialsId = "estarr_id"
 * 		tag = "docker.optum.com/estarr/anthillagent:${BUILD_NUMBER}" = "docker.optum.com/estarr/anthillagent:37"
 * Will result in the following docker commands
 * 	   	docker build --pull --tag docker.optum.com/estarr/anthillagent:37 .
 *     	docker push docker.optum.com/estarr/anthillagent:37
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the $namespace Docker Trusted Registry.
 *
 * */

def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.buildPushDockerImage(config)
}
