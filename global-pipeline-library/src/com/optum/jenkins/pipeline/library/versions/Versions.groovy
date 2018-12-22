#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.versions

import com.optum.jenkins.pipeline.library.scm.Git

class Versions implements Serializable {
  def jenkins

  Versions() throws IllegalArgumentException {
    throw new IllegalArgumentException('"this" must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Versions(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Bump semantic version according to the version bump level MAJOR/MINOR/PATCH
 * @param patchLevel valid values "MAJOR" "MINOR" "PATCH"(default), patch level will bump the specific version field
 * @param version version in format of semantic versioning MAJOR.MINOR.PATCH
 * @see <a href="https://semver.org/">https://semver.org/</a>
 *
 */
  def deriveNextSemanticVersion(Map<String,Object> params){
    def defaults = [
      patchLevel: 'PATCH',      // optional
      version   : null          // required
    ]

    def config = defaults + params

    if (!config.version) {
      jenkins.error('Please provide a version you want to derive, version must be of type java.lang.String or com.optum.jenkins.pipeline.library.versions.SemVer and conform to https://semver.org/ standard')
    }
    try {
      def semVer = (config.version.getClass() == SemVer) ? config.version : new SemVer(config.version)
      semVer.bump(config.patchLevel).toString()
    }
    catch (IllegalArgumentException ex) {
      jenkins.error('There was a problem when trying to derive next version. ' + ex.getMessage())
    }
  }

/**
 * Get latest semantic version from git tags
 * @param gitCredentials String required if one of these condition is fulfiledd:
 *        jenkins is configured to use ssh checkout, credential type is expected to be ssh key
 *        jenkins is configured to use https checkout to private repo, credential type is expected to be username/password
 * @param gitTagPrefix String optional, default empty. if you're using github recommended prefix, provide 'v'
 * @param defaultInitialVersion String optional, default to '0.0.0'
 */
  def deriveLatestSemanticVersionFromGitTag(Map<String, Object> params){
    def defaults = [
      gitCredentials       : null,          // required if jenkins is configured to use ssh checkout or private repo
      gitTagPrefix         : '',            // optional
      defaultInitialVersion: '0.0.0'        // optional
    ]

    def config = defaults + params
    def git = new Git(jenkins)

    def gitTagNames = git.getGitTagNames(config)
    //getGitTagNames will return a list of git tags in string descending order

    def latestVersion = config.defaultInitialVersion ?: ''

    def semVerRegex = "^${config.gitTagPrefix}[0-9]+\\.[0-9]+\\.[0-9]+\$"
    //loop through each tag names
    for (eachTag in gitTagNames) {
      if(eachTag ==~ /${semVerRegex}/){
        //if a tag name found with matching regex, extract the version from it
        latestVersion = config.gitTagPrefix ? eachTag.replace(config.gitTagPrefix, '') : eachTag
        //return latest tag derived version if found
        return latestVersion
      }
    }
    //return defaultInitialVersion if no tag derived version is found
    return latestVersion
  }
}
