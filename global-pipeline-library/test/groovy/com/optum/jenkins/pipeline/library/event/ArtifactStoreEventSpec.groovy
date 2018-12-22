package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.maven.MavenBuild
import com.optum.jenkins.pipeline.library.sonar.Sonar
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import spock.lang.Specification

class ArtifactStoreEventSpec extends Specification{

  def "Empty required config raises error"(){
    given: "Required config values are not set"
    def event = new ArtifactStoreEvent(null, [:])
    when: 'ArtifactStoreEvent created with empty config'
    Map invalids = event.getInvalidEventProperties(event.getConfig())
    then: "all required properties are invalid"
    invalids == [
      duration : null,
      status : null
    ]
  }

  def "Kafka event name is correct"(){
    given: "Default Event config"
    def event = new ArtifactStoreEvent([:], [:])
    when: 'Getting the event top'
    def topic = event.getTopic()
    then: "topic matches expectation"
    topic == 'devops.artifact.store'
  }

  def "Test jenkins context"(){
    given: "Default jenkins context"
    def jenkins = [test : 'test']
    when: 'Creating event with jenkins context'
    def event = new ArtifactStoreEvent(jenkins, [:])
    then: "context is saved and retrievable"
    event.getJenkins() == jenkins
  }

  def "Test with valid config"() {
    given: "Required config values are set"
    def jenkins = [test : 'test']
    Map config = [
      duration: '30',
      status  : 'success'
    ]
    def event = new ArtifactStoreEvent(jenkins, config)
    when: 'ArtifactStoreEvent created with valid config'
    GroovyMock(OptumFileReader, global: true)
    MavenBuild mavenBuildMock = Mock()
    GroovyMock(MavenBuild, global: true)
    new MavenBuild(jenkins) >> mavenBuildMock
    mavenBuildMock.deployToArtifactory test: "test"
    then: "get config with sonar metrics"
    event.getConfig().toString() == "[duration:30, status:success]"
  }


}
