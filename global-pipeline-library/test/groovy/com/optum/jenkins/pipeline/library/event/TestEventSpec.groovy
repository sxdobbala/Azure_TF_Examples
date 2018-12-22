package com.optum.jenkins.pipeline.library.event


import spock.lang.Specification

class TestEventSpec extends Specification {

  def "Empty required config raises error"() {
    given: "Required config values are not set"
    def event = new TestEvent(null, [:])
    when: 'TestEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      duration: null,
      status  : null,
      testtype: null
    ]
  }

  def "Invalid test type raises error"() {
    given: "Required config values are not set"
    def event = new TestEvent(null, [testtype: 'foo'])
    when: 'TestEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      duration: null,
      status  : null,
      testtype: 'foo'
    ]
  }

  def "Kafka event name is correct"() {
    given: "Default Event config"
    def event = new TestEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.test'
  }

  def "Test jenkins context"() {
    given: "Default jenkins context"
    def jenkins = [test: 'test']
    when: 'Creating event with jenkins context'
    def event = new TestEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

}
