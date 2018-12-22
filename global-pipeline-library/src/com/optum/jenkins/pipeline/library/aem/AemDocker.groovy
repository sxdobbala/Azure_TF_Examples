#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.aem

import com.optum.jenkins.pipeline.library.utils.Utils

class AemDocker implements Serializable {
  def jenkins

  AemDocker() throws Exception {
    throw new Exception('"this" must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  AemDocker(jenkins) {
    this.jenkins = jenkins
  }

  //This method will update the deployment configuration to deploy using the provided DTR image
  def deploy(Map<String, Object> params) {
    def defaults = [
        oseCredential : null, // required
        ocpUrl: null,  //required e.g. https://ocp-ctc-core-nonprod.optum.com
        project: null, // required
        dcName: null, //required
        containerName: null, //required name of container inside DC e.g. 'author' or 'publish'
        dockerImage: null,  // required
        waitForCompletion: false
    ]

    def config = defaults + params
    jenkins.echo "Deploying New Image - arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['oseCredential', 'ocpUrl', 'project', 'dcName', 'containerName', 'dockerImage'], config)

    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.oseCredential, usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {

      //Login and retrieve specified deployment config
      def oseScript = """
        oc login ${config.ocpUrl} -u ${jenkins.env.OC_USER} -p ${jenkins.env.OC_PASS} --insecure-skip-tls-verify=true
        oc project ${config.project}
        oc get dc ${config.dcName} -o json > ${config.dcName}.json
      """
      jenkins.command(oseScript)

      //Get the currently running pod name used for waitForCompletion functionality
      def currentPodName = ''
      if(config.waitForCompletion){
        jenkins.command("oc get pods --show-all=false -l deploymentconfig=${config.dcName} -o json > current-pods.json")
        def currentPodsJson = jenkins.readJSON file: 'current-pods.json'
        currentPodsJson['items'].each{ pod ->
          if(pod['metadata']['labels']['deploymentconfig'] == config.dcName){
            currentPodName = pod['metadata']['name']
          }
        }
      }

      //Update deployment config with built docker image
      def dc = jenkins.readJSON file: "${config.dcName}.json"
      dc['spec']['template']['spec']['containers'].each{ container ->
        if(container['name'] == config.containerName){
          container['image'] = config.dockerImage.toString()
        }
      }
      jenkins.writeJSON file: "${config.dcName}.json", json: dc

      //Replace the deployment config
      jenkins.command("oc replace -f ${config.dcName}.json")

      //Optional wait for new pod to be created and ready
      if(config.waitForCompletion){
        jenkins.glRetry times: 20, delay: 30, {
          jenkins.command("oc get pods --show-all=false -l deploymentconfig=${config.dcName} -o json > active-pods.json")
          def activePodsJson = jenkins.readJSON file: 'active-pods.json'
          def activePodName = ''
          def activePodReady = false
          activePodsJson['items'].each{ pod ->
            if(pod['metadata']['labels']['deploymentconfig'] == config.dcName){
              activePodName = pod['metadata']['name']
              activePodReady = pod['status']['containerStatuses'][0]['ready']
            }
          }

          if(activePodName == currentPodName){
            jenkins.error 'The deployment has not started yet'
          }

          if(!activePodReady){
            jenkins.error 'Startup in progress...'
          }
        }
      }

      //Logout
      jenkins.command('oc logout')
    }
  }
}
