package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.sonar.Sonar
import spock.lang.Specification
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader

class SonarEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new SonarEvent(null, [:])
    when: 'Sonarevent created with empty config'
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getAskId(_) >> 'aid'
    OptumFileReader.getCAAgileId(_) >> 'caid'
    OptumFileReader.getTargetQG(_) >> '5'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      duration : null,
      status : null,
      scanTool: null,
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new SonarEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.sonar'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new SonarEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test getSonarMetrics method"() {
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    def event = new SonarEvent(jenkins, [:])

    when: 'Sonar metrics is called'
    Sonar sonarMock = Mock()
    GroovyMock(Sonar, global: true)
    new Sonar(jenkins) >> sonarMock
    sonarMock.getSonarMetrics(['additionalMetrics': 'false_positive_issues,' + 'wont_fix_issues']) >> ['metrics':'123']
    then: "sonar metrics returns with the sonar info"
    event.getSonarMetrics() == ['metrics':'123']
  }

  def "Test getSonarQualityGate method"() {
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    def event = new SonarEvent(jenkins, [:])

    when: 'Sonar metrics is called'
    Sonar sonarMock = Mock()
    GroovyMock(Sonar, global: true)
    new Sonar(jenkins) >> sonarMock
    sonarMock.getSonarQualityGate(['additionalMetrics': 'false_positive_issues,' + 'wont_fix_issues']) >> ['qualityGate':'3']
    then: "sonar metrics returns with the sonar info"
    event.getSonarQualityGate() == ['qualityGate':'3']
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
      duration: '30',
      status  : 'success',
      scanTool: 'maven',
      isPreview: false
    ]
    def event = new SonarEvent(jenkins, config)
    when: 'Sonarevent created with valid config'
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'com.company.busunit.app:component'
    OptumFileReader.getTargetQG(_) >> '5'
    Sonar sonarMock = Mock()
    GroovyMock(Sonar, global: true)
    new Sonar(jenkins) >> sonarMock
    sonarMock.getSonarMetrics(['additionalMetrics': 'false_positive_issues,' + 'wont_fix_issues']) >> ['metrics':'123']
    sonarMock.getSonarQualityGate(['additionalMetrics': 'false_positive_issues,' + 'wont_fix_issues']) >> ['qualityGate':'3']
    then: "get config with sonar metrics"
    event.getConfig().toString() == "[metrics:123, duration:30, status:success, scanTool:maven, targetQG:5, isPreview:false, qualityGate:3]"
  }
}
