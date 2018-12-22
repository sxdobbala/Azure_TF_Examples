#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.docker

import com.optum.jenkins.pipeline.library.utils.docker.DockerCapabilities
import com.optum.jenkins.pipeline.library.utils.Utils

class Docker implements Serializable {
  static final String DEFAULT_DOCKER_HOST='docker.optum.com'
  def jenkins

  Docker() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Docker(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Builds Docker Image, Tags and then will Push the Image to the Docker Trusted Registry which is docker.optum.com by default
 *
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 * @param baseDir String Defaults to the current directory.
 * @param extraBuildOptions String
 * @param dockerCredentialsId String Required Credentails to push to the Docker registry
 * @param dockerHost String Defaults to docker.optum.com
 * @param dockerVersion String Optional docker version (in the form xx.xx) used for docker CLI commands (e.g., pushing image to DTR)
 * @param extraPushOptions String Optional Push options.
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		dockerCredentialsId = "estarr_id"
 * 		tag = "docker.optum.com/estarr/anthillagent:${BUILD_NUMBER}" = "docker.optum.com/estarr/anthillagent:37"
 * Will result in the following docker commands
 * 	   	docker build --pull --tag docker.optum.com/estarr/anthillagent:37 .
 *     	docker push docker.optum.com/estarr/anthillagent:37
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the $namespace Docker Trusted Registry.
 *
 * */

  def buildPushDockerImage(Map<String, Object> params) {
    def defaults = [
      tag                : null, //required
      baseDir            : '.',  // optional
      dockerHost         : DEFAULT_DOCKER_HOST, //optional
      dockerVersion      : "${jenkins.env.DOCKER_VERSION}", // optional (drives if --password_stdin (preferred security best practice) can be used for docker login)
      extraBuildOptions  : '', //optional
      dockerCredentialsId: null, //required
      extraPushOptions   : '', //optional
      extraFlagOptions   : '' //optional
    ]
    def config = defaults + params

    jenkins.echo "buildAndDeployDockerImageToDTR arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId', 'tag'], config)

    buildDockerImage tag: config.tag,
      baseDir: config.baseDir,
      dockerCredentialsId: config.dockerCredentialsId,
      dockerVersion: config.dockerVersion,
      extraBuildOptions: config.extraBuildOptions,
      extraFlagOptions: config.extraFlagOptions
    pushDockerImage tag: config.tag,
      dockerHost: config.dockerHost,
      dockerCredentialsId: config.dockerCredentialsId,
      dockerVersion: config.dockerVersion,
      extraPushOptions: config.extraPushOptions,
      extraFlagOptions: config.extraFlagOptions
  }

/**
 * Builds Docker Image
 *
 * @param baseDir String Defaults to the current directory.
 * @param extraBuildOptions String
 * @param extraFlagOptions String Optional flag options.
 * @param requirePull Boolean Optional Pull the base image? False may be
 *          necessary when base image is private and is pulled separately
 * @param dockerVersion String Optional docker version (in the form xx.xx) used for docker CLI commands (e.g., pushing image to DTR)
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 *
 *  The following passed in parameters
 * 		tag = "docker.optum.com/estarr/anthillagent:${BUILD_NUMBER}" = "docker.optum.com/estarr/anthillagent:37"
 * Will result in the following docker commands
 * 	   	docker build --pull --tag estarr/anthillagent:30 .
 *
 *
 * */

  def buildDockerImage(Map<String, Object> params) {
    def defaults = [
      baseDir             : '.', // optional
      dockerCredentialsId : null, //optional
      dockerVersion       : "${jenkins.env.DOCKER_VERSION}", // optional (drives if --password_stdin (preferred security best practice) can be used for docker login)
      extraBuildOptions   : '',  // optional
      extraFlagOptions    : '',  // optional
      requirePull         : true, // optional
      tag                 : "${jenkins.env.BUILD_NUMBER}", // optional
    ]

    def config = defaults + params

    jenkins.echo "buildDockerImage arguments: $config"

    String pullOption = config.requirePull ? '--pull' : ''

    GString buildCmd = "docker $config.extraFlagOptions build $pullOption --tag \
      \"$config.tag\" $config.extraBuildOptions $config.baseDir "

    if(!config.dockerCredentialsId){
      jenkins.command(buildCmd)
    }
    else {
      withDockerLogin dockerCredentialsId: config.dockerCredentialsId, dockerVersion: config.dockerVersion, {
        jenkins.command(buildCmd)
      }
    }
  }

/**
 * Tags Docker Image
 *
 * @param repository String The name of the image that you are wanting to build.
 * @param sourceTag String The unique identifer for the source to tag
 * @param sourceTag String The unique identifer for the source to tag
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		sourceTag = "estarr/antillagent:30"
 * 		destTag = "docker.optum.com/estarr/anthillagent:30"
 * Will result in the following docker commands
 *     	docker tag estarr/anthillagent:30 docker.optum.com/estarr/anthillagent:30
 *
 * */

  def tagDockerImage(Map<String, Object> params) {
    def defaults = [
      sourceTag: null, //required
      destTag  : null, //required
      extraFlagOptions    : '' //optional
    ]
    def config = defaults + params

    jenkins.echo "tagDockerImage arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['sourceTag', 'destTag'], config)

    def tagCmd = "docker $config.extraFlagOptions tag \"$config.sourceTag\" \"$config.destTag\""
    jenkins.command(tagCmd)
  }

/**
 * Push the Docker image to the registry defined in tag parameter
 *
 * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
 * @param dockerCredentialsId String Required Credentails to push to the DTR
 * @param dockerHost String Defaults to docker.optum.com
 * @param dockerVersion String Optional docker version (in the form xx.xx) used for docker CLI commands (e.g., pushing image to DTR)
 * @param extraPushOptions String Optional Push options.
 * @param extraFlagOptions String Optional flag options.
 *
 *  The following passed in parameters
 * 		tag = "docker.optum.com/estarr/anthillagent:30"
 * Will result in the following docker commands
 *     	docker push docker.optum.com/estarr/anthillagent:30
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the target image repository.
 *
 * */

  def pushDockerImage(Map<String, Object> params) {
    def defaults = [
      tag                : null, //required
      dockerCredentialsId: null, //required
      dockerHost         : DEFAULT_DOCKER_HOST, //optional
      dockerVersion      : "${jenkins.env.DOCKER_VERSION}", // optional (drives if --password_stdin (preferred security best practice) can be used for docker login)
      extraPushOptions   : '', //optional
      extraFlagOptions   : '' //optional
    ]
    def config = defaults + params

    jenkins.echo "pushDockerImage arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId', 'tag'], config)

    withDockerLogin dockerCredentialsId: config.dockerCredentialsId, dockerVersion: config.dockerVersion, {
      jenkins.command("docker $config.extraFlagOptions push $config.extraPushOptions \"$config.tag\" \n")
    }
  }

 /**
  * Pull the Docker image to the registry defined in tag parameter
  *
  * @param tag String The unique identifer for this instance of the image.  Defaults to the Jenkins $BUILD_NUMBER.
  * @param dockerCredentialsId String Required Credentails to push to the DTR
  * @param dockerHost String Defaults to docker.optum.com
  * @param dockerVersion String Optional docker version (in the form xx.xx) used for docker CLI commands (e.g., pulling image from DTR)
  * @param extraOptions String Optional options.
  * @param extraFlagOptions String Optional flag options.
  *
  *  The following passed in parameters
  * 		tag = "docker.optum.com/estarr/anthillagent:30"
  * Will result in the following docker commands
  *       docker login ...
  *     	docker pull docker.optum.com/estarr/anthillagent:30
  *       docker logout
  *
  * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
  * This service account must have permission to pull from the target image repository.
  *
  * */
  def pullDockerImage(Map<String, Object> params) {
    def defaults = [
      tag                 : null, //required
      dockerCredentialsId : null, //required
      dockerHost          : DEFAULT_DOCKER_HOST, //optional
      dockerVersion       : "${jenkins.env.DOCKER_VERSION}", // optional (drives if --password_stdin (preferred security best practice) can be used for docker login)
      extraOptions        : '', //optional
      extraFlagOptions    : '' //optional
    ]
    def config = defaults + params

    jenkins.echo "pullDockerImage arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId', 'tag'], config)


    withDockerLogin dockerCredentialsId: config.dockerCredentialsId, dockerVersion: config.dockerVersion, {
      jenkins.command("docker $config.extraFlagOptions pull $config.extraOptions \"$config.tag\"")
    }
  }

/**
 * Create the Repository in the Docker Trusted Registry which is docker.optum.com by default
 *
 * @param dockerCredentialsId String Required Credentails to push to the Docker registry
 * @param dockerHost String Host name for the Docker registry - defaults to docker.optum.com
 * @param enableManifestLists boolean Feature to support multiple architectures (Windows/Linux) under the same tag - defaults to true
 * @param immutableTags boolean Whether tags can be overwritten - defaults to true
 * @param namespace String Required name of the image namespace (either user or organization)
 * @param repository String The name of the image that you are wanting to push to the repository.
 * @param scanOnPush boolean Whether security scans should be run on every push - defaults to false
 * @param visibility String If this is a public or private repository - defaults to public
 *
 *  The following passed in parameters
 * 		repository = "anthillagent"
 * 		namespace = "estarr"
 * 		dockerCredentialsId = "estarr_id"
 * Will result in a curl command that will create a repository
 *
 * The 'dockerCredentialsId' Credential is the service account Credential in Jenkins.
 * This service account must have permission to deploy to the $namespace Docker Trusted Registry.
 *
 * */

  def createDockerRepository(Map<String, Object> params) {
    def defaults = [
      dockerHost         : DEFAULT_DOCKER_HOST,
      dockerCredentialsId: null, //required
      enableManifestLists: true,
      immutableTags      : false,
      longDescription    : 'This was generated from the Jenkins Pipeline as Code global library',
      namespace          : null, //required
      repository         : null, //required
      scanOnPush         : false,
      shortDescription   : 'JPaC generated repo',
      visibility         : 'public'
    ]
    def config = defaults + params

    jenkins.echo "createDockerRepository arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId',
      'namespace',
      'repository'], config)

    jenkins.withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: config.dockerCredentialsId,
                      usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS']]) {

      //optum dtr occassionally throw gateway timeout 504 error, which usually self-correct after a retry or two
      //so i'm just using --retry option from curl to deal with that
      def checkRepoExistsCmd = "curl --retry 5 --silent --insecure --user '${jenkins.env.DOCKER_USER}':'${jenkins.env.DOCKER_PASS}' --header 'Accept: application/json' -X GET https://${config.dockerHost}/api/v0/repositories/${config.namespace}/${config.repository}"
      def response = jenkins.command(checkRepoExistsCmd, true)
      def jsonObject = null
      //the problem is now that curl response include 504 html response in addition to json response
      if(response?.contains('{')){
        //so just parse the json response and ignore the 504 response
        def firstJsonChar = response.indexOf('{')
        def jsonResponse = response.substring(firstJsonChar)
        jsonObject =  jenkins.readJSON text: jsonResponse
      }
      else {
        //give up after 5 retries, maybe optum dtr service is really down.
        //Provide curl response in jenkins error message for troubleshooting
        jenkins.error "Could not determine docker repository exist. This is the response from docker registry ${response}, please check if docker registry is online and operational"
      }

      //successful json response will either be json object with docker repository metadata
      //or json object with error message with "code": "NO_SUCH_REPOSITORY"
      if(jsonObject?.'id'){
        //do nothing, it's already there. provide a relevant message
        jenkins.echo "Docker repository ${config.namespace}/${config.repository} already exists, skipping creation"
      }
      else{
        //or create repo if doesn't exist
        def createRegistryCmd = "curl  -v  --user '${jenkins.env.DOCKER_USER}':'${jenkins.env.DOCKER_PASS}' --insecure -X POST --data \'{\"name\":\"${config.repository}\", \
        \"enableManifestLists\": " + Boolean.toString(config.enableManifestLists) + ", \
        \"immutableTags\": " + Boolean.toString(config.immutableTags) + ", \
        \"longDescription\": \"${config.longDescription}\",\
        \"scanOnPush\": " + Boolean.toString(config.scanOnPush) + ", \
        \"shortDescription\": \"${config.shortDescription}\", \
        \"visibility\": \"${config.visibility}\"}\' --header \"Content-type: application/json\" https://${config.dockerHost}/api/v0/repositories/${config.namespace}"

        jenkins.command(createRegistryCmd, false)
      }
    }
  }

/**
 * Deletes tags from Docker Trusted Registry based on their names. Uses tagRegex value
 * to find a list of tags to delete from and then deletes the 'oldest' numTags tags based on the delimited numbers.
 * Default pattern expects for tags is #.#.#.#.
 * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
 * @param dockerHost String The Docker registry host - defaults to docker.optum.com
 * @param dryRun boolean Whether or not to actually perform deletes - defaults to true
 * @param namespace String Required name of the organization or user account that holds the repo
 * @param numTagsRetained int The number of tags that match tagRegex to EXCLUDE from deletion (choice based on sort)
 * @param repository String Required The name of the repository that holds the tags you wish to delete
 * @param sortFlags String flags to pass to the unix command 'sort'.  Results in the sorted list to delete. - defaults to "-t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr"
 * @param tagRegex String The regex to use to match tags for deletion - defaults to "^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+\$"
 * @param verbose boolean Output tag names as you delete
 * */

  def deleteTagsByPattern(Map<String, Object> params) {
    def defaults = [
      dockerCredentialsId: null, //required
      dockerHost         : DEFAULT_DOCKER_HOST,
      dryRun             : true,
      namespace          : null, //required
      numTagsRetained    : 10,
      repository         : null, //required
      sortFlags          : '-t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr',
      tagRegex           : "^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+\$",
      verbose            : true
    ]
    def config = defaults + params

    jenkins.echo "deleteTagsByPattern arguments: $config"
    def dryRunString = Boolean.toString(config.dryRun)
    def verboseString = Boolean.toString(config.verbose)

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId',
      'namespace',
      'repository'], config)

    jenkins.withEnv(["DOCKER_HOST=$config.dockerHost", "NUM_TAGS=$config.numTagsRetained", "TAG_REGEX=$config.tagRegex", "VERBOSE=$verboseString",
             "DRY_RUN=$dryRunString", "NAMESPACE=$config.namespace", "REPO=$config.repository", "SORT_FLAGS=$config.sortFlags"]) {
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.dockerCredentialsId", passwordVariable: 'DOCKER_PASS',
        usernameVariable: 'DOCKER_USER')]) {
        jenkins.command '''
        curl -s -u ${DOCKER_USER}:${DOCKER_PASS} -k --header "Accept: application/json" -X GET -K - \\
        "https://$DOCKER_HOST/api/v0/repositories/$NAMESPACE/$REPO/tags?pageSize=10000" | jq -r ".[] | .name" > tags.txt

        # The grep gets just those tags that pass the tagRegex
        cat tags.txt | { egrep "${TAG_REGEX}" || echo ''; } > trimmed_tags.txt

        #Sorting based on number within delimiter
        #the _default_ sort sets . as the delimiter and sorts by each of the 4th sections of number
        #the awk only prints the ones after the first ${NUM_TAGS} lines - this gives me all tags EXCEPT for
        #the ${NUM_TAGS} most recent that are named like #.#.#.#
        cat trimmed_tags.txt | sort ${SORT_FLAGS} | awk "FNR >${NUM_TAGS}" > final_tags.txt
        delete_count=`cat final_tags.txt | wc -l`

        [ "$VERBOSE" = "true" ] && echo "Tags to delete: $delete_count\nTAG LIST:"; cat final_tags.txt
        #Delete the tags in this list
        if [ "$DRY_RUN" = "false" ]; then
        while read tag; do
        [ "$VERBOSE" = "true" ] && echo "Deleting tag $tag"
        [ "$DRY_RUN" = "false" ] && curl -s -u ${DOCKER_USER}:${DOCKER_PASS} -k --header "Accept: application/json" -X DELETE -K - \\
         "https://$DOCKER_HOST/api/v0/repositories/$NAMESPACE/$REPO/tags/$tag"
        done < final_tags.txt
        fi
      '''
      }
    }
  }

  /**
   * Pulls an image from the given Docker repository, changes the tags on it,
   * and pushes it back to the original repository with the new tag.
   * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
   * @param dockerHost String The Docker registry host - defaults to docker.optum.com
   * @param namespace String Required Name of the organization or user account that holds the repo
   * @param repository String Required The name of the repository that holds the tags you wish to delete
   * @param sourceTag String Required The image tag that will be converted
   * @param destNamespace String Optional field for specifying a different Docker namespace to retag an image to. Defaults to value of namespace otherwise
   * @param destRepo String Optional field for specifying a different Docker repository to retag an image to. Defaults to value of repository otherwise
   * @param destinationTag String Required The new image tag that will be pushed to Docker
   * */
  def retagRemoteDockerImage(Map<String, Object> params) {
    def defaults = [
      dockerCredentialsId : null, //required
      dockerHost          : DEFAULT_DOCKER_HOST,
      namespace           : null, //required
      repository          : null, //required
      sourceTag           : null, //required
      destNamespace       : "",
      destRepo            : "",
      destTag             : null  //required
    ]
    def config = defaults + params

    jenkins.echo "retagRemoteDockerImage arguments: $config"

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId',
      'namespace',
      'repository',
      'sourceTag',
      'destTag'], config)

    if(config.destNamespace == ""){
      config.destNamespace = config.namespace
    }
    if(config.destRepo == ""){
      config.destRepo = config.repository
    }

    jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.dockerCredentialsId", passwordVariable: 'DOCKER_PASS',
            usernameVariable: 'DOCKER_USER')]) {
      jenkins.command """
        echo -n 'user=$jenkins.env.DOCKER_USER:$jenkins.env.DOCKER_PASS' | curl -s -k --header "Content-Type: application/json" -X POST -K - \\
       "https://$config.dockerHost/api/v0/repositories/$config.namespace/$config.repository/tags/$config.sourceTag/promotion" \\
       -d '{ "targetRepository": "$config.destNamespace/$config.destRepo", "targetTag": "$config.destTag" }'
       """
    }
  }

  /**
   * Run the closure passed in after we log in to the Docker registry; always log out afterwards;  ONLY run the login if dockerCredentialsId is passed
   * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
   * @param dockerHost String The Docker registry host - defaults to docker.optum.com
   * @param command String The string to pass to jenkins.command to run after login
   * */
  def withDockerLogin(Map<String, Object> params, Closure body) {
    def defaults = [
      dockerCredentialsId : null, //required
      dockerHost          : DEFAULT_DOCKER_HOST, //optional
    ]
    def config = defaults + params

    jenkins.echo "withDockerLogin arguments: $config"


    //First perform the login only perform login/out if we have a credentials id
    if(config.dockerCredentialsId) dockerLogin dockerCredentialsId: config.dockerCredentialsId, dockerHost: config.dockerHost

    //Call the passed in closure
    body.call()

    //Logout
    if(config.dockerCredentialsId) dockerLogout dockerHost: config.dockerHost
  }

  /**
   * Log in to the target docker host
   * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
   * @param dockerHost String The Docker registry host - defaults to docker.optum.com
   * */
  private def dockerLogin(Map<String, Object> params) {
    def defaults = [
      dockerCredentialsId : null, //required
      dockerHost          : DEFAULT_DOCKER_HOST, //optional
    ]
    def config = defaults + params

    Utils utils = new Utils(jenkins)
    utils.requireParams((String[])['dockerCredentialsId'], config)

    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding',
        credentialsId: config.dockerCredentialsId,
        usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS']]) {

      // https://github.optum.com/jenkins-pipelines/global-pipeline-library/issues/322
      // Docker best practices note that using --password via the CLI is insecure.
      // Use --password-stdin instead if docker version is 18+ or version is v17.09+
      def loginCmd = "docker login --username ${jenkins.env.DOCKER_USER} --password '${jenkins.env.DOCKER_PASS}' $config.dockerHost \n"

      if (DockerCapabilities.supportsPasswordStdin(config.dockerVersion)) {
        // Provide password via --password-stdin
        loginCmd = "echo -n '${jenkins.env.DOCKER_PASS}' | docker login --username ${jenkins.env.DOCKER_USER} --password-stdin $config.dockerHost \n"
      }
      jenkins.command(loginCmd)
    }
  }

  /**
   * Log out of the target docker host
   * @param dockerHost String The Docker registry host - defaults to docker.optum.com
   * */
  private def dockerLogout(Map<String, Object> params) {
    def defaults = [
      dockerHost          : DEFAULT_DOCKER_HOST //optional
    ]
    def config = defaults + params
    jenkins.command("docker logout $config.dockerHost")
  }
}
