package com.optum.jenkins.pipeline.library.utils

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class JenkinsMockSpec extends Specification {
  def "jenkins context is available"(){
    given: "Default jenkins context"
      def jenkins = new JenkinsMock()
    when: 'Creating class with jenkins context'
      def utils = new Utils(jenkins)
    then: "Jenkins context is available"
      utils.getJenkins() == jenkins
  }

  def "It can set an env and read it directly"(){
    given: "Jenkins has some env values loaded up"
      def expectedEnvValue = "Example value"
      def jenkins = new JenkinsMock( env : [ EXAMPLE_ENV_KEY: expectedEnvValue ] )
    when: "An env is read"
      def actualEnvValue = jenkins.env.EXAMPLE_ENV_KEY
    then: "The correct value is retrieved"
      actualEnvValue.contains(expectedEnvValue)
  }

  def "It can set an env and read it through the string method"(){
    given: "Jenkins has some env values loaded up"
      def expectedEnvValue = "Example value"
      def jenkins = new JenkinsMock( env : [ EXAMPLE_ENV_KEY: expectedEnvValue ] )
    when: "An env is read"
      jenkins.string(credentialsId: 'EXAMPLE_ENV_KEY', variable: 'BLAH')
    then: "The correct value is set in a new jenkins.env variable"
      jenkins.env.BLAH.contains(expectedEnvValue)
  }

  def "It has a withCredentials that works with the jenkins.string mock to allow tests to complete with proper credentials setup"(){
    given: "Jenkins has some env values loaded up"
      def expectedEnvValue = "Example value"
      def jenkins = new JenkinsMock( env : [ EXAMPLE_ENV_KEY: expectedEnvValue ] )
    when: "An env is read along with a withCredentials block"
      String actualResult
      jenkins.withCredentials([ jenkins.string(credentialsId: 'EXAMPLE_ENV_KEY', variable: 'BLAH') ]) {
        actualResult = jenkins.env.BLAH
      }
    then: "The correct value is set within the withCredentials block"
      actualResult.contains(expectedEnvValue)
  }

  def "It adds an empty env key if none was specified"(){
    // given: "Jenkins is instantiated with no env key"
    when: "An env is read along with a withCredentials block"
      def jenkins = new JenkinsMock()
    then: "it works"
      noExceptionThrown()
  }

  def "It enables the inspection of calls to 'command' through a 'calledJenkinsCommand' property"(){
    given: "Jenkins is instantiated"
      def jenkins = new JenkinsMock()
    when: "a command is issued to the jenkins object"
      String cmd1 = "echo 'hello'"
      String cmd2 = "echo 'these commands will be stored in an attribute on jenkins'"
      String cmd3 = "echo 'third cmd'"
      String expectedCommands = "$cmd1\n$cmd2\n$cmd3\n"
      jenkins.command(cmd1, true)
      jenkins.command(cmd2)
      jenkins.command(cmd3, false, "#!/bin/sh")
    then: "it can be retrieved from the calledJenkinsCommand property"
      jenkins.calledJenkinsCommand.equals(expectedCommands)
  }

  def "It returns pre-specified responses from calls to 'command' with 'returnStdout' set"() {
    given: "Jenkins is instantiated with specific command response specified"
      def jenkins = new JenkinsMock()
      String cmd = 'echo hello'
      String expectedRes = 'hello'
      jenkins.put(cmd.split()[0].trim(), expectedRes)
    when: "a command is issued to jenkins object with 'returnStdout = true'"
      def res = jenkins.command(cmd, true)
    then: "pre-specified response is returned"
      expectedRes.equals(res)
  }

}
