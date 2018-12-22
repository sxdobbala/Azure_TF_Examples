package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.jenkins.pipeline.library.utils.PropertyReader.JenkinsfileReader
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import com.optum.jenkins.pipeline.library.utils.http.RequestParameters
import com.optum.jenkins.pipeline.library.utils.http.RestClient
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

class AbstractEventSpec extends Specification {

  @ConfineMetaClassChanges([RestClient])
  def "Empty endpoint environment parameter allows processing"() {
    given: 'Post Request is stubbed to always returns success'
      def stub = GroovyMock(Map)
      def myMockResponse = new RestClient.RestResponse(rc: 200, message: 'ok')
      1 * stub.post() >> {myMockResponse}
      RestClient.metaClass.static.post = { context, RequestParameters params -> stub.post() }
    and: 'No endpoint parameter set'
      def jenkins = [
        env: ['DEVOPS_METRICS_ENDPOINTS':''],
        echo: { message -> println message }
      ]
    when: "Sending the event"
      RestClient.RestResponse response = new TestEvent(jenkins, ['bla': 'blub']).send()
    then: 'The request is sent successfully'
      response == myMockResponse
  }

  @ConfineMetaClassChanges([RestClient])
  def "Missing endpoint environment parameter allows event processing by using defaults"() {
    given: 'Post Request is mocked to always returns success and to make sure to never be called'
      def stub = GroovyMock(Map)
      def myMockResponse = new RestClient.RestResponse(rc: 200, message: 'ok')
      1 * stub.post() >> {myMockResponse}
      RestClient.metaClass.static.post = { context, RequestParameters params -> stub.post() }
    and: 'No endpoint parameter set'
      def jenkins = [
        env: [:],
        echo: { message -> println message }
      ]
    when: "Sending the event"
      RestClient.RestResponse response = new TestEvent(jenkins, ['bla': 'blub']).send()
    then: 'The request is sent successfully'
      response == myMockResponse
  }

  @ConfineMetaClassChanges([RestClient])
  def "Enabled endpoints cause event posting"() {
    given: 'Post Request is mocked, always returns success and is called exactly once'
      def stub = GroovyMock(Map)
      1 * stub.post() >> {new RestClient.RestResponse(rc: 200, message: 'ok')}
      RestClient.metaClass.static.post = { context, RequestParameters params -> stub.post() }
    and: 'Endpoints are enabled'
      def jenkins = [
        env: [
          'DEVOPS_METRICS_ENDPOINTS':'http://metrics-rest-proxy-devops01.ose-elr-core.optum.com/',
          'DEVOPS_METRICS_ENABLED':'true'
        ],
        echo: { message -> println message }
      ]
    when: "Sending the event"
      RestClient.RestResponse response = new TestEvent(jenkins, ['bla': 'blub']).send()
    then: 'The request is sent successfully'
      response.rc == 200
      response.message == 'ok'
  }

  def "Event takes list of endpoints from environment variable"() {
    when: "2 Endpoints configured in environment "
      def jenkins = [
        env: [
          'DEVOPS_METRICS_ENDPOINTS': 'host1,host2'
        ],
        echo: {}
      ]
      def event = new TestEvent(jenkins, [:])

    then: "function returns list of the 2 endpoints"
      event.getHosts() == ['host1', 'host2']
  }

  def "Requesting the url path adds a topics prefix to the event topic"() {
    when:
      def event = new TestEvent([:], [:])
    then:
      event.path == 'topics/testtopic'
  }


  def "Event has to correct kafka content type"() {
    when:
      def event = new TestEvent([:], [:])
    then:
      event.contentType == 'application/vnd.kafka.json.v2+json'
  }

  void "Event has correct headers"() {
    when:
      def event = new TestEvent([:], [:])
    then:
      event.headers == [Accept: 'application/json']
  }

  @ConfineMetaClassChanges([BuildInfoReader, JenkinsfileReader, OptumFileReader, Date])
  def "Valid event data generates body"() {
    Date timestamp = Mock()
    GroovyMock(BuildInfoReader, global: true)
    GroovyMock(JenkinsfileReader, global: true)
    GroovyMock(OptumFileReader, global: true)
    GroovyMock(Date, global: true)
    def jenkins = [
      error: { msg -> throw new IllegalArgumentException(msg) },
      echo: {},
    ]

    given: "Required event property is valid"
      def config = [myProp: 'myvalue']

    and: "Metadata event properties are valid"
      OptumFileReader.getAskId(_) >> 'aid'
      OptumFileReader.getCAAgileId(_) >> 'cid'
      OptumFileReader.getProjectKey(_) >> 'fakeKey'
      OptumFileReader.getComponentType(_) >> OptumFileReader.ComponentType.CODE
      JenkinsfileReader.getLibraries(_) >> new JenkinsfileReader.Library(id: 'lib', version: 'v2')
      BuildInfoReader.getBuildNumber(_) >> 1
      BuildInfoReader.getBuildUrl(_) >> 'url'
      BuildInfoReader.getJenkinsVersion(_) >> 'v1'
      new Date() >> timestamp

    when: "Requesting the event data"
      Map body = new TestEvent(jenkins, config).getBody()

    then: "The generated body matches the event properties"
      body.records.size() == 1
      with(body.records[0].value) {
        askId == 'aid'
        caAgileId == 'cid'
        projectKey == 'fakeKey'
        componentType == OptumFileReader.ComponentType.CODE
        timestamp.toTimestamp() == timestamp.toTimestamp()
        pipelineLibraries[0].id == 'lib'
        pipelineLibraries[0].version == 'v2'
        buildId == 1
        buildUrl == 'url'
        buildSystemVersion == 'v1'
        myProp == 'myvalue'
      }
  }

  @ConfineMetaClassChanges([BuildInfoReader, JenkinsfileReader, OptumFileReader])
  def "Invalid metadata raises an error"() {
    def jenkins = [
      error: { msg -> throw new IllegalArgumentException(msg) },
      echo: {},
    ]
    GroovyMock(BuildInfoReader, global: true)
    GroovyMock(JenkinsfileReader, global: true)
    GroovyMock(OptumFileReader, global: true)

    given: "Required event property is valid"
      def config = [myProp: 'myvalue']

    and: "Metadata event property is invalid"
      OptumFileReader.getAskId(_) >> null

    when: "Requesting the event data"
      new TestEvent(jenkins, config).getBody()

    then: "An error about the invalid metadata property is raised"
      def e = thrown(IllegalArgumentException)
      e.getMessage().startsWith('invalid event metadata')
  }

  @ConfineMetaClassChanges([BuildInfoReader, JenkinsfileReader, OptumFileReader])
  def "Invalid event specific data raises an error"() {
    GroovyMock(BuildInfoReader, global: true)
    GroovyMock(JenkinsfileReader, global: true)
    GroovyMock(OptumFileReader, global: true)
    def jenkins = [
      error: { msg -> throw new IllegalArgumentException(msg) },
      echo: {}
    ]

    given: "Required event property is empty"
      def config = [myProp: null]

    and: "Metadata event properties are valid"
      OptumFileReader.getAskId(_) >> 'aid'
      OptumFileReader.getCAAgileId(_) >> 'cid'
      OptumFileReader.getProjectKey(_) >> 'fakeKey'
      OptumFileReader.getComponentType(_) >> OptumFileReader.ComponentType.CODE
      JenkinsfileReader.getLibraries(_) >> new JenkinsfileReader.Library([lib: 'version'])
      BuildInfoReader.getBuildNumber(_) >> 1
      BuildInfoReader.getBuildUrl(_) >> 'url'
      BuildInfoReader.getJenkinsVersion(_) >> 'v1'

    when: "Requesting the event data"
      new TestEvent(jenkins, config).getBody()

    then: "An error about the invalid event property is raised"
      def e = thrown(IllegalArgumentException)
      e.getMessage().startsWith('invalid event specific')
  }

  class TestEvent extends AbstractEvent {
    def jenkins
    def config = [myProp: 'testvalue']

    TestEvent(jenkins, config) {
      this.jenkins = jenkins
      this.config = this.config + config
    }

    @Override
    String getTopic() {
      return 'testtopic'
    }

    @Override
    Map getConfig() {
      config
    }

    @Override
    Map getInvalidEventProperties(Map config) {
      Map invalidProps = [:]
      if (isEmpty(config.myProp)) {
        invalidProps.put('myProp', config.myProp)
      }
      return invalidProps
    }

    @Override
    def getJenkins() {
      return jenkins
    }
  }
}
