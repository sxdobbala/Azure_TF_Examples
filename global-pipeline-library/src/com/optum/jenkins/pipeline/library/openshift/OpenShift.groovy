#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.openshift

import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.DeployEvent
import com.optum.jenkins.pipeline.library.utils.Utils
import com.optum.jenkins.pipeline.library.utils.Constants
import java.lang.instrument.IllegalClassFormatException

// TODO: Monitor updates to the oc login options. In the event it supports
//       accepting the password via stdin (security hardening measure), that
//       approach should be favored over passwords directly on the oc login
//       command line
class OpenShift implements Serializable {
  def jenkins
  def startTime

  OpenShift() throws IllegalClassFormatException {
    throw new IllegalClassFormatException('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  OpenShift(jenkins) {
    this.jenkins = jenkins
    startTime = new Date()
  }

  /**
   * Takes an image from the Docker Trusted Registy and deploys it to an OpenShift application.
   *
   * @param dockerImage String The name of the docker image that you are wanting to build.
   *      Example:  "docker.optum.com/devops_engineering/anthillagent:${BUILD_NUMBER}"
   * @param ocpUrl String The server url for the OSEv3 instance.
   *      Example: "https://ose-ctc-core.optum.com"
   * @param project String The OSEv3 project name.
   * @param port String The port to EXPOSE
   * @param credentials String Required Credentials to push to OSEv3
   * @param serviceName String Specify name of service if different than Docker image
   * @param tag String Specify name of image stream tag
   * @param wait Boolean Waits for deployment to complete if it is available for that platform.
   * @param times int Overrides how many times to poll OpenShift on whether the deployment is complete.
   * @param delay int Overrides how long it takes between poll attempts to OpenShift on whether the deployment is complete.
   * @param annotations Map key/value to inject metadata annotations information for the openshift object.
   *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
   *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
   *
   *  def example = [
   * 		dockerImage:"docker.optum.com/devops_engineering/anthillagent",
   * 		ocpUrl:"https://ose-ctc-core.optum.com",
   * 		project:"ocdtest',
   * 		credentials:"devopseng_tech",
   *     serviceName:"anthill",
   *     annotations: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
   *  ]
   */

  def deploy(Map<String, Object> params) {
    def defaults = [
      credentials: null,  // required
      dockerImage: null,  // required
      project: null,      // required
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',  //optional
      port: '8080',  //optional
      serviceName: null,  //required
      tag: 'latest',  //optional
      wait: false, //optional
      times: 10, //optional
      delay: 10,  //optional
      annotations: [:] //optional
    ]
    def config = defaults + params

    if (config.serviceName == null) {
      config.serviceName = config.dockerImage.split('/')[2].split(':')[0]
    }
    if (config.serviceName.length() > 24) {
      config.serviceName = config.serviceName[0..23]
    }
    config.serviceName = config.serviceName.replace('_','-').toLowerCase()

    if (params.tag == null) {
      config.tag = config.dockerImage.split(':').size() > 1 ? config.dockerImage.split(':')[1] : config.tag
    }
    config.dockerImage = config.dockerImage.split(':')[0] + ":" + config.tag

    jenkins.echo "Service name: $config.serviceName"
    jenkins.echo "Openshift deploy arguments: $config"
    if (config.ocpUrl.contains("ose")) {
      deployOSE(config)
    } else {
      deployOCP(config)
    }

    if (config.annotations) {
      applyAnnotations(config)
    }
  }

  /**
   * Takes an image in DTR runs it on OpenShift 3.2
   *
   *
   */

  def deployOSE(Map<String, Object> params) {

    def defaults = [
      production: false, // required
      env: 'dev' // required
    ]
    def config = defaults + params

    def dcConfigName = "$params.serviceName"

    try {
      jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: params.credentials,
                                usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
        def oseScript = """
          oc login $params.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true
          oc project $params.project
          if [[ \$(oc get dc --output=name | grep '$dcConfigName'\$) == */\"$dcConfigName\" ]] ; then
              echo 'DEPLOYMENTCONFIG ALREADY EXISTS - USING IT'
              oc delete rc \$(oc get rc | grep $params.serviceName | awk '\$2 == 0 {print \$1}') || \
                  echo 'NO REPLICATION CONTROLLERS TO CLEAN UP'
              oc deploy $params.serviceName --latest
          else
              echo 'DEPLOYMENTCONFIG DOES NOT EXIST - CREATING'
              oc new-app --docker-image=$params.dockerImage \
                  --name=$params.serviceName --allow-missing-images
              oc expose svc $params.serviceName \
                  || oc expose dc/$params.serviceName --port=$params.port
          fi
          oc logout
        """
        jenkins.command(oseScript)
      }
    } catch (Exception ex) {
      sendDeployEvent(jenkins, startTime, EventStatus.FAILURE, 'jenkins', config.env, config.production, 'OSE')
      jenkins.error(' Deploy OSE failed: ' + ex.getStackTrace())
    }
    sendDeployEvent(jenkins, startTime, EventStatus.SUCCESS, 'jenkins', config.env, config.production, 'OSE')
  }

  /**
   * Takes an image in DTR runs it on OpenShift 3.6
   *
   *
   */

  def deployOCP(Map<String, Object> params) {

    def defaults = [
      production: false, // required
      env: 'dev' //required
    ]
    def config = defaults + params

    def dcConfigName = "$params.serviceName"
    boolean deploy = true
    try {
      jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: params.credentials,
                                usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
        def oseScript = """
        oc login $params.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true
        oc project $params.project
        if [ $params.wait = true ]
        then
            echo \"\$(oc describe dc/$params.serviceName | grep 'Image:' | awk '{print \$2}')\" > prev_deploy_image
            echo \"\$(oc rollout history dc/$params.serviceName | grep Complete | awk 'BEGIN{a= 0}{if(\$1>0+a) a=\$1} END{print a}')\" > prev_deploy_number
        fi
        if [[ \$(oc get dc --output=name | grep '/$dcConfigName'\$ | cut -c 2-) == */\"$dcConfigName\" ]] ; then
            echo 'DEPLOYMENTCONFIG ALREADY EXISTS - USING IT'
            oc delete rc \$(oc get rc | grep $params.serviceName | awk '\$2 == 0 {print \$1}') || \
                echo 'NO REPLICATION CONTROLLERS TO CLEAN UP'
            oc import-image $params.serviceName:$params.tag
        else
            echo 'DEPLOYMENTCONFIG DOES NOT EXIST - CREATING'
            oc new-app --docker-image=$params.dockerImage \
                --name=$params.serviceName --allow-missing-images
            oc expose svc $params.serviceName \
                || oc expose dc/$params.serviceName --port=$params.port
        fi
        if [ $params.wait = true ]
        then
            echo \"\$(oc describe is/$params.serviceName | grep '*' | awk '{print \$2}')\" > new_deploy_image
        fi
        oc logout
      """
        jenkins.command(oseScript)
      }
      if(params.wait){
        def prev_deploy_image = jenkins.readFile('prev_deploy_image').trim()
        def new_deploy_image = jenkins.readFile('new_deploy_image').trim()
        if(new_deploy_image != prev_deploy_image) {
          def PREV_DEPLOY_NUMBER = jenkins.readFile('prev_deploy_number').trim() as Integer
          waitForDeployment(PREV_DEPLOY_NUMBER + 1, params.credentials, params.ocpUrl, params.project, params.serviceName, params.times, params.delay)
        } else {
          jenkins.echo 'Image not changed: No deployment required'
          deploy = false
        }
      }
    } catch (Exception ex) {
      sendDeployEvent(jenkins, startTime, EventStatus.FAILURE, 'jenkins', config.env, config.production, 'OCP')
      jenkins.error(' Deploy OCP failed: ' + ex.getStackTrace())
    }
    if (deploy) {
      sendDeployEvent(jenkins, startTime, EventStatus.SUCCESS, 'jenkins', config.env, config.production, 'OCP')
    }
  }

  /**
   * Used for Image Promotion
   *
   * As the image is tagged, it will trigger a deployment with an image change trigger in the deployment config.
   *
   * @param tag HashMap of the tag instructions.
   *  Example:  ['prod':'rollback', 'dev':'prod']
   * @param ocpUrl String The server url for the OSEv3 instance.
   *  Example: "https://ose-ctc-core.optum.com"
   * @param project String The OSEv3 project name.
   * @param destinationProject String Project to promote the image to, if any.
   * @param credentials String Required Credentials to push to OSEv3
   * @param serviceName String Specify name of service if different than Docker image
   * @param annotations Map key/value to inject metadata annotations information for the openshift object
   *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
   *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
   *
   *  def example = [
   * 		tag:['prod':'rollback', 'dev':'prod'],
   * 		ocpUrl:"https://ose-ctc-core.optum.com",
   * 		project:"ocdtest',
   * 		destinationProject:"ocdprod",
   * 		credentials:"devopseng_tech",
   *         serviceName:"anthill",
   *    annotations: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
   *  ]
   *
   */

  def tag(Map<String, Object> params) {
    def defaults = [
      tag: null, //required
      credentials: null,  // required
      project: null,      // required
      destinationProject: null,  //optional, used to promote to another project
      ocpUrl: "https://ocp-ctc-core-nonprod.optum.com", //optional
      serviceName: '',  //required
      annotations: [:], //optional
    ]
    def config = defaults + params

    if (config.serviceName.length() > 24) {
      config.serviceName = config.serviceName[0..23]
    }
    config.serviceName = config.serviceName.replace('_','-').toLowerCase()

    if(config.destinationProject != null){
      config.destinationProject = config.destinationProject + "/"
    } else {
      config.destinationProject = ''
    }

    jenkins.echo "Openshift tag arguments: $config"
    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.credentials,
                              usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
      def oseScript = """
      oc login $config.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true
      oc project $config.project
    """
      for ( e in config.tag ) {
        oseScript = oseScript + "oc tag $config.serviceName:${e.key} $config.destinationProject$config.serviceName:${e.value}\n"
      }
      oseScript = oseScript + "oc logout"

      jenkins.command(oseScript)
    }
    if (config.annotations) {
      applyAnnotations(config)
    }
  }

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


  def buildAndRun(Map<String, Object> params) {
    def defaults = [
      credentials: null,  // required
      project    : null,      // required
      ocpUrl     : "https://ocp-ctc-core-nonprod.optum.com", //optional
      path       : '.', //optional
      serviceName: null, //required
      port: 8080, //optional
      wait: false, //optional
      times: 10, //optional
      delay: 10,  //optional
      annotations: [:], //optional
      production: false, // required
      env: 'dev', // optional
      platform: 'OCP'  // optional
    ]
    def config = defaults + params

    if (config.serviceName.length() > 24) {
      config.serviceName = config.serviceName[0..23]
    }

    jenkins.echo "Openshift build and run arguments: $config"

    def bcConfigName = "$config.serviceName"

    def localPath = "$config.path"

    try {
      jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.credentials,
                                usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
        def oseScript = """
          oc login $config.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true
          oc project $config.project
          if [ $config.wait = true ]
          then
              echo \"\$(oc rollout history dc/$config.serviceName | grep Complete | awk 'BEGIN{a= 0}{if(\$1>0+a) a=\$1} END{print a}')\" > prev_deploy_number
          fi
          if [[ \$(oc get bc --output=name | grep '$bcConfigName'\$) == */\"$bcConfigName\" ]] ; then
              echo 'BUILDCONFIG ALREADY EXISTS - USING IT'
              oc delete rc \$(oc get rc | grep $config.serviceName | awk '\$2 == 0 {print \$1}') || \
                  echo 'NO REPLICATION CONTROLLERS TO CLEAN UP'
              oc start-build $config.serviceName --from-dir=$localPath --follow=true
          else
              echo 'BUILDCONFIG DOES NOT EXIST - CREATING'
              oc new-build --binary=true --name=$config.serviceName -l app=$config.serviceName
              oc start-build $config.serviceName --from-dir=$localPath --follow=true
              oc new-app -i $config.serviceName -l app=$config.serviceName
              oc expose svc $config.serviceName \
                  || oc expose dc/$config.serviceName --port=$config.port
          fi
          oc logout
        """
        jenkins.command(oseScript)
      }
      if(config.wait){
        def PREV_DEPLOY_NUMBER = jenkins.readFile('prev_deploy_number').trim() as Integer
        waitForDeployment(PREV_DEPLOY_NUMBER + 1, config.credentials, config.ocpUrl, config.project, config.serviceName, config.times, config.delay)
      }
      if (config.annotations) {
        applyAnnotations(config)
      }
      sendDeployEvent(jenkins, startTime, EventStatus.SUCCESS, 'jenkins', config.env, config.production, config.platform)
    } catch (Exception ex) {
      sendDeployEvent(jenkins, startTime, EventStatus.FAILURE, 'jenkins', config.env, config.production, config.platform)
      jenkins.error(' Openshift buildAndRun failed: ' + ex.getStackTrace())
    }
  }

/**
 * Takes a OpenShift template from a Jenkins workspace and runs it on OpenShift.
 *
 * @param credentials String Required Credentials to push to OSEv3
 * @param project String The OSEv3 project name.
 * @param ocpUrl String The server url for the OSEv3 instance.
 *      Example: "https://ose-ctc-core.optum.com"
 * @param templateFile String The path to the yaml template within the Jenkins workspace.
 *      Example: "deploy-to-openshift.yaml"
 * @param templateParams Array of paramaters to pass to template file in key:value format
 *      Example: "["RUNTIME_ENVIRONMENT":"prod", "DOCKER_TAG":"${DOCKER_TAG}"]"
 *  @param templateParamsFile File containing template parameters, requires OC 3.5
 *        Example: "params.properties"
 * @param annotations Map key/value to inject metadata annotations information for the openshift object
 *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
 *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
 *
 */
  def processTemplate(Map<String, Object> params){

    def defaults = [
      credentials: null,  // required
      project: null,      // required
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',  //required
      templateFile: null,  //required
      templateParams: null,  //optional
      templateParamsFile: null, // optional, file containing template parameters, requires OC 3.5
      force: true, //optional
      apply: false,
      annotations: [:], //optional
      ocVersion: Constants.OC_VERSION,
      production: false,  // required
      env: 'dev',         // required
      platform: 'OSE'      // optional
    ]

    def config = defaults + params

    def ocParams = ""
    config?.templateParams?.each{ paramKey, paramValue ->
      ocParams += "-p ${paramKey}=\"${paramValue}\" "
    }

    def paramFileParameter = ""
    if(config.templateParamsFile != null){
      paramFileParameter = "--param-file=${config.templateParamsFile}"
    }

    if(config?.templateFile){
      try {
        jenkins.withEnv(["OC_VERSION=${config.ocVersion}"]) {
          jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: params.credentials,
                                    usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {

            jenkins.echo "checking openshift objects from ${config.templateFile}"
            def ocObjects = jenkins.command ("""
              oc login $params.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true > /dev/null
              oc project $params.project > /dev/null
              oc process -f ${config.templateFile} ${ocParams} ${paramFileParameter} | oc get --no-headers=true -f - | awk '{print \$1}' || echo ''
            """, true).trim()

            def ocActions

            if(config.force){
              ocActions = ocObjects ? "replace --force" : "create"
            } else if(config.apply){
              ocActions = "apply"
            } else {
              ocActions = ocObjects ? "replace" : "create"
            }

            jenkins.echo "${ocActions} openshift objects ${ocObjects} from ${config.templateFile}"

            jenkins.command ("""
              oc process -f ${config.templateFile} ${ocParams} ${paramFileParameter} | oc ${ocActions} -f -
            """, true).trim()
            jenkins.command ("""
              oc logout
            """, true).trim()
          }
        }
      } catch (Exception ex) {
        sendDeployEvent(jenkins, startTime, EventStatus.FAILURE, 'jenkins', config.env, config.production, config.platform)
        jenkins.error(' Process Template failed: ' + ex.getStackTrace())
      }
      sendDeployEvent(jenkins, startTime, EventStatus.SUCCESS, 'jenkins', config.env, config.production, config.platform)
    }
    else{
      throw new Exception("required parameter key templateFile is empty")
    }

    if (config.annotations) {
      applyAnnotations(config)
    }
  }

  /**
   * Waits for a particular deployment number in OpenShift.
   *
   *
   */
  private def waitForDeployment(int deploymentNumber, String credentials, String cluster, String project, String service, int times, int delay) {
    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentials, usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
      jenkins.echo('Waiting for deployment to complete...')

      jenkins.echo('Previous complete deployment: ' + (deploymentNumber - 1))
      def oseDeployedScript = """
        oc login $cluster -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true
        oc project $project
        echo \"\$(oc rollout history dc/$service | grep Complete | awk 'BEGIN{a= 0}{if(\$1>0+a) a=\$1} END{print a}')\" > deploy_number
      """
      def body = {
        jenkins.command(oseDeployedScript)
        def DEPLOY_NUMBER = jenkins.readFile('deploy_number').trim() as Integer
        jenkins.echo('Latest complete deployment: ' + DEPLOY_NUMBER)
        if(deploymentNumber > DEPLOY_NUMBER){
          throw new Exception("Deployment " + deploymentNumber + " not yet complete.")
        }
      }
      def retryParams = [times: times, delay: delay]
      Utils utils = new Utils(jenkins)
      utils.retry(retryParams, body)
    }
    def oseCleanupScript = """
      oc logout
      rm prev_deploy_number || true
      """
    jenkins.command(oseCleanupScript)
  }

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
  def deleteServiceResources(Map<String, Object> params){
    def defaults = [
      credentials   : null,  // required
      project       : null,      // required
      serviceName   : null,  // required
      ocpUrl        : 'https://ose-ctc-core.optum.com',
    ]
    def config = defaults + params

    if (config.serviceName.length() > 24) {
      config.serviceName = config.serviceName[0..23]
    }
    config.serviceName = config.serviceName.replace('_','-').toLowerCase()

    jenkins.echo "Delete all OSE resources with serviceName: $config.serviceName"

    jenkins.withCredentials([[
                               $class: 'UsernamePasswordMultiBinding',
                               credentialsId: config.credentials,
                               usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
      def deleteCommand = """\
        oc login $config.ocpUrl -u ${jenkins.env.OC_USER} -p '${jenkins.env.OC_PASS}' --insecure-skip-tls-verify=true
        oc project $config.project
        oc delete all -l app=$config.serviceName || true
        oc delete pvc -l app=$config.serviceName || true
        oc logout
        """
      jenkins.command(deleteCommand)
    }
  }

  /**
   * Takes an image from the Docker Trusted Registy and deploys it to an OpenShift application.
   *
   * @param dockerImage String The name of the docker image that you are wanting to build.  Example:  "docker.optum.com/devops_engineering/anthillagent:${BUILD_NUMBER}"
   * @param serverUrl String The server url for the OSEv3 instance.  Example: "https://ose-ctc-core.optum.com"
   * @param projectName String The OSEv3 project name.
   * @param port String The port to EXPOSE
   * @param credentials String Required Credentials to push to OSEv3
   *
   *  * Example:
   * 		dockerImage="docker.optum.com/devops_engineering/anthillagent"
   * 		serverUrl="https://ose-ctc-core.optum.com"
   * 		projectName="ocdtest'
   * 		port="7915"
   * 		credentials="devopseng_tech"
   *
   * */
  def deployImageOpenshiftFromDTR(String dockerImage, String serverUrl, String projectName, String port, String credentials){
    jenkins.echo('################ DEPRECATED AS OF v0.1.20 ################')

    jenkins.env.DOCKER_IMAGE = dockerImage
    jenkins.env.OCP_SERVER = serverUrl
    jenkins.env.OCP_PROJECT = projectName
    jenkins.env.OCP_APP = dockerImage.split('/')[2].split(':')[0]
    jenkins.env.PORT = port

    jenkins.echo 'Put Docker Trusted Registry image onto OpenShift v3, if the Deployment Config already exists for it, deploy that.'

    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentials,
                              usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {

      jenkins.sh '''. /etc/profile.d/jenkins.sh
      oc login $OCP_SERVER -u ${OC_USER} -p '${OC_PASS}' --insecure-skip-tls-verify=true
          oc project $OCP_PROJECT
      CHECK=$(oc get dc --output=name | grep $OCP_APP) || echo "NO DEPLOYMENT CONFIG"
      WHATITSHOULDBE="deploymentconfig/$OCP_APP"
      echo ${CHECK}
      echo ${WHATITSHOULDBE}
      if [ "${CHECK}" == "${WHATITSHOULDBE}" ] ; then
        echo "DEPLOYMENTCONFIG ALREADY EXISTS - USING IT"
        oc deploy $OCP_APP --latest
        oc delete rc $(oc get rc | grep $OCP_APP | awk '$2 == 0 {print $1}') || echo "NO REPLICATION CONTROLLERS TO CLEAN UP"
      else
        echo "DEPLOYMENTCONFIG DOES NOT EXIST - CREATING"
        oc new-app --docker-image=$DOCKER_IMAGE --allow-missing-images
        oc expose svc $OCP_APP || oc expose dc/$OCP_APP --port=$PORT
      fi
      oc logout
      '''
    }

  }

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
   * 		dockerImage="docker.optum.com/devops_engineering/anthillagent"
   * 		serverUrl="https://ose-ctc-core.optum.com"
   * 		projectName="ocdtest'
   * 		credentials="devopseng_tech"
   *
   * */
  def deleteAll(String dockerImage, String serverUrl, String projectName, String credentials){
    jenkins.echo('################ DEPRECATED AS OF v0.1.20 ################')

    jenkins.env.DOCKER_IMAGE = dockerImage
    jenkins.env.OCP_SERVER = serverUrl
    jenkins.env.OCP_PROJECT = projectName
    jenkins.env.OCP_APP = dockerImage.split('/')[2].split(':')[0]

    deleteServiceResources([
      credentials : credentials,
      project     : projectName,
      serviceName : jenkins.env.OCP_APP,
      ocpUrl      : OCP_SERVER,
    ])

  }


  /**
   * Annotates openshift objects metadata information
   *
   * @param ocpUrl String The server url for the OSEv3 instance.  Example: "https://ose-ctc-core.optum.com"
   * @param projectName String The openshift project name.
   * @param credentials String Required Credentials to login to openshift
   * @param serviceName String The name of the openshift object you want to annotate
   * @param templateFile String The path to the yaml template within the Jenkins workspace.
   *      Example: "deploy-to-openshift.yaml"
   * @param templateParams Array of paramaters to pass to template file in key:value format
   *      Example: "["RUNTIME_ENVIRONMENT":"prod", "DOCKER_TAG":"${DOCKER_TAG}"]"
   * @param annotations Map key/value to inject metadata annotations information for the openshift object
   *      Note: the annotations key/value provided will overwrite existing annotations in openshift.
   *      Example: ["jenkins-build-url":(env.BUILD_URL), "update-timestamp":(env.BUILD_TIMESTAMP)]
   *  @param templateParamsFile File containing template parameters, requires OC 3.5
   *        Example: "params.properties"
   *
   **/
  def applyAnnotations(Map<String, Object> params) {
    def defaults = [
      credentials: null,      // required
      project: null,          // required
      serviceName: null,      //optional
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',  //required
      templateFile: null,     //optional
      templateParams: null,   //optional
      templateParamsFile: null, // optional, file containing template parameters, requires OC 3.5
      annotations: [:],        //optional
      ocVersion: Constants.OC_VERSION
    ]
    def config = defaults + params

    def ocAnnotations = ""
    config?.annotations?.each{ annotationKey, annotationValue ->
      ocAnnotations += " ${annotationKey}=${annotationValue} "
    }

    if(ocAnnotations){
      jenkins.withEnv(["OC_VERSION=${config.ocVersion}"]) {
        jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: params.credentials,
                                  usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
          if(config?.templateFile){
            def ocParams = ""
            config?.templateParams?.each{ paramKey, paramValue ->
              ocParams += "-p ${paramKey}=\"${paramValue}\" "
            }

            def paramFileParameter = ""
            if(config.templateParamsFile != null){
              paramFileParameter = "--param-file=${config.templateParamsFile}"
            }

            jenkins.echo "annotating openshift objects from ${config.templateFile}"
            def output = jenkins.command ("""
                  oc login $params.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true > /dev/null
                  oc project $params.project > /dev/null
                  oc process -f ${config.templateFile} ${ocParams} ${paramFileParameter} | oc annotate --overwrite ${ocAnnotations} -f -
                """, true).trim()
            jenkins.echo output
          }

          if(config?.serviceName){
            jenkins.echo "annotating openshift objects ${config.serviceName}"
            def ocObjects = jenkins.command ("""
                  oc login $params.ocpUrl -u $jenkins.env.OC_USER -p '$jenkins.env.OC_PASS' --insecure-skip-tls-verify=true > /dev/null
                  oc project $params.project > /dev/null
                  oc get all --show-kind=true -o name | grep .*$config.serviceName\$ || echo ''
                """, true).trim()

            ocObjects?.split("\r?\n")?.each{ eachOCObjects ->
              def output = jenkins.command ("""
                    oc annotate $eachOCObjects --overwrite ${ocAnnotations}
                  """, true).trim()
              jenkins.echo output
            }
          }
        }
      }
    }
    else{
      jenkins.echo "no annotations key-value pair provided"
    }
  }
  def sendDeployEvent(jenkins, Date processStart, EventStatus status, String deployTool, String env, boolean production, String platform){
    new DeployEvent(jenkins, [duration: new Utils(jenkins).getDuration(processStart).toString(), status: status, deployTool: deployTool, env: env, production: production, platform: platform]).send()
  }
}
