package com.optum.jenkins.pipeline.library.event

import spock.lang.Specification

class FortifyCloudEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new FortifyCloudEvent(null, [:])
    when: 'Fortify cloud event created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
            duration : null,
            cloudJobSubmitToken:null,
            fortifyBuildName:null,
            translateExclusions:null,
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new FortifyCloudEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.fortify.cloud'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new FortifyCloudEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
            duration:'30',
            //status  :'success',
            cloudJobSubmitToken:'abcdef-1234',
            fortifyBuildName:'fortify1',
            translateExclusions:'src/test',
            scarProjectName:'ScarProjectName',
            scarProjectVersion:'All'
    ]
    def event = new FortifyCloudEvent(jenkins, config)
    when: 'Fortify event created with valid config'
    then: "get config with fortify token"
    event.getConfig().toString() == '[duration:30, status:STARTED, cloudJobSubmitToken:abcdef-1234, fortifyBuildName:fortify1, translateExclusions:src/test, scarProjectName:ScarProjectName, scarProjectVersion:All, scanType:cloud]'}
}
