package com.optum.jenkins.pipeline.library.security

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class ContrastSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def Contrast = new Contrast(jenkins)
    then: "Jenkins context is available"
    Contrast.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def Contrast = new Contrast()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  // TODO: Add unit tests for pullMetrics
}