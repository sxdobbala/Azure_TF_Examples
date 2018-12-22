#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.scm

class Git implements Serializable {
  def jenkins

  Git() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Git(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Wrapper for `checkout scm` for GIT
 */

  def checkout() {

    jenkins.echo "........................................................................"

    // checkout source into the node
    jenkins.checkout jenkins.scm

    jenkins.echo "scm url: ${getRemoteUrl()}"

    // echo the revision HASH
    jenkins.echo "revision hash: ${getRevision()}"

  }

/**
 * Checkout to a branch or tag.
 *
 * @params credentialsID
 * @params tag
 * @params url
 *
 * Examples:
 *    checkoutTag( credentialsId: '123', tag: 'refs/tags/v1.2.3')
 *    checkoutTag( credentialsId: '123', tag: 'master')
 *    checkoutTag( credentialsId: '123', tag: 'refs/tags/v1.2.3',
 *        url: 'https://github.optum.com/org/repo')
 */
  def checkoutTag(Map<String,Object> params) {

    def defaults = [
        credentialsId: null,    // required
        tag: null,              // required
        url: null,              // optional
    ]
    def config = defaults + params

    // use pipeline config to clone git repo
    if (config.url == null) {
        jenkins.checkout jenkins.scm
        config.url = getRemoteUrl()
    }
    // use available env variables to checkout branch/tag
    jenkins.checkout scm: [$class: 'GitSCM', userRemoteConfigs: [config],
        branches: [[name: config.tag]]], poll: false

    jenkins.echo "scm url: ${getRemoteUrl()}"

    // echo the revision HASH
    jenkins.echo "revision hash: ${getRevision()}"

  }

/**
 * Get the HEAD revision hash.
 */
  def getRevision() {
    jenkins.command 'git rev-parse HEAD', true
  }

  def getRevisionShort() {
    jenkins.command 'git rev-parse --short HEAD', true
  }

  def getGitAuthorName() {
    jenkins.command 'git --no-pager show -s --format=\'%an\'', true
  }

  def getGitAuthorEmail() {
    jenkins.command 'git --no-pager show -s --format=\'%ae\'', true
  }

  def getGitAuthorDate() {
    jenkins.command 'git --no-pager show -s --format=\'%ai\'', true
  }

  def getGitCommitterName() {
    jenkins.command 'git --no-pager show -s --format=\'%cn\'', true
  }

  def getGitCommitterEmail() {
    jenkins.command 'git --no-pager show -s --format=\'%ce\'', true
  }

  def getGitCommitterDate() {
    jenkins.command 'git --no-pager show -s --format=\'%ci\'', true
  }

  def getGitSubject() {
    jenkins.command 'git --no-pager show -s --format=\'%s\'', true
  }

  def getGitSanitizedSubject() {
    jenkins.command 'git --no-pager show -s --format=\'%f\'', true
  }

  def getGitTagNames(Map<String, Object> params) {
    def defaults = [
      gitCredentials: null,         // required if jenkins is configured to use ssh checkout or checking out private repo
    ]

    def config = defaults + params
    def gitUrl = getRemoteUrl()
    def httpGitUrl = gitUrl.tokenize('://')
    def gitProtocol = gitUrl.startsWith('git@') ? 'ssh' : httpGitUrl[0]
    def gitHostname = gitUrl.startsWith('git@') ? gitUrl.split(/[@:\/]/)[1] : httpGitUrl[1].tokenize('/')[0]
    def gitRemoteTagCmd = "git ls-remote --tags origin | awk '{print \$2}' | grep -v '\\^{}\$' | sort -r -V | sed 's@refs/tags/@@' "
    //another easier way to get latest tag is by using this command
    //git describe --abbrev=0 --tags || echo ''
    //but the catch is that, by default jenkins git plugin does not checkout tags unless specifically configured
    //in order to make this function work with default configuration git ls-remote tag is used
    //in combination of other commands to get the tags by name only
    //and git ls-remote is a remote operation that may require additional credential if ssh checkout is used

    def gitTags = ''
    def gitTagsList = []
    switch (gitProtocol) {
      case 'ssh':
        if(config.gitCredentials){
          jenkins.sshagent (credentials: [config.gitCredentials]) {
            jenkins.command( 'ssh-keyscan -H ' + gitHostname + ' >> ~/.ssh/known_hosts' )
            gitTags = jenkins.command gitRemoteTagCmd, true
          }
          break
        }
        else{
          jenkins.error('If you\'re using ssh checkout, please provide credential id when calling this method. your credential is expected to be ssh key')
        }
      case 'https':
        if(config.gitCredentials){
          jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding',
              credentialsId: config.gitCredentials,
              usernameVariable: 'GIT_USERNAME',
              passwordVariable: 'GIT_PASSWORD']]) {
            gitUrl = gitUrl.replace('https://', "https://${jenkins.env.GIT_USERNAME}:${jenkins.env.GIT_PASSWORD}@")
            gitRemoteTagCmd = gitRemoteTagCmd.replace('origin', gitUrl)
            gitTags = jenkins.command gitRemoteTagCmd, true
          }
        }
        else{
          gitTags = jenkins.command gitRemoteTagCmd, true
        }
        break
    }
    gitTagsList = gitTags?.trim()?.split('\r?\n') ?: []
    return gitTagsList
  }

/**
 * legacy code for users that does not use standard jenkins slave
 */
  def getSha() {
    jenkins.echo 'fyi, this information is already available in GIT_COMMIT environment variable'
    jenkins.echo "env.GIT_COMMIT : ${jenkins.env.GIT_COMMIT}"
    return jenkins.sh (script: 'git rev-parse HEAD', returnStdout: true)
  }

/**
 * Get the remote URL.
 */
  def getRemoteUrl() {
    jenkins.command 'git config remote.origin.url', true
  }

/**
 * Get the current branch name.
 * Hacky, because jenkins checks out a specific commit hash -> detached Head state -> messes up regular branch name fetching
 * and env.BRANCH_NAME is only available for some job types
 *
 * workaround: get path -> remotes/origin/testbranch -> extract "testbranch"
 */
  def getBranch() {
    String branch = null
    if (jenkins.env.GIT_BRANCH) {
      jenkins.echo('GIT_BRANCH exists, value is: ' + jenkins.env.GIT_BRANCH)
      branch = jenkins.env.GIT_BRANCH
    } else {
      jenkins.echo('GIT_BRANCH doesn\'t exist, trying to get the branch using \'git name-rev --name-only HEAD\'')
      def path = jenkins.command 'git name-rev --name-only HEAD', true
      if (path != null && path != 'undefined') {
        jenkins.echo('Path: ' + path)
        def pathElements = path.split('remotes/origin/')
        jenkins.echo('Trying to get branch from remotes/origin/, path element size: ' + pathElements.size())
        if (pathElements.size() > 1) {
          branch = pathElements[1]
          jenkins.echo('Found branch name: ' + branch)
        } else {
          jenkins.command 'git log --graph --all --color --pretty=format:\'%x1b[31m%h%x09%x1b[32m%d%x1b[0m%x20%s\''
          jenkins.error 'Extracting the git branch name using the git path is currently not supported by the the global-pipeline-library for your specific setup .\n' +
            'Please contact the JPaC team to find a solution.\n'
        }
      } else if (jenkins.env.BRANCH_NAME) {
        jenkins.echo('BRANCH_NAME exists, value is: ' + jenkins.env.BRANCH_NAME)
        //Pull request build job that consists of Master + merged feature branch, rely on Jenkins plugin to set env.BRANCH_NAME
        branch = jenkins.env.BRANCH_NAME
      } else {
        jenkins.command 'git log --graph --all --color --pretty=format:\'%x1b[31m%h%x09%x1b[32m%d%x1b[0m%x20%s\''
        jenkins.error 'Extracting the git branch name for your specific setup is currently not supported by the the global-pipeline-library.\n' +
          'Please contact the JPaC team to find a solution\n'
      }
    }
    String originPrefix='origin/'
    if(branch.startsWith(originPrefix)){
      jenkins.echo('removing branch prefix: ' + originPrefix)
      branch = branch.replace(originPrefix, '')
    }
    return branch
  }

/**
 * Check if string appears in most recent changeset
 *
 * Usage: use on the same node/stage as where git checkout occurred and store
 *  result to a variable visible to all nodes that need it. 'unstash'ed repos
 *  don't retain git repo descriptors.
 *  `
 *    def isProjectChanged = headChangesetHas('project-1')
 *    ...
 *    if (isProjectChanged) { do stuff }
 *  `
 *
 */
  def headChangesetHas(string) {
    def numMatches = jenkins.sh(
        script: "echo \$(git diff-tree --no-commit-id --name-only -r HEAD | grep -o '$string' | wc -l)",
        returnStdout: true).trim()
    def hasProject = jenkins.sh(
        script: "if [ $numMatches -gt 0 ]; then echo 1; else echo 0; fi",
        returnStdout: true).trim()
    if (hasProject=="1") {
        return true
    } else if (hasProject=="0") {
        return false
    } else {
        echo "Parsing error: hasProject is $hasProject"
        return false
    }
  }

/**
 * Check if multiple strings appear in most recent changeset. Return a map:
 *  [string] -> [is string in changeset]
 *
 * Usage: use on the same node/stage as where git checkout occurred and store
 *  result to a variable visible to all nodes that need it. 'unstash'ed repos
 *  don't retain git repo descriptors.
 *  `
 *    projectsChanged = headChangesetHasList(projectList)
 *    ...
 *    if (projectsChanged['proj-1']) { do stuff }
 *  `
 *
 */
  def headChangesetHasList(stringList) {
    def output = [:]
      for (item in stringList) {
          output.put(item, headChangesetHas(item))
      }
      return output
  }

/**
  * Creates a git tag and pushes it
  *
  * @param credentialsId String Required Credentials to git push
  * @param repoUrl String Required The repo url getting tagged
  *      Example: "github.optum.com/org/repo"
  * @param tag String Required Tag annotation
  *      Example: "app-name-1.0.0.54"
  * @param userName String Required User name with which to execute git tag
  * @param userEmail String Required Email with which to execute git tag
  * @param tagMessage String Optional Description for the tag
  *
  */
  def tagAndPush(Map<String,Object> params){
    def defaults = [
        credentialsId: null, // required
        repoUrl: 'github.optum.com/org/repo', //required
        tag: null,           // required
        userName: null,      // required
        userEmail: null,     // required
        tagMessage: 'Tagged by Jenkins', //optional
    ]
    def config = defaults + params
    if (config.repoUrl.contains('https://')) {
	config.repoUrl = config.repoUrl.replaceFirst(/https\:\/\//, '')
    }

    jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding',
        credentialsId: config.credentialsId,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD']]) {
      jenkins.command """
          git config user.email "${config.userEmail}"
          git config user.name "${config.userName}"
          git tag -a ${config.tag} -m ${config.tagMessage}
          git push https://${jenkins.env.GIT_USERNAME}:${jenkins.env.GIT_PASSWORD}@${config.repoUrl} ${config.tag}
        """
      }
  }

}
