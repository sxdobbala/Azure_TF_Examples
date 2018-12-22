package com.optum.jenkins.pipeline.library.kubernetes

import spock.lang.Specification

class KubernetesDeploySpec extends Specification {

	def "jenkins context is available"(){
		given: "Default jenkins context"
		def jenkins = [echo : 'hello']
		when: 'Creating class with jenkins context'
		def kubernetes = new Kubernetes(jenkins)
		then: "Jenkins context is available"
		kubernetes.getJenkins() == jenkins
	}

	def "error for missing jenkins context"(){
		when: 'Creating class without jenkins context'
		def kubernetes = new Kubernetes()
		then: "Exception is thrown"
		def e = thrown(Exception)
		e.message.contains('`this` must be passed when creating new class')
	}

	def "kubernetes deployment with no yaml provided"(){
		given: "no templateFile"
		def jenkins = [
				env     : [],
		]
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentName: "deployment", //required
			serviceName: "service", //required
			deploymentYaml: "deployment.yaml", //
			serviceYaml: "service.yaml" //
		]
		when: "deploymentKubernetes() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deploymentKubernetes(params)
		then:
		thrown(java.lang.Exception)
	}

	def "kubernetes deployment yaml passed to kubectl"(){
		given: "yaml given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def template = 'file.yml'
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentYaml = "deployment.yaml"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentYaml: deploymentYaml, //
		]
		when: "deploymentKubernetes() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deploymentKubernetes(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl create -f ' + deploymentYaml)
	}
	
	
	def "kubernetes service yaml passed to kubectl"(){
		given: "yaml given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def serviceYaml = "service.yaml"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			serviceYaml: "service.yaml" //
		]
		when: "serviceKubernetes() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.serviceKubernetes(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl create -f ' + serviceYaml)
	}
	
	def "kubernetes deployment yaml passed to kubectl delete"(){
		given: "yaml given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentYaml = "deployment.yaml"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentYaml: deploymentYaml, //
		]
		when: "deleteDeploymentFromYaml() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deleteDeploymentFromYaml(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl delete -f ' + deploymentYaml)
	}
	def "kubernetes service yaml passed to kubectl delete"(){
		given: "yaml given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def serviceYaml = "service.yaml"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			serviceYaml: serviceYaml, //
		]
		when: "deleteServiceFromYaml() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deleteServiceFromYaml(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl delete -f ' + serviceYaml)
	}
	
	def "kubernetes deployment name passed to kubectl delete"(){
		given: "name given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentName: deploymentName, //
		]
		when: "deleteDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deleteDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl delete deploy ' + deploymentName)
	}
	
	def "kubernetes service name passed to kubectl delete"(){
		given: "name given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def serviceName = "service"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			serviceName: serviceName, //
		]
		when: "deleteService() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.deleteService(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl delete svc ' + serviceName)
	}
	
	def "kubernetes env variables passed to kubectl update"(){
		given: "env variables given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def envVars = ["key":"value"]
		def envVarsFormatted = "key=value"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			envVars: envVars, //required
			deploymentName: deploymentName //required
		]
		when: "updateEnvVarsDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.updateEnvVarsDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl set env deployment/'+deploymentName+'  ' + envVarsFormatted)
	}
	
	def "kubernetes containerImage passed to kubectl update"(){
		given: "image given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def containerImage = ['key':'value', 'key2':'value2']
		def containerImageFormatted = "key=value key2=value2"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			containerImages: containerImage, //required
			deploymentName: deploymentName //required
		]
		when: "updateImageDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.updateImageDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl set image deployment/'+deploymentName+'  ' + containerImageFormatted)
	}
	
	def "kubernetes resources passed to kubectl update"(){
		given: "resource variables given and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def limitMem = "5G"
		def limitCpu = "16"
		def requestsMem = "4G"
		def requestsCpu = "15"
		def formattedResources = "--limits=memory=5G,cpu=16 --requests=memory=4G,cpu=15"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			limitMem: limitMem, //required
			limitCpu: limitCpu, //required
			requestsMem: requestsMem, //required
			requestsCpu: requestsCpu, //required
			deploymentName: deploymentName //required
		]
		when: "updateResourcesDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.updateResourcesDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl set resources deployment/'+deploymentName+' ' + formattedResources)
	}
	
	def "kubernetes replicas passed to kubectl scale"(){
		given: "replicas and deployment name provided and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def replicas = "3"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentName: deploymentName, //required
			replicas: replicas //required
		]
		when: "scaleDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.scaleDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl scale --replicas '+replicas + ' '+ 'deployment/'+deploymentName)
	}
	
	def "kubernetes port and deployment name passed to kubectl expose"(){
		given: "replicas and deployment name provided and blank return of the jenkins.sh"
		def calledJenkinsSh
		def jenkins = [
				env     : [],
				echo    : { String s -> println(s) },
				withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
				command      : { String a, Boolean b, String c -> calledJenkinsSh += a }
		]
		def deploymentName = "deployment"
		def port = "8080"
		def params = [
				
			credentials: "credentials",  // required
			cluster: "k8s-prod-ctc-aci.optum.com", //required
			namespace: "mynamespace", //required
			deploymentName: deploymentName, //required
			port: port //required
		]
		when: "exposeDeployment() is called"
		def kubernetes = new Kubernetes(jenkins)
		kubernetes.exposeDeployment(params)
		then:
		noExceptionThrown()
		calledJenkinsSh.contains('kubectl expose deployment ' + deploymentName+ ' --port ' + port)
	}


	
}