import com.optum.jenkins.pipeline.library.openshift.OpenShift

/**
 * Takes a Dockerfile from a Jenkins workspace and runs it on OpenShift.
 *
 * @param credentials String Required Credentials to push to OSEv3
 * @param project String The OSEv3 project name.
 * @param ocpUrl String The server url for the OSEv3 instance.
 *      Example: "https://ose-ctc-core.optum.com"
 * @param path String The path to the Dockerfile within the Jenkins workspace.
 * @param serviceName String Specify name of service if different than Docker image
 * @param port String The port to EXPOSE
 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
 * @param times int Overrides how many times to poll OpenShift on whether the deployment is complete.
 * @param delay int Overrides how long it takes between poll attempts to OpenShift on whether the deployment is comp
 * @param annotations Map key/value to inject metadata annotations information for the openshift object
 *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
 *
 *
 */

def call(Map<String, Object> config){
    OpenShift openshift = new OpenShift(this)
    openshift.buildAndRun(config)
}