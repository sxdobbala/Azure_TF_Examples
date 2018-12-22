import com.optum.jenkins.pipeline.library.docker.Docker


/**
 * Create the Repository in the Docker Trusted Registry which is docker.optum.com by default
 *
 * @param dockerCredentialsId String Required Credentails to push to the Docker registry
 * @param dockerHost String Host name for the Docker registry - defaults to docker.optum.com
 * @param enableManifestLists boolean Feature to support multiple architectures (Windows/Linux) under the same tag - defaults to true
 * @param immutableTags boolean Whether tags can be overwritten - defaults to true
 * @param namespace String Required name of the image namespace (either user or organization)
 * @param repository String The name of the image that you are wanting to push to the repository.
 * @param scanOnPush boolean Whether security scans should be run on every push - defaults to false
 * @param visibility String If this is a public or private repository - defaults to public
 *
 *  The following passed in parameters
 * 		repository = "anthillagent"
 * 		namespace = "estarr"
 * 		dockerCredentialsId = "estarr_id"
 * Will result in a curl command that will create a repository
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the $namespace Docker Trusted Registry.
 *
 * */
def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.createDockerRepository(config)
}
