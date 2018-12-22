package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.utils.Cache
import spock.lang.Specification

class BuildInfoReaderSpec extends Specification {


  def "Build id read from jenkins environment"() {
    when: "Environment contains build id"
      def jenkins = [env: [BUILD_ID: 123]]
    then: "return correct id"
      BuildInfoReader.getBuildNumber(jenkins) == 123
  }

  def "Build url read from jenkins environment"() {
    when: "Environment contains build url"
      def jenkins = [env: [BUILD_URL: "host/job/nr"]]
    then: "return correct url"
      BuildInfoReader.getBuildUrl(jenkins) == 'host/job/nr'
  }

  def "Read Jenkins base url from jenkins environment"() {
    when: "Environment contains jenkins base url"
      def jenkins = [env: [JENKINS_URL: "host/context"]]
    then: "return correct url"
      BuildInfoReader.getBuildRootUrl(jenkins) == 'host/context'
  }

  def "Read Kafka endpoints from jenkins environment"() {
    when: "Environment contains kafka endpoints"
      def jenkins = [
        env: [DEVOPS_METRICS_ENDPOINTS: "kafka1,kafka2"],
        echo: {}
      ]
    then: "return correct endpoints"
      BuildInfoReader.getDevopsMetricsEndpoints(jenkins) == 'kafka1,kafka2'
  }

  def "Read Kafka endpoints when not in jenkins environment"() {
    when: "Environment does not contains kafka endpoints"
    def jenkins = [
      env: [],
      echo: {}
    ]
    then: "return correct endpoints"
    BuildInfoReader.getDevopsMetricsEndpoints(jenkins) == 'http://metrics-rest-proxy-devops01.ose-elr-core.optum.com/'
  }

  def "Reading jenkins version from server"() {
    given: 'Jenkins mock setup for version 1.0.0'
      def jenkinsMock = [
        echo: { message -> 'nop' },
        command: { String sh, Boolean flag -> return 'X-Jenkins: 1.0.0' },
        env: [JENKINS_URL: '']
      ]
    and: "the version cache is empty"
      Cache.getInstance().clear()
    when: 'requesting the jenkins version'
      def version = BuildInfoReader.getJenkinsVersion(jenkinsMock)
    then: 'the version matches'
      version == '1.0.0'
  }

  def "Unsuccessful reading of jenkins version from server returns 'na' as Jenkins version"() {
    given: 'The Jenkins mock is set up to return an invalid Jenkins version response'
      def jenkinsMock = [
        command: { String sh, Boolean flag -> return 'yeah, this is not the expected Jenkins version reply' },
        env: [JENKINS_URL: ''],
        echo: {msg -> println msg}
      ]
    and: "the version cache is empty"
      Cache.getInstance().clear()
    when: 'requesting the jenkins version'
      def version = BuildInfoReader.getJenkinsVersion(jenkinsMock)
    then: "the Jenkins version is set to 'na'"
      version == 'na'
  }

}
