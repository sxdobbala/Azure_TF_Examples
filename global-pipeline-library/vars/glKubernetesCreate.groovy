import com.optum.jenkins.pipeline.library.kubernetes.Kubernetes
/**
 * Deploys a Service and/or Deployment into kubernetes.
 * @param credentials String ID in jenkins for kubernetes token.
 *      Example:  "k8s_token"
 * @param cluster String The cluster you wish to deploy too
 * 		Example: k8s-prod-ctc-aci.optum.com
 * @param namespace String The namesapce you wish to deploy to.
 * @param deleteIfExists Boolean Deletes Deployment if it already exists before attempting to deploy again.
 * One of the below two is required:
 * @param deploymentYaml String The name of the deployment yaml file to deploy to Kubernetes.
 * @param serviceYaml String the name of the service yaml file to deploy to Kubernetes.
 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
 * @param times int Overrides how many times to poll OpenShift on whether the deployment is complete.
 * @param delay int Overrides how long it takes between poll attempts to OpenShift on whether the deployment is complete.
 * @param clusterPort String Overrides the default port at which the cluster is available
 *  def example = [
 * 		credentials:"k8s_token",
 * 		cluster:"k8s-prod-ctc-aci.optum.com",
 * 		namespace:"mynamespace',
 * 		deleteIfExists:"true",
 *     deploymentYaml:"deployment.yaml",
 *     serviceYaml: "service.yaml"
 *  ]
 */
def call(Map<String, Object> config){
  Kubernetes k8s = new Kubernetes(this)
  k8s.deploy(config)
  
}
