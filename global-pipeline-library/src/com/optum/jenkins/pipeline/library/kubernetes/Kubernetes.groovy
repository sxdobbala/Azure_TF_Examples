#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.kubernetes

class Kubernetes implements Serializable{
	def jenkins
	Kubernetes() throws Exception {
		throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
		'This enables access to jenkinsfile steps, global variables, functions and envvars.')
	}

	Kubernetes(jenkins) {
		this.jenkins = jenkins
	}

	/**
	 * Takes in params for deploymentKubernetes and/or serviceKubernetes.
	 * @param credentials String ID in jenkins for kubernetes token.
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deleteIfExists Boolean Deletes Deployment if it already exists before attempting to deploy again.
	 * One of the below two is required:
	 * @param deploymentYaml String The name of the deployment yaml file to deploy to Kubernetes.
	 * @param serviceYaml String the name of the service yaml file to deploy to kubernetes.
	 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
	 * @param times int Overrides how many times to poll OpenShift on whether the deployment is complete.
	 * @param delay int Overrides how long it takes between poll attempts to OpenShift on whether the deployment is complete. 
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 * 		deleteIfExists:"true",
	 *     deploymentYaml:"deployment.yaml",
	 *     serviceYaml: "service.yaml"
	 *  ]
	 */

	def deploy(Map<String, Object> params) {
		jenkins.echo "Kubernetes arguments: $params"
		if(params.containsKey('serviceYaml')){
			jenkins.echo "Deploying Service from file $params.serviceYaml"
			serviceKubernetes(params)
		}
		if(params.containsKey('deploymentYaml')){
			jenkins.echo "Deploying Deployment from file $params.deploymentYaml"
			deploymentKubernetes(params)
		}

	}

	/**
	 * Takes a yaml file and deploys it as a kubernetes deployment.
	 * @param credentials String ID in jenkins for kubernetes token.
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to.
	 * @param deleteIfExists Boolean Deletes Deployment if it already exists before attempting to deploy again.
	 * @param deploymentYaml String The name of the yaml file to deploy to Kubernetes.
	 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
	 * @param times int Overrides how many times to poll Kubernetes on whether the deployment is complete.
	 * @param delay int Overrides how long it takes between poll attempts to Kubernetes on whether the deployment is complete.
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"ctc",
	 * 		namespace:"mynamespace',
	 * 		deleteIfExists:"true",
	 *      deploymentYaml:"deployment.yaml",
	 *  ]
	 */
	def deploymentKubernetes(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, //required
			namespace: null, //required
			deleteIfExists: false, // optional
			deploymentYaml: null, // required
			wait: false, //optional
			times: 10, //optional
			delay: 10, //optional
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: config.credentials, variable: 'TOKEN']]) {
			if(config.deleteIfExists){
				def checkIfExists="""
					kubectlParams="--server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true"
					echo \"Checking if deployment exists...lines in get deployment=`kubectl get -f $config.deploymentYaml \$kubectlParams -n $config.namespace | wc -l`\"
                """
				def response=jenkins.command(checkIfExists, true, '#/bin/bash +x')
				if(response.contains('lines in get deployment=2')){
					jenkins.echo 'Deployment Exists Deleting!'
					deleteDeploymentFromYaml(config)
				}
			}
			def launchDeploy= """
				namespace=$config.namespace
				kubectlParams="--server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true"
				kubectl create -f $config.deploymentYaml \$kubectlParams -n \$namespace | awk '{print \$2}' | sed -e \'s/^\"//\' -e \'s/\"\$//'
			"""
					def deploymentName=jenkins.command("${launchDeploy}".toString(), true, '#!/bin/bash +x').trim()
			if(config.wait){
				wait(config, deploymentName)
			}
		}

	}

	/**
	 * Function to check if deployment has finished deploying or not. Prints out deployment, replica set, and pod statuses if it fails and pod status if it succeeds.
	 * @param credentials String ID in jenkins for kubernetes token.
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to.
	 * @param times int Overrides how many times to poll Kubernetes on whether the deployment is complete.
	 * @param delay int Overrides how long it takes between poll attempts to Kubernetes on whether the deployment is complete.
	 * @param deploymentName String The name of the deployment to wait on completion. 
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      times: 10,
	 *      delay: 10
	 *  ]
	 *  def deploymentName = "deployment"
	 */

	def wait(Map<Object, String> params, String deployment){
		def defaults = [
			credentials: null,  // required
			cluster: null, //required
			namespace: null, //required
			times: 10, //required
			delay: 10,  //required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		def deploymentName = deployment
		def waitScript="""
	namespace=$config.namespace
	kubectlParams="--server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true"
	status=incomplete
	   for i in {1..$config.times}
	   do
		  response=`kubectl get deploy \$kubectlParams   -n $config.namespace | grep $deploymentName`
		  available=`echo \$response | awk '{print \$5}'`
		  desired=`echo \$response | awk '{print \$2}'`
		  status="incomplete"
		  if [ \$available -eq \$desired ]; then
			  echo "Deployment Complete"
			  status="complete"
			  break
		  else
			  echo "\$available of \$desired pods have started in deployment"
		  fi
		  sleep $config.delay
	  done
	  if [ \$status == "incomplete" ]; then
		echo \"Deployment not completed...Printing describe command for pods.\"
		pods=`kubectl get pods \$kubectlParams -o=custom-columns=name:.metadata.name,uid:.metadata.ownerReferences -n \$namespace |grep "\$(kubectl get rs \$kubectlParams -o=custom-columns=name:.metadata.name,uid:.metadata.uid -n \$namespace | grep $deploymentName | awk '{print \$2}')" | awk '{print \$1}'`
		for pod in \$pods
		do
			kubectl describe po \$pod \$kubectlParams -n \$namespace
			echo \"-----------------------------------------------------------------------------------------------------\"
		done
		echo \"-----------------------------------------------------------------------------------------------------\"
		echo \"Deployment not completed...Printing describe command for deployment.\"
		kubectl describe deploy $deploymentName \$kubectlParams -n \$namespace
		echo \"-----------------------------------------------------------------------------------------------------\"
		echo \"Deployment not completed...Printing describe command for replicaset.\"
		kubectl describe rs `kubectl get rs \$kubectlParams -o=custom-columns=name:.metadata.name,uid:.metadata.uid,desired:.status.replicas -n \$namespace | grep $deploymentName | grep -v 0| awk '{print \$1}'` \$kubectlParams -n \$namespace
		exit 1
	  else
		echo "Deployment Succeeded!!! Printing describe command for pods."
		pods=`kubectl get pods \$kubectlParams -o=custom-columns=name:.metadata.name,uid:.metadata.ownerReferences -n \$namespace |grep "\$(kubectl get rs \$kubectlParams -o=custom-columns=name:.metadata.name,uid:.metadata.uid -n \$namespace | grep $deploymentName | awk '{print \$2}')" | awk '{print \$1}'`
		for pod in \$pods
		do
			kubectl describe po \$pod \$kubectlParams -n \$namespace
			echo \"-----------------------------------------------------------------------------------------------------\"
		done
	  fi
"""
		jenkins.command("${waitScript}".toString(), false, '#!/bin/bash +x')

	}

	/**
	 * Takes a yaml file and deploys it as a kubernetes service.
	 * @param credentials String ID in jenkins for kubernetes token.
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to.
	 * @param serviceYaml String The name of the yaml file to deploy to Kubernetes.
	 * @param wait Boolean Waits for deployment to complete if it is available for that platform.
	 * @param times int Overrides how many times to poll Kubernetes on whether the deployment is complete.
	 * @param delay int Overrides how long it takes between poll attempts to Kubernetes on whether the deployment is complete.
	 * @param clusterPort String The port at which the cluster is available
   *
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 * 		deleteIfExists:"true",
	 *     serviceYaml:"service.yaml",
	 *  ]
	 */

	def serviceKubernetes(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, //required
			namespace: null, //required
			serviceYaml: null, //required
			wait: false, //optional
			times: 10, //optional
			delay: 10,  //optional
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: config.credentials, variable: 'TOKEN']]) {
			
			def checkIfExists="""
					kubectlParams="--server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true"
					echo \"Checking if service exists...lines in get service=`kubectl get -f $config.serviceYaml \$kubectlParams -n $config.namespace | wc -l`\"
                """
			def response=jenkins.command(checkIfExists, true, '#/bin/bash +x')
			if(response.contains('lines in get service=2')){
				jenkins.echo "Service in $config.serviceYaml already exists"
			}
			else{
			def launchService="""
				serviceName=`kubectl create -f $config.serviceYaml --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $params.namespace | awk '{ print \$2}' | sed -e 's/^"//' -e 's/"\$//'`
				kubectl describe svc \$serviceName -n $config.namespace --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true
			"""
			jenkins.command("${launchService}".toString(), false, '#!/bin/bash +x')
			}

		}
	}

	/**
	 * Exposes a Deployment in Kubernetes with Type NodePort.
	 * @param credentials String ID in jenkins for kubernetes token.
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to.
	 * @param deploymentName String The name of the deployment to expose.
	 * @param port String the name port to expose within the container.
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *		deploymentName: "deployment",
	 *		port: "8080"
	 *  ]
	 */
	def exposeDeployment(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, //required
			namespace: null, //required
			deploymentName: null, //required
			port: null, // required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: config.credentials, variable: 'TOKEN']]) {
			def exposeDeployment="""
				kubectl get svc $config.deploymentName -n $config.namespace --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true
				if [ \$? -eq 0 ]; then
					echo "Deployment Already Exposed"
				else
					kubectl expose deployment $config.deploymentName --port $config.port --type NodePort -n $config.namespace --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true
					if [ \$? -eq 0 ]; then
						echo "Deployment Exposed!"
					else
						echo "Deployment Exposure Failed"
						exit 1
					fi
				fi
				echo "Printing Service Details"
				kubectl describe svc $config.deploymentName -n $config.namespace --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true
			"""
			jenkins.command("${exposeDeployment}".toString(), false, '#!/bin/bash +x')

		}

	}

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
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *		deploymentName: "deployment",
	 *		replicas: "3",
	 *	    wait: true
	 *  ]
	 */

	def scaleDeployment(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, //required
			namespace: null, //required
			deploymentName: null, //required
			replicas: null, // required
			wait: false, //optional
			times: 10, //optional
			delay: 10, //optional
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: config.credentials, variable: 'TOKEN']]) {
			def scaleDeployment="""
				kubectl scale --replicas $config.replicas deployment/$config.deploymentName -n $config.namespace --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true
				 if [ \$? -eq 0 ]; then
					echo "Deployment Scaled to $config.replicas replicas!"
				else
					echo "Deployment Failed to Scale"
					exit 1
				fi
			"""
			jenkins.command("${scaleDeployment}".toString(), false, '#!/bin/bash +x')
			if(config.wait){
				wait(config, config.deploymentName)
				}
		}

	}

	/**
	 * Takes yaml files or deployment/service names and deletes the corresponding objects from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * 1 or more of the following 4 params is required
	 * @param deploymentYaml String The name of the yaml file to delete from Kubernetes that contains a deployment
	 * @param deploymentName String The name of the deployment to delete from Kubernetes
	 * @param serviceYaml String The name of the yaml file to delete from Kubernetes that contains a service
	 * @param serviceName String The name of the service to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       deploymentYaml:"deployment.yaml",
	 *  ]
	 */
	def delete(Map<String, Object> params){
		if(params.containsKey('serviceName')||params.containsKey('serviceYaml'))
			service(params)
		if(params.containsKey('deploymentName')||params.containsKey('deploymentYaml'))
			deployment(params)

	}

	/**
	 * Takes yaml file or service name and deletes the corresponding service from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * 1 or more of the following 2 params is required
	 * @param serviceYaml String The name of the yaml file to delete from Kubernetes that contains a service
	 * @param serviceName String The name of the service to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       serviceYaml:"service.yaml",
	 *  ]
	 */

	def service(Map<String, Object> params){
		if(params.containsKey('serviceName')){
			deleteService(params)
		}
		else
			deleteServiceFromYaml(params)

	}

	/**
	 * Takes yaml file or deployment names and deletes the corresponding deployment from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * 1 or more of the following 2 params is required
	 * @param deploymentYaml String The name of the yaml file to delete from Kubernetes that contains a deployment
	 * @param deploymentName String The name of the deployment to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       deploymentYaml:"deployment.yaml",
	 *  ]
	 */
	def deployment(Map<String, Object>params){
		if(params.containsKey('deploymentName')){
			deleteDeployment(params)
		}
		else
			deleteDeploymentFromYaml(params)

	}

	/**
	 * Takes a yaml file and deletes the corresponding service from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param serviceYaml String The name of the yaml file to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       serviceYaml:"service.yaml",
	 *  ]
	 */

	def deleteServiceFromYaml(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			serviceYaml: null,  // required
			namespace: null, //required
			cluster: null, //required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.echo "Deleting Kubernetes service from file: $config.serviceYaml "
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl delete -f $config.serviceYaml --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')
		}

	}

	/**
	 * Takes a yaml file and deletes the corresponding service from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentYaml String The name of the yaml file to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       deploymentYaml:"deployment.yaml",
	 *  ]
	 */

	def deleteDeploymentFromYaml(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			deploymentYaml: null,  // required
			namespace: null, //required
			cluster: null, //required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.echo "Deleting Kubernetes deployment from file: $config.deploymentYaml "
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl delete -f $config.deploymentYaml  --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, "#!/bin/bash +x")
		}

	}

	/**
	 * Takes a service name and deletes the corresponding service from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param serviceName String The name of the service to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *       serviceName:"service",
	 *  ]
	 */

	def deleteService(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			serviceName: null,  // required
			namespace: null, //required
			cluster: null, //required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.echo "Deleting Kubernetes service $config.serviceName"
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl delete svc $config.serviceName --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true  -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')
			
		}
		
	}

	/**
	 * Takes a deployment name and deletes the corresponding deployment from kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentName String The name of the deployment to delete from Kubernetes
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      deploymentName: "deployment",
	 *  ]
	 */

	def deleteDeployment(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			deploymentName: null,  // required
			namespace: null, //required
			cluster        : null, //required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.echo "Deleting Kubernetes service $config.deploymentName"

		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl delete deploy $config.deploymentName  --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')

		}

	}

	/**
	 * Takes deployment name and a list of container and image names and updates the corresponding 
	 * deployment's containers with the given images in kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentName String The name of the Kubernetes deployment to update
	 * @param containerImages Map of container names and image names to update in the deployment
   * @param clusterPort String The port at which the cluster is available
	 * 		Example: ["nginx":"nginx1.10", "busybox":"busybox2.3.1"]
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      deploymentName:"deployment",
	 *      containerImages:["nginx":"nginx1.10", "busybox":"busybox2.3.1"]
	 *  ]
	 */

	def updateImageDeployment(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, // required
			namespace: null, //required
			deploymentName: null,  // required
			containerImages: [:], // required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		jenkins.echo "Updating Kubernetes deployment $config.deploymentName"
		def containerImages = ''
		config?.containerImages?.each{ container, image ->
			containerImages += " ${container}=${image}"
		}
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl set image deployment/$config.deploymentName $containerImages --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')

		}

	}

	/**
	 * Takes deployment name and a list of environment variable keys and values and updates the corresponding
	 * deployment with the given environment variables in kubernetes.
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentName String The name of the Kubernetes deployment to update
	 * @param envVars Map of environment variable keys and values to update in the deployment
   * @param clusterPort String The port at which the cluster is available
	 * 		Example: ["home":"/home/user","KUBECTL_VERSION":"1.10.0"]
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      deploymentName:"deployment",
	 *      envVars:["home":"/home/user","KUBECTL_VERSION":"1.10.0"]
	 *  ]
	 */

	def updateEnvVarsDeployment(Map<String, Object> params){
		def defaults = [
			credentials   : null,  // required
			cluster : null, // required
			namespace: null, //required
			deploymentName   : null,  // required
			envVars: [:], // required
      clusterPort: 16443 //optional
		]
		def config = defaults + params
		def env = ''
		config?.envVars?.each{ envKey, envVar ->
			env += " ${envKey}=${envVar} "
		}
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl set env deployment/$config.deploymentName $env --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')

		}

	}

	/**
	 * Takes deployment name and resource limit and/or request variables and updates the corresponding
	 * deployment with the given resources
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentName String The name of the Kubernetes deployment to update
	 * 1 of the below 4 parameters are required
	 * @param limitMem String The limit memory or upper bound of memory that the pod can consume
	 * @param limitCpu String The limit cpu or upper bound of cpu that the pod can consume
	 * @param requestMem String The request memory or amount of memory that is reserved for the pod
	 * @param requestCpu String The request cpu or amount of cpu that is reserved for the pod
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      deploymentName:"deployment",
	 *      limitMem: "5Gi",
	 *      limitCpu: "16",
	 *      requestMem: "4Gi",
	 *      requestCpu: "10"
	 *  ]
	 */

	def updateResourcesDeployment(Map<String, Object> params){
		def defaults = [
			credentials: null,  // required
			cluster: null, // required
			namespace: null, //required
			deploymentName: null,  // required
			limitMem: null, //optional
			limitCpu: null, //optional
			requestsMem: null, //optional
			requestsCpu: null, //optional
      clusterPort: 16443 //optional
		]
		def config = defaults + params

		jenkins.echo "Updating Kubernetes deployment $config.deploymentName"
		def limits = ''
		if(config.limitMem!=null){
			limits+="--limits=memory=$config.limitMem"
		}
		if(config.limitCpu!=null){
			if(limits.equals(''))
				limits+="--limits=cpu=$config.limitCpu"
			else
				limits+=",cpu=$config.limitCpu"
		}
		def requests=''
		if(config.requestsMem!=null){
			requests+="--requests=memory=$config.requestsMem"
		}
		if(config.requestsCpu!=null){
			if(requests.equals('')){
				requests+="--requests=cpu=$config.requestsCpu"
			}
			else
				requests+=",cpu=$config.requestsCpu"
		}
		jenkins.withCredentials([[$class: 'StringBinding', credentialsId: params.credentials, variable: 'TOKEN']]){
			def deleteCommand = """
				kubectl set resources deployment/$config.deploymentName $limits $requests --server=https://$config.cluster:$config.clusterPort --token=$jenkins.env.TOKEN --insecure-skip-tls-verify=true -n $config.namespace
        """
			jenkins.command("${deleteCommand}".toString(), false, '#!/bin/bash +x')
			
		}

	}

	/**
	 * Takes deployment name and one or more of:
	 * 		-resource limit and/or request variables
	 * 		-environment variables
	 * 		-container and image names
	 *  and updates the corresponding deployment with the given resources
	 * @param credentials String ID in jenkins for kubernetes token
	 *      Example:  "k8s_token"
	 * @param cluster String The cluster you wish to deploy too
	 * 		Example: k8s-prod-ctc-aci.optum.com
	 * @param namespace String The namesapce you wish to deploy to
	 * @param deploymentName String The name of the Kubernetes deployment to update
	 * 1 of the below 6 parameters are required
	 * @param containerImages Map of container names and image names to update in the deployment
	 * @param envVars Map of environment variable keys and values to update in the deployment
	 * @param limitMem String The limit memory or upper bound of memory that the pod can consume
	 * @param limitCpu String The limit cpu or upper bound of cpu that the pod can consume
	 * @param requestMem String The request memory or amount of memory that is reserved for the pod
	 * @param requestCpu String The request cpu or amount of cpu that is reserved for the pod
   * @param clusterPort String The port at which the cluster is available
	 *  def example = [
	 * 		credentials:"k8s_token",
	 * 		cluster:"k8s-prod-ctc-aci.optum.com",
	 * 		namespace:"mynamespace',
	 *      deploymentName:"deployment",
	 *      containerImages:["nginx":"nginx1.10", "busybox":"busybox2.3.1"],
	 *      envVars:["home":"/home/user","KUBECTL_VERSION":"1.10.0"],
	 *      limitMem: "5Gi",
	 *      limitCpu: "16",
	 *      requestMem: "4Gi",
	 *      requestCpu: "10"
	 *  ]
	 */

	def update(Map<String, Object> params){
		if(params.containsKey('envVars'))
			updateEnvVarsDeployment(params)
		if(params.containsKey('containerImages'))
			updateImageDeployment(params)
		if(params.containsKey('limitMem') || params.containsKey('limitCpu')|| params.containsKey('requestsMem')|| params.containsKey('requestsCpu'))
			updateResourcesDeployment(params)

	}

}
