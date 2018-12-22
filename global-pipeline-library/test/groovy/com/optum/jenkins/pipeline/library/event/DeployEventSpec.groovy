package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.event.DeployEvent
import spock.lang.Specification

class DeployEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new DeployEvent(null, [:])
    when: 'DeployEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      duration: null,
      status: null,
      deployTool: null,
      production: null,
      env: null,
      platform: null
    ]
  }

  def "Non boolean value for production raises error"(){
    given: "Required config values are not set"
    def jenkins = [test : 'test']
    def event = new DeployEvent(jenkins, [duration: 10, status: 'FAILURE', deployTool: 'jenkins', env : 'dev', platform: 'OCP', production: '123'])
    when: 'DeployEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      production: '123'
    ]
  }

  def "Boolean value for production does not produce invalids"(){
    given: "Required config values are not set"
    def jenkins = [test: 'test']
    def event = new DeployEvent(jenkins, [duration: 10, status: 'FAILURE', deployTool: 'jenkins', env : 'dev', platform: 'OCP', production: true])
    when: 'DeployEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [:]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new DeployEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.deploy'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new DeployEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }
}
