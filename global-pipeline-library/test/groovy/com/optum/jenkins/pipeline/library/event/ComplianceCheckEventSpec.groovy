package com.optum.jenkins.pipeline.library.event

import spock.lang.Specification

class ComplianceCheckEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new ComplianceCheckEvent(null, [:])
    when: 'Test execution event created with empty config'

    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      agileReleaseScope:null, CM1Data:null, CM2Data:null, CM3Data:null
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new ComplianceCheckEvent([:], [:])
    when: 'Getting the event topic'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.compliance'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new ComplianceCheckEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
            milestoneid:'MI1234',
            CM1Data:"[{\"userstoryId\":\"US111\"},{\"userstoryId\":\"US112\"}, {\"userstoryId\":\"US113\"}]",
            CM2Data:"[{\"a\":\"a\",\"b\":\"b\"}]",
            CM3Data:"[{\"c\":\"c\",\"d\":\"d\"}]"
    ]
    def event = new ComplianceCheckEvent(jenkins, config)
    when: 'Test execution event created with valid config'
    then: "get config with test execution metrics"
    event.getConfig().toString() == '[agileReleaseScope:null, CM1Data:[{"userstoryId":"US111"},{"userstoryId":"US112"}, ' +
      '{"userstoryId":"US113"}], CM2Data:[{"a":"a","b":"b"}], CM3Data:[{"c":"c","d":"d"}], milestoneid:MI1234]'
  }
}

