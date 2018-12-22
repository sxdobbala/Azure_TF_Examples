import com.optum.jenkins.pipeline.library.docker.Docker

/**
 * Tags Docker Image
 *
 * @param repository String The name of the image that you are wanting to build.
 * @param sourceTag String The unique identifer for the source to tag
 * @param sourceTag String The unique identifer for the source to tag
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		sourceTag = "estarr/antillagent:30"
 * 		destTag = "docker.optum.com/estarr/anthillagent:30"
 * Will result in the following docker commands
 *     	docker tag estarr/anthillagent:30 docker.optum.com/estarr/anthillagent:30
 *
 * */
def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.tagDockerImage(config)
}
