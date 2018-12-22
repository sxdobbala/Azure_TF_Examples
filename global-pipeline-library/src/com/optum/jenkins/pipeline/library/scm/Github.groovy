#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.scm

import com.optum.jenkins.pipeline.library.utils.CredentialValidator

class Github implements Serializable {
  def jenkins

  Github() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Github(jenkins) {
    this.jenkins = jenkins
  }

  /**
  * Get the organization name
  */
  def getOrg(){
    if(jenkins.env.CHANGE_URL != null){
      String path = new URL(jenkins.env.CHANGE_URL).getPath()
      String[] splitPath = path.split("/")
      return splitPath[1]
    }
  }

  /**
  * Get the repository name
  */
  def getRepo(){
    if(jenkins.env.CHANGE_URL != null){
      String path = new URL(jenkins.env.CHANGE_URL).getPath()
      String[] splitPath = path.split("/")
      return splitPath[2]
    }
  }


  /**
  * Send or update a Git status on the current revision.
  *
  * Usage: use on the same node/stage as where git checkout occurred. Have the option to pass
  *  one of four statuses to Github. These status are then able to serve as required checks for code merges to branches.
  *
  * @param gitHubUrl The base url for accessing Github APIs (default 'https://github.optum.com/api/v3').
  * @param credentialsId Required jenkins credential reference id of SecretText type, for github. Containing github api token with write permission to repository.
  * @param context The title of the status to display on Github.
  * @param description The description associated with the status.
  * @param status The status display on Github. (success, pending, failure, error)
  * @param org The organization the repository belongs to on Github. (optional)
  * @param repo The repository the status will belong to. (optional)
  * @param sha The commit sha the status will belong to. (optional)
  * @param target_url The target url for the status to be associated with. (optional, default: jenkins.env.BUILD_URL)
  *
  */
  def sendGitStatus(Map<String, Object> params){
    def defaults = [
        gitHubUrl : 'https://github.optum.com/api/v3', //Optional
        credentialsId : null, //Required
        context : 'default', //optional
        description : 'default', //Optional
        status : 'pending', //Required, viable values: success, pending, failure, error,
        org: getOrg(), //Optional
        repo: getRepo(), //Optional
        sha: "${jenkins.env.GIT_COMMIT}", //Optional
        target_url: "${jenkins.env.BUILD_URL}" //Optional
    ]
    def config = defaults + params
    jenkins.echo "sendGitStatus arguments: $config"
    //Workaround to avoid passing an enum as a variable which requires additional script approvals.
    try{
      config.status = config.status.toLowerCase()
      GithubStatus.validateStatus(config.status)
    }catch(Exception e){
      jenkins.error('Invalid Git status passed in.  Please use success, pending, failure, or error only.')
    }

    if (config.credentialsId) {
      CredentialValidator.validate(jenkins, config.credentialsId, 'SecretText')
    }
    else{
      jenkins.error 'credentialsId is required, please provide a jenkins credential reference id of SecretText type containing github oAuth token with proper write permission to update github status'
    }

    String url = "${config.gitHubUrl}/repos/${config.org}/${config.repo}/statuses/${config.sha}".trim()
    jenkins.withCredentials([jenkins.string(credentialsId: config.credentialsId, variable: 'GITHUB_TOKEN')]) {
      jenkins.sh  """
              curl --silent POST "$url" --header 'Authorization: token ${jenkins.env.GITHUB_TOKEN}' \
              -d '{
              "state": "$config.status",
              "description": "$config.description",
              "context": "$config.context",
              "target_url": "$config.target_url"
              }'  > /dev/null
          """
    }
  }
}
