import com.optum.jenkins.pipeline.library.openshift.OpenShift

/**
 * Used for Image Promotion
 *
 * As the image is tagged, it will trigger a deployment with an image change trigger in the deployment config.
 *
 * @param tag HashMap of the tag instructions.
 *  Example:  ['prod':'rollback', 'dev':'prod']
 * @param ocpUrl String The server url for the OSEv3 instance.
 *  Example: "https://ose-ctc-core.optum.com"
 * @param project String The OSEv3 project name.
 * @param destinationProject String Project to promote the image to, if any.
 * @param credentials String Required Credentials to push to OSEv3
 * @param serviceName String Specify name of service if different than Docker image
 * @param annotations Map key/value to inject metadata annotations information for the openshift object
 *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
 *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *
 *  def example = [
 * 		tag:['prod':'rollback', 'dev':'prod'],
 * 		ocpUrl:"https://ose-ctc-core.optum.com",
 * 		project:"ocdtest',
 * 		destinationProject:"ocdprod",
 * 		credentials:"devopseng_tech",
 *         serviceName:"anthill",
 *    annotations: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *  ]
 *
 */


def call(Map<String, Object> config){
    OpenShift openshift = new OpenShift(this)
    openshift.tag(config)
}