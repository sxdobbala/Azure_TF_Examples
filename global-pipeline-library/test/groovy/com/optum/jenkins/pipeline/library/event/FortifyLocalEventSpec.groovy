package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.sca.Fortify
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import spock.lang.Specification

class FortifyLocalEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new FortifyLocalEvent(null, [:])
    when: 'Fortify local event created with empty config'

    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
            duration : null,
            duration : null,
            status : null,
            fortifyBuildName:null,
            translateExclusions: null
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new FortifyLocalEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.fortify.local'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new FortifyLocalEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
            duration: '30',
            status  : 'success',
            translateExclusions:'src/test',
            fortifyBuildName:'fortify1',
            scarProjectName:'ScarProjectName',
            scarProjectVersion:'All',
            issuesMap: ['LOWIssues':3,'MEDIUMIssues':4,'CriticalIssues':5]
    ]
    def event = new FortifyLocalEvent(jenkins, config)
    when: 'Fortify event created with valid config'
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getScarProjectName(_) >> 'ScarProjectName'
    OptumFileReader.getScarProjectVersion(_) >> 'All'
    OptumFileReader.getTranslateExclusions(_) >> 'src/test'
    Fortify fortifyMock = Mock()
    GroovyMock(Fortify, global: true)
    new Fortify(jenkins) >> fortifyMock
    then: "get config with fortify metrics"
    event.getConfig().toString() == '[LOWIssues:3, MEDIUMIssues:4, CriticalIssues:5, duration:30, status:success, translateExclusions:src/test, fortifyBuildName:fortify1, scarProjectName:ScarProjectName, scarProjectVersion:All, scanType:local]'
  }
}

