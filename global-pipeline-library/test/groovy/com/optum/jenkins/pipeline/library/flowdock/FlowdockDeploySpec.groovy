package com.optum.jenkins.pipeline.library.flowdock

import com.optum.jenkins.pipeline.library.openshift.OpenShift
import spock.lang.Specification
import com.optum.jenkins.pipeline.library.utils.JenkinsMock

class FlowdockDeploySpec extends Specification {
    def "jenkins context is available"(){
      given: "Default jenkins context"
        def jenkins = new JenkinsMock()
      when: 'Creating class with jenkins context'
        def flowdock = new Flowdock(jenkins)
      then: "Jenkins context is available"
        flowdock.getJenkins() == jenkins
    }

    def "error for missing jenkins context"(){
      when: 'Creating class without jenkins context'
        def flowdock = new Flowdock()
      then: "Exception is thrown"
        def e = thrown(Exception)
        e.message.contains('`this` must be passed when creating new class')
    }

    def "can call flowdock with user token"(){
      given:
        def message = 'hello world!'
        def flow = 'jenkins-pipeline-as-code'
        def credentialsId = 'FLOWDOCK_API_PERSONAL'
        def token = 'hunter2'
        def encodedToken = (token + ':\n').bytes.encodeBase64().toString()
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_PERSONAL: token]
        )
        def params = [
                flow: flow,
                message: message,
                credentialsId: credentialsId
        ]
      when: "flowdock is signaled"
        def flowdock = new Flowdock(jenkins)
        flowdock.signalFlowdock(params) //doesn't actually fire curl command with mock Jenkins, therefore response parsing is not tested with this unit test
      then: "targets the correct flow and uses the right message"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X POST')
        cmd.contains('Content-Type: application/json')
        cmd.contains('Authorization: Basic ' + encodedToken)
        cmd.contains('X-flowdock-wait-for-message:')
        cmd.contains('-d')
        cmd.contains('{"event":"message","content":"' + message + '"}')
        cmd.contains('https://api.flowdock.com/flows/uhg/' + flow + '/messages')
    }

    def "can call flowdock with source token"(){
      given:
        def message = 'hello world!'
        def flow = 'jenkins-pipeline-as-code'
        def credentialsId = 'FLOWDOCK_API_SOURCE'
        def credentialsType = 'source'
        def token = 'hunter2'
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_SOURCE: token]
        )
        def params = [
                flow: flow,
                message: message,
                credentialsId: credentialsId,
                credentialsType: credentialsType
        ]
      when: "flowdock is signaled"
        def flowdock = new Flowdock(jenkins)
        flowdock.signalFlowdock(params) //doesn't actually fire curl command with mock Jenkins, therefore response parsing is not tested with this unit test
      then: "targets the correct flow and uses the right message"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X POST')
        cmd.contains('Content-Type: application/json')
        cmd.contains('X-flowdock-wait-for-message:')
        cmd.contains('-d')
        cmd.contains('{"event":"message","content":"' + message + '","flow_token":"' + token + '"}')
        cmd.contains('https://api.flowdock.com/flows/uhg/' + flow + '/messages')
    }

    def "can call flowdock with oauth token"(){
      given:
        def message = 'hello world!'
        def flow = 'jenkins-pipeline-as-code'
        def credentialsId = 'FLOWDOCK_API_OAUTH'
        def credentialsType = 'oauth'
        def token = 'hunter2'
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_OAUTH: token]
        )
        def params = [
                flow: flow,
                message: message,
                credentialsId: credentialsId,
                credentialsType: credentialsType
        ]
      when: "flowdock is signaled"
        def flowdock = new Flowdock(jenkins)
        flowdock.signalFlowdock(params) //doesn't actually fire curl command with mock Jenkins, therefore response parsing is not tested with this unit test
      then: "targets the correct flow and uses the right message"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X POST')
        cmd.contains('Content-Type: application/json')
        cmd.contains('Authorization: Bearer ' + token)
        cmd.contains('X-flowdock-wait-for-message:')
        cmd.contains('-d')
        cmd.contains('{"event":"message","content":"' + message + '"}')
        cmd.contains('https://api.flowdock.com/flows/uhg/' + flow + '/messages')
    }

    def "can call flowdock with thread id"(){
      given:
        def message = 'hello world!'
        def flow = 'jenkins-pipeline-as-code'
        def threadId = 'thread_id_gibberish_stuff'
        def credentialsId = 'FLOWDOCK_API_PERSONAL'
        def token = 'hunter2'
        def encodedToken = (token + ':\n').bytes.encodeBase64().toString()
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_PERSONAL: token]
        )
        def params = [
                flow: flow,
                threadId: threadId,
                message: message,
                credentialsId: credentialsId
        ]
      when: "flowdock is signaled"
        def flowdock = new Flowdock(jenkins)
        flowdock.signalFlowdock(params) //doesn't actually fire curl command with mock Jenkins, therefore response parsing is not tested with this unit test
      then: "targets the correct flow and uses the right message"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X POST')
        cmd.contains('Content-Type: application/json')
        cmd.contains('Authorization: Basic ' + encodedToken)
        cmd.contains('X-flowdock-wait-for-message:')
        cmd.contains('-d')
        cmd.contains('{"event":"message","content":"' + message + '","thread_id":"' + threadId + '"}')
        cmd.contains('https://api.flowdock.com/flows/uhg/' + flow + '/messages')
    }

    def "can call flowdock and retrieve user id"() {
      given:
        def flow = 'jenkins-pipeline-as-code'
        def credentialsId = 'FLOWDOCK_API_PERSONAL'
        def token = 'hunter2'
        def encodedToken = (token + ':\n').bytes.encodeBase64().toString()
        def email = 'personofinterest@optum.com'
        def expectedId = 111
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_PERSONAL: token]
        )
        def params = [
          flow: flow,
          credentialsId: credentialsId,
          email: email
        ]
        String mockRes = '[{"id":' + expectedId + ',"nick":"Flowy","email":"' + email + '","name":"Flow Nerd"},{"id":354136,"nick":"User2","email":"person2@optum.com","name":"Another Person"}]'
        jenkins.put('curl', mockRes)
      when: "flowdock request is made"
        def flowdock = new Flowdock(jenkins)
        def id = flowdock.getUserId(params)
      then: "executes the correct curl commnand and returns correct user id"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X GET')
        cmd.contains('Accept: application/json')
        cmd.contains('https://api.flowdock.com/flows/uhg/' + flow + '/users')
        id.equals(expectedId)
    }

    def "can call flowdock with user id and send user private message"() {
      given:
        def message = 'hello world!'
        def id = 111
        def credentialsId = 'FLOWDOCK_API_PERSONAL'
        def token = 'hunter2'
        def encodedToken = (token + ':\n').bytes.encodeBase64().toString()
        def jenkins = new JenkinsMock(
          env     : [ FLOWDOCK_API_PERSONAL: token]
        )
        def params = [
          credentialsId: credentialsId,
          userId: id,
          message: message
        ]
      when: "flowdock request is made"
        def flowdock = new Flowdock(jenkins)
        flowdock.signalUser(params)
      then: "uses the correct auth and targets the correct user with the right message"
        noExceptionThrown()
        String cmd = jenkins.calledJenkinsCommand
        cmd.contains('curl -s -X POST')
        cmd.contains('Content-Type: application/json')
        cmd.contains('Authorization: Basic ' + encodedToken)
        cmd.contains('-d')
        cmd.contains('{"event":"message","content":"' + message + '"}')
        cmd.contains('https://api.flowdock.com/private/' + id + '/messages')
    }
}
