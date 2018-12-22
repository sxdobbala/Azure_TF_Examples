package com.optum.jenkins.pipeline.library.security

import spock.lang.Specification

class ArachniSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def arachni = new Arachni(jenkins)
    then: "Jenkins context is available"
    arachni.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def arachni = new Arachni()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "scanWithArachni command is structured correctly"() {
    given:
    def calledShCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def jenkins = [
      echo    : {},
      sh      : { String cmd -> calledShCommand = cmd },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = '/attack_scripts/attack_address_input.sh https://someTargetAddress.com optumObjectStorageBucket'
    when: "I scan with arachni"
    def arachni = new Arachni(jenkins)
    def config = [
      targetAddress : "https://someTargetAddress.com",
      scanUsername  : "username",
      scanPassword  : "userpassword",
      OOSSBucketName: "optumObjectStorageBucket",
      OOSSUsername  : "username1",
      OOSSPassword  : "password1"
    ]
    arachni.scanWithArachni(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    calledShCommand == expectedCmd
  }
}
