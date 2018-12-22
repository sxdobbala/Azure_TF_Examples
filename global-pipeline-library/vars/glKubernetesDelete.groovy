import com.optum.jenkins.pipeline.library.kubernetes.Kubernetes

/**
* Deletes a service and/or deployment from Kubernetes
* Takes yaml files or deployment/service names and deletes the corresponding objects from kubernetes.
* @param credentials String ID in jenkins for kubernetes token.
*      Example:  "k8s_token"
* @param cluster String The cluster you wish to deploy too
* 		Example: k8s-prod-ctc-aci.optum.com
* @param namespace String The namesapce you wish to deploy to.
* 1 or more of the following 4 params is required.
* @param deploymentYaml String The name of the yaml file to delete from Kubernetes that contains a deployment.
* @param deploymentName String The name of the deployment to delete from Kubernetes.
* @param serviceYaml String The name of the yaml file to delete from Kubernetes that contains a service.
* @param serviceName String The name of the service to delete from Kubernetes.
* @param clusterPort String Overrides the default port at which the cluster is available
*  def example = [
* 		credentials:"k8s_token",
* 		cluster:"k8s-prod-ctc-aci.optum.com",
* 		namespace:"mynamespace',
*       deploymentYaml:"deployment.yaml",
*  ]
*/

def call(Map<String, Object> config){
  Kubernetes k8s = new Kubernetes(this)
  k8s.delete(config)
  
}
