package com.optum.jenkins.pipeline.library.versions

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.scm.Git

import spock.lang.Specification
import spock.lang.Unroll

class VersionsSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
      def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
      def versions = new Versions(jenkins)
    then: "Jenkins context is available"
      versions.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
      def versions = new Versions()
    then: "IllegalArgumentException is thrown"
      def e = thrown(IllegalArgumentException)
      e.message.contains('"this" must be passed when creating new class instance')
  }

  @Unroll
  def "deriveNextSemanticVersion command is bumping version correctly '#testName'"() {
    given:
      def bumpedVersion
      def jenkins = [
        error       : { msg -> throw new JenkinsErrorException(msg) }
      ]
    when: "I run deriveNextSemanticVersion"
      def versions = new Versions(jenkins)
      bumpedVersion = versions.deriveNextSemanticVersion(config)
    then: "The version is bumped correctly"
      bumpedVersion == expectedResult
    where:
      testName            | config                                    | expectedResult
      'MAJOR'             | [patchLevel: 'MAJOR', version: '1.0.0']   | '2.0.0'
      'MINOR'             | [patchLevel: 'MINOR', version: '1.0.0']   | '1.1.0'
      'PATCH'             | [patchLevel: 'PATCH', version: '1.0.0']   | '1.0.1'
      'DEFAULT_UNLISTED'  | [patchLevel: 'asdas', version: '1.0.0']   | '1.0.1'
      'DEFAULT_EMPTY'     | [patchLevel: '', version: '1.0.0']        | '1.0.1'
      'DEFAULT_NULL'      | [patchLevel: null, version: '1.0.0']      | '1.0.1'
  }

  def "deriveNextSemanticVersion command is missing required parameter"() {
    given:
      def errorMessage = 'Please provide a version you want to derive, version must be of type java.lang.String or com.optum.jenkins.pipeline.library.versions.SemVer and conform to https://semver.org/ standard'
      def bumpedVersion
      def calledJenkinsCommand
      def jenkins = [
        error       : { msg -> throw new JenkinsErrorException(msg) }
      ]
    when: "I run deriveNextSemanticVersion"
      def config = [
        patchLevel: 'MAJOR',
        version: null
      ]
      def versions = new Versions(jenkins)
      bumpedVersion = versions.deriveNextSemanticVersion(config)
    then: "Exception is thrown"
      JenkinsErrorException e = thrown()
      e.message.contains(errorMessage)
  }

  @Unroll
  def "deriveLatestSemanticVersionFromGitTag command is returning version correctly '#testName'"() {
    given:
      def latestSemanticVersion
      def calledJenkinsCommand
      def jenkins = [
        error       : { msg -> throw new JenkinsErrorException(msg) },
        command     : { String cmd, Boolean output = true -> calledJenkinsCommand = [cmd,output] }
      ]
    when: "I run deriveLatestSemanticVersionFromGitTag"
      Git gitMock = Mock()
      GroovyMock(Git, global: true)
      new Git(jenkins) >> gitMock
      gitMock.getGitTagNames(config) >> tag
      def versions = new Versions(jenkins)
      latestSemanticVersion = versions.deriveLatestSemanticVersionFromGitTag(config)
    then: "The version is returned correctly"
      latestSemanticVersion == expectedResult
    where:
      testName                | config                                                | tag       | expectedResult
      'TAG_EXISTS_W_PREFIX'   | [gitTagPrefix: 'v', defaultInitialVersion: '1.0.0']   | 'v1.0.0'  |  '1.0.0'
      'TAG_NOT_EXISTS'        | [gitTagPrefix: 'v', defaultInitialVersion: '1.0.0']   | null      |  '1.0.0'
      'TAG_EXISTS_WO_PREFIX'  | [gitTagPrefix: '', defaultInitialVersion: '1.0.0']    | '1.0.0'   |  '1.0.0'
  }
}
