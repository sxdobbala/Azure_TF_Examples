import com.optum.jenkins.pipeline.library.kubernetes.Kubernetes

/**
 * Exposes a Deployment in Kubernetes with Type NodePort.
 * @param credentials String ID in jenkins for kubernetes token.
 *      Example:  "k8s_token"
* @param cluster String The cluster you wish to deploy too
* 		Example: k8s-prod-ctc-aci.optum.com
 * @param namespace String The namesapce you wish to deploy to.
 * @param deploymentName String The name of the deployment to expose.
 * @param port String the name port to expose within the container.
 * @param clusterPort String Overrides the default port at which the cluster is available
 *  def example = [
 * 		credentials:"k8s_token",
 * 		cluster:"k8s-prod-ctc-aci.optum.com",
 * 		namespace:"mynamespace',
 *		deploymentNmae: "deployment",
 *		port: "8080"
 *  ]
 */

def call(Map<String, Object> config){
  Kubernetes k8s = new Kubernetes(this)
  k8s.exposeDeployment(config)
  
}
