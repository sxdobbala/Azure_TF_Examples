import com.optum.jenkins.pipeline.library.openshift.OpenShift

/**
 * Deletes all of the resources from an OpenShift application.
 * oc delete all -l app=$OCP_APP
 *
 * @param dockerImage String The name of the docker image that you are wanting to build.
 * @param serverUrl String The server url for the OSEv3 instance.
 * @param projectName String The OSEv3 project name.
 * @param credentials String Required Credentials to push to OSEv3
 *
 * Example:
 *     deleteConfig = [
 *   		dockerImage: 'docker.optum.com/devops_engineering/anthillagent',
 * 	  	projectName: 'ocdtest',
 * 	  	credentials: 'devopseng_tech',
 *     ]
 *     OpenShift ose = new OpenShift(this)
 *     ose.deleteServiceResources(deleteConfig)
 *
 */
def call(Map<String, Object> config){
  OpenShift openshift = new OpenShift(this)
  openshift.deleteServiceResources(config)
}
