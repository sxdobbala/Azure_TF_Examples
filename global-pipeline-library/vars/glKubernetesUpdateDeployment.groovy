import com.optum.jenkins.pipeline.library.kubernetes.Kubernetes

/**
 * Updates a Kubernetes Deployment image, environment variable, or resources.
 * Takes deployment name and one or more of:
 * 		-resource limit and/or request variables
 * 		-environment variables
 * 		-container and image names
 *  and updates the corresponding deployment with the given resources.
 * @param credentials String ID in jenkins for kubernetes token.
 *      Example:  "k8s_token"
 * @param cluster String The cluster you wish to deploy too
 * 		Example: k8s-prod-ctc-aci.optum.com
 * @param namespace String The namesapce you wish to deploy to.
 * @param deploymentName String The name of the Kubernetes deployment to update.
 * 1 of the below 6 parameters are required
 * @param containerImages Map of container names and image names to update in the deployment.
 * @param envVars Map of environment variable keys and values to update in the deployment.
 * @param limitMem String The limit memory or upper bound of memory that the pod can consume.
 * @param limitCpu String The limit cpu or upper bound of cpu that the pod can consume.
 * @param requestMem String The request memory or amount of memory that is reserved for the pod.
 * @param requestCpu String The request cpu or amount of cpu that is reserved for the pod.
 * @param clusterPort String Overrides the default port at which the cluster is available
 *  def example = [
 * 		credentials:"k8s_token",
 * 		cluster:"k8s-prod-ctc-aci.optum.com",
 * 		namespace:"mynamespace',
 *      deploymentName:"deployment",
 *      containerImages:["nginx":"nginx:1.10", "busybox":"busybox:2.3.1"],
 *      envVars:["home":"/home/user","KUBECTL_VERSION":"1.10.0"],
 *      limitMem: "5Gi",
 *      limitCpu: "16",
 *      requestMem: "4Gi",
 *      requestCpu: "10"
 *  ]
 */

def call(Map<String, Object> config){
  Kubernetes k8s = new Kubernetes(this)
  k8s.update(config)
  
}
