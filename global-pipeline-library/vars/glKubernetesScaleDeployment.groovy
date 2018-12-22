import com.optum.jenkins.pipeline.library.kubernetes.Kubernetes

/**
* Scale a Deployment in Kubernetes to desired number of replicas.
* @param credentials String ID in jenkins for kubernetes token.
*      Example:  "k8s_token"
* @param cluster String The cluster you wish to deploy too
* 		Example: k8s-prod-ctc-aci.optum.com
* @param namespace String The namesapce you wish to deploy to.
* @param deploymentName String The name of the deployment to expose.
* @param replicas String Number of replicas to scale to for provided deployment.
* @param wait Boolean Whether to wait for deployment to scale up or not.
* @param times int Overrides how many times to poll Kubernetes on whether the deployment is complete.
* @param delay int Overrides how long it takes between poll attempts to Kubernetes on whether the deployment is complete.
* @param clusterPort String Overrides the default port at which the cluster is available
*  def example = [
* 		credentials:"k8s_token",
* 		cluster:"k8s-prod-ctc-aci.optum.com",
* 		namespace:"mynamespace',
*		deploymentName: "deployment",
*		replicas: "3",
*       wait: true
*  ]
*/

def call(Map<String, Object> config){
  Kubernetes k8s = new Kubernetes(this)
  k8s.scaleDeployment(config)
  
}
