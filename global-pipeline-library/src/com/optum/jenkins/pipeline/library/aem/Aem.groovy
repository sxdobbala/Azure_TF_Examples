#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.aem

class Aem implements Serializable {
  def jenkins

  Aem() throws Exception {
    throw new Exception('"this" must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Aem(jenkins) {
    this.jenkins = jenkins
  }

  def uninstallPackage(Map<String, Object> params) {
    def defaults = [
        credentialsId : null, //required
        artifactId : null, //required
        artifactGroup : null, //required
        envUrl : null, //required
        deleteArtifact : true, //required
        retries : 5, //optional
        retryDelay : 5 //optional
    ]

    def config = defaults + params
    jenkins.echo "Uninstalling package - arguments: $config"

    if(!config.credentialsId){
      jenkins.error 'Credentials ID is required'
    }
    if(!config.artifactId) {
      jenkins.error 'Artifact name is required'
    }
    if(!config.artifactGroup) {
      jenkins.error 'Artifact group is required'
    }
    if(!config.envUrl) {
      jenkins.error 'Environment url is required'
    }

    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId, usernameVariable: 'USER', passwordVariable: 'PASS']]) {
      def allPackages = []
      jenkins.glRetry times: config.retries, delay: config.retryDelay, {
        //Get all packages
        jenkins.echo 'Fetching all packages...'
        jenkins.command("curl -s -u '$jenkins.env.USER:$jenkins.env.PASS' $config.envUrl/crx/packmgr/list.jsp > all-packages.json")

        //Parse all packages
        def json = jenkins.readJSON file: 'all-packages.json'
        allPackages = json['results']
      }
      
      //Iterate all packages looking for matching artifact/group
      allPackages.each { artifact ->
        def name = artifact['name']
        def group = artifact['group']
        def downloadName = artifact['downloadName']

        //Uninstall artifact
        if(config.artifactId == name && config.artifactGroup == group){
          jenkins.glRetry times: config.retries, delay: config.retryDelay, {
            def response = jenkins.command("curl -u '$jenkins.env.USER:$jenkins.env.PASS' -X POST '$config.envUrl/crx/packmgr/service/.json/etc/packages/$group/$downloadName?cmd=uninstall'", true)
            jenkins.echo response
            response = jenkins.readJSON text: response
            if(!response['success']){
              jenkins.error 'The package could not be found or failed to uninstall'
            }
          }

          //Conditionally delete artifact
          if(config.deleteArtifact){
            jenkins.glRetry times: config.retries, delay: config.retryDelay, {
              def response = jenkins.command("curl -u '$jenkins.env.USER:$jenkins.env.PASS' -X POST '$config.envUrl/crx/packmgr/service/.json/etc/packages/$group/$downloadName?cmd=delete'", true)
              jenkins.echo response
              response = jenkins.readJSON text: response
              if(!response['success']){
                jenkins.error 'The package could not be found or failed to delete'
              }
            }
          }
        }
      }
    }
  }

  def installPackage(Map<String, Object> params) {
    def defaults = [
      credentialsId : null, // required
      artifactId : null, // required
      artifactVersion : null, //required
      artifactBuildPath : 'target', //relative path to build output directory
      envUrl : null, // required
      retries : 5, //optional
      retryDelay : 5 //optional
    ]

    def config = defaults + params
    jenkins.echo "Installing Package - arguments: $config"

    if(!config.credentialsId){
      jenkins.error 'Credentials ID is required'
    }
    if(!config.artifactId) {
      jenkins.error 'Artifact name is required'
    }
    if(!config.artifactVersion) {
      jenkins.error 'Artifact version is required'
    }
    if(!config.envUrl) {
      jenkins.error 'Environment url is required'
    }

    jenkins.glRetry times: config.retries, delay: config.retryDelay, {
      jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId, usernameVariable: 'USER', passwordVariable: 'PASS']]) {
        def response = jenkins.command("curl -u '$jenkins.env.USER:$jenkins.env.PASS' -F file=@${config.artifactBuildPath}/${config.artifactId}-${config.artifactVersion}.zip -F name=${config.artifactId}-${config.artifactVersion} -F force=true -F install=true ${config.envUrl}/crx/packmgr/service.jsp", true)
        jenkins.echo response
        if(!(response =~ 'code="200"')){
          jenkins.error 'The package installation was unsuccessful'
        }
      }
    }
  }
}
