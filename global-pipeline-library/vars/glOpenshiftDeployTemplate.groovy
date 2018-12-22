import com.optum.jenkins.pipeline.library.openshift.OpenShift

/**
 * Takes a OpenShift template from a Jenkins workspace and runs it on OpenShift.
 *
 * @param credentials String Required Credentials to push to OSEv3
 * @param project String The OSEv3 project name.
 * @param ocpUrl String The server url for the OSEv3 instance.
 *      Example: "https://ose-ctc-core.optum.com"
 * @param templateFile String The path to the yaml template within the Jenkins workspace.
 *      Example: "deploy-to-openshift.yaml"
 * @param templateParams Array of paramaters to pass to template file in key:value format
 *      Example: "["RUNTIME_ENVIRONMENT":"prod", "DOCKER_TAG":"${DOCKER_TAG}"]"
 *  @param templateParamsFile File containing template parameters, requires OC 3.5
 *        Example: "params.properties"
 * @param annotations Map key/value to inject metadata annotations information for the openshift object
 *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
 *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *
 */

def call(Map<String, Object> config){
  OpenShift openshift = new OpenShift(this)
  openshift.processTemplate(config)
}
