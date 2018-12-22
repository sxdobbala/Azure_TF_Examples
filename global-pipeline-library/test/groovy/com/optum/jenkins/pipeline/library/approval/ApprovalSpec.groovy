package com.optum.jenkins.pipeline.library.approval

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.utils.JenkinsMock
import spock.lang.Specification
import spock.lang.Unroll

class ApprovalSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = new JenkinsMock()
    when: 'Creating class with jenkins context'
    def approval = new Approval(jenkins)
    then: "Jenkins context is available"
    approval.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def approval = new Approval()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

}
