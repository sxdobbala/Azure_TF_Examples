import com.optum.jenkins.pipeline.library.docker.Docker
/**
 * Builds Docker Image
 *
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 * @param baseDir String Defaults to the current directory.
 * @param extraBuildOptions String
 * @param extraFlagOptions String Optional flag options.
 * @param requirePull Boolean Optional Pull the base image? False may be
 *          necessary when base image is private and is pulled separately
 *
 *  The following passed in parameters
 * 		tag = "docker.optum.com/estarr/anthillagent:${BUILD_NUMBER}" = "docker.optum.com/estarr/anthillagent:37"
 * Will result in the following docker commands
 * 	   	docker build --pull --tag estarr/anthillagent:30 .
 *
 *
 * */
def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.buildDockerImage(config)
}