package com.optum.jenkins.pipeline.library.angular

import com.optum.jenkins.pipeline.library.event.BuildEvent
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import spock.lang.Specification

class AngularCliSpec extends Specification {

  def "buildAngularAppWithCli environment command correct default ng version"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I build angular app"
    def angular = new AngularCli(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.buildAngularAppWithCli(config)

    then: "The command is structured correctly"
    calledJenkinsCommand.contains('--environment=tst')
    !calledJenkinsCommand.contains('--configuration')
  }

  def "buildAngularAppWithCli environment command correct ng version = 6.x"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I build angular app"
    def angular = new AngularCli(jenkins)
    def config = [
      buildForEnvironment: 'tst',
      angularCliVersion:   '6.1.1'
    ]
    angular.buildAngularAppWithCli(config)

    then: "The command is structured correctly"
    calledJenkinsCommand.contains('--configuration=tst')
    !calledJenkinsCommand.contains('--environment')
  }


  def "buildAngularAppWithCli environment command correct ng version > 6.x"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I build angular app"
    def angular = new AngularCli(jenkins)
    def config = [
      buildForEnvironment: 'tst',
      angularCliVersion:   '16.1.1'
    ]
    angular.buildAngularAppWithCli(config)

    then: "The command is structured correctly"
    calledJenkinsCommand.contains('--configuration=tst')
    !calledJenkinsCommand.contains('--environment')
  }

  def "buildAngularApp command structured correctly cli < 6"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I build angular app"
    def angular = new AngularCli(jenkins)
    def config = [
      buildForEnvironment: 'tst',
      angularCliVersion:   '1.7.0'
    ]
    angular.buildAngularAppWithCli(config)

    then: "The command is structured correctly"
    calledJenkinsCommand.contains('--environment=tst')
    !calledJenkinsCommand.contains('--configuration')
  }

  def "buildAngularApp event no error success"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      echo: {},
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: "I build angular app"
    def angular = new AngularCli(jenkins)
    angular.sendBuildEvent(new Date())
    then: "No exception is thrown"
    noExceptionThrown()
  }

  def "testAngularApp with default config"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: "I test angular app"
    def angular = new AngularCli(jenkins)

    angular.testAngularAppWithCli([:])
    then: "No exception is thrown"
    calledJenkinsCommand.contains('--single-run')
    calledJenkinsCommand.contains('--code-coverage')
  }

  def "testAngularApp with Angular CLI 6+ config"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: "I test angular app"
    def angular = new AngularCli(jenkins)

    def config = [
      angularCliVersion:   '6.1.1'
    ]

    angular.testAngularAppWithCli(config)
    then: "No exception is thrown"
    !calledJenkinsCommand.contains('--single-run')
    calledJenkinsCommand.contains('--code-coverage')
  }

  def "testAngularApp with Angular CLI 6+ and no code coverage"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: "I test angular app"
    def angular = new AngularCli(jenkins)

    def config = [
      angularCliVersion:   '6.1.1',
      generateCodeCoverage: false
    ]

    angular.testAngularAppWithCli(config)
    then: "No exception is thrown"
    !calledJenkinsCommand.contains('--single-run')
    !calledJenkinsCommand.contains('--code-coverage')
  }

  def "testAngularApp with Angular CLI < 6 and code coverage"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { ArrayList a, Closure c -> c.call() },
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: "I test angular app"
    def angular = new AngularCli(jenkins)

    def config = [
      angularCliVersion:   '1.7.4',
      generateCodeCoverage: true
    ]

    angular.testAngularAppWithCli(config)
    then: "No exception is thrown"
    calledJenkinsCommand.contains('--single-run')
    calledJenkinsCommand.contains('--code-coverage')
  }

}
