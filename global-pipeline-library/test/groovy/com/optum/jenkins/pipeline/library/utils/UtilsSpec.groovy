package com.optum.jenkins.pipeline.library.utils

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class UtilsSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = [echo : 'hello']
    when: 'Creating class with jenkins context'
    def utils = new Utils(jenkins)
    then: "Jenkins context is available"
    utils.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def utils = new Utils()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "retry fails after two times"(){
    given: "Jenkins has an echo method"
    def jenkins = [echo : {}]
    def utils = new Utils(jenkins)
    when: "Retry is called with times: 2"
    def retryConfig = [
            times: 2,
            delay: 0
    ]
    def body = {throw new Exception()}
    utils.retry(retryConfig, body)
    then: "An exception is thrown"
    def e = thrown(Exception)
    e.message.contains('Failed after 2 retries')
  }

  def "retry succeeds the first time"(){
    given: "Jenkins has an echo method"
    def jenkins = [echo : {}]
    def utils = new Utils(jenkins)
    when: "Retry is called with times: 2"
    def retryConfig = [
        times: 2,
        delay: 0
    ]
    def body = {print('success')}
    utils.retry(retryConfig, body)
    then: "No exeption is thrown"
    noExceptionThrown()
  }

  def "duration is non zero"(){
    given: 'Default jenkins context'
    def jenkins = [echo : {}]
    def utils = new Utils(jenkins)
    def long my_duration = 0
    when: 'StartTime is current time and sleep 1 second, find duration'
    def startTime = new Date()
    sleep(1000)  // sleep for 1 second
    my_duration = utils.getDuration(startTime)
    then: "Duration in seconds should be greater than 0"
    my_duration > 0
  }

  def "requireParams doesn't find param"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
    ]
    def utils = new Utils(jenkins)

    when: 'We check for a parameter that is not provided'
    def config = [
      foundParameter  : "someValue",
    ]
    utils.requireParams((String[])['notFoundParameter'], config)
    then: "an exeption is thrown"
    def e = thrown(Exception)
    e.message.contains('Required parameters missing')
  }

  def "requireParams finds all params"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
    ]
    def utils = new Utils(jenkins)

    when: 'We check for a parameter that should be found'
    def config = [
      foundParameter  : "someValue",
    ]
    utils.requireParams((String[])['foundParameter'], config)
    then: "No exeption is thrown"
    noExceptionThrown()
  }

  def "gStage fails without required params"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      echo            : {},
      node            : {},
      stage           : { body -> body.call() },
    ]
    def utils = new Utils(jenkins)

    when: 'gStage is called'
    utils.gStage noname: "Unit Test Stage", {
      jenkins.echo "test"
    }

    then: "It fails without the proper parameters"
    def e = thrown(Exception)
    e.message.contains('name')
  }
}
