import com.optum.jenkins.pipeline.library.openshift.OpenShift

/**
 * Takes an image from the Docker Trusted Registy and deploys it to an OpenShift application.
 *
 * @param dockerImage String The name of the docker image that you are wanting to build.
 *      Example:  "docker.optum.com/devops_engineering/anthillagent:${BUILD_NUMBER}"
 * @param ocpUrl String The server url for the OSEv3 instance.
 *      Example: "https://ose-ctc-core.optum.com"
 * @param project String The OSEv3 project name.
 * @param port String The port to EXPOSE
 * @param credentials String Required Credentials to push to OSEv3
 * @param serviceName String Specify name of service if different than Docker image
 * @param tag String Specify name of image stream tag
 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
 * @param times int Overrides how many times to poll OpenShift on whether the deployment is complete.
 * @param delay int Overrides how long it takes between poll attempts to OpenShift on whether the deployment is complete.
 * @param annotations Map key/value to inject metadata annotations information for the openshift object.
 *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
 *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *
 *  def example = [
 * 		dockerImage:"docker.optum.com/devops_engineering/anthillagent",
 * 		ocpUrl:"https://ose-ctc-core.optum.com",
 * 		project:"ocdtest',
 * 		credentials:"devopseng_tech",
 *     serviceName:"anthill",
 *     annotations: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *  ]
 */

def call(Map<String, Object> config){
  OpenShift openshift = new OpenShift(this)
  openshift.deploy(config)
}
