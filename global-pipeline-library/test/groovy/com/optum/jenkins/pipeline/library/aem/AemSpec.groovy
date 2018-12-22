package com.optum.jenkins.pipeline.library.aem

import spock.lang.Specification
import com.optum.jenkins.pipeline.library.utils.JenkinsMock

class AemSpec extends Specification {
    def "jenkins context is available"(){
      given: "Default jenkins context"
        def jenkins = new JenkinsMock()
      when: 'Creating class with jenkins context'
        def aem = new Aem(jenkins)
      then: "Jenkins context is available"
        aem.getJenkins() == jenkins
    }

    def "error for missing jenkins context"(){
      when: 'Creating class without jenkins context'
        def aem = new Aem()
      then: "Exception is thrown"
        def e = thrown(Exception)
        e.message.contains('"this" must be passed when creating new class')
    }
}
