package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.event.TestExecutionEvent
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import spock.lang.Specification

class TestExecutionEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new TestExecutionEvent(null, [:])
    when: 'Test execution event created with empty config'

    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
            duration:null,
            testType:null,
            totalTests:null,
            testsExecuted:null,
            testsPassed:null,
            testsFailed:null
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new TestExecutionEvent([:], [:])
    when: 'Getting the event topic'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.test'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new TestExecutionEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
            duration:'30',
            testType:'Regression',
            totalTests:'100',
            testsExecuted:'100',
            testsPassed:'99',
            testsFailed:'1',
    ]
    def event = new TestExecutionEvent(jenkins, config)
    when: 'Test execution event created with valid config'
    then: "get config with test execution metrics"
    event.getConfig().toString() == '[duration:30, testType:Regression, totalTests:100, testsExecuted:100, testsPassed:99, testsFailed:1]'
  }
}

