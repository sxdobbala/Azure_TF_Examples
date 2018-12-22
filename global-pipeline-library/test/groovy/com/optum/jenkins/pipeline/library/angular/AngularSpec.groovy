package com.optum.jenkins.pipeline.library.angular

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class AngularSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = [echo : 'hello']
    when: 'Creating class with jenkins context'
    def angular = new Angular(jenkins)
    then: "Jenkins context is available"
    angular.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def angular = new Angular()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "buildAngularApp command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'node_modules/@angular/cli/bin/ng build --environment=tst '
    when: "I build angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.buildAngularApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerInnerObj = withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerInnerObj.call() == expectedCmd
  }

  def "buildAngularApp NODEJS_VERSION, NPM_AUTH_KEY, NPM_EMAIL set"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = "[NODEJS_VERSION=7.9.0, NPM_AUTH_KEY=npmAuthKey, NPM_EMAIL=noreply@optum.com]"
    when: "I build angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.buildAngularApp(config)
    then: "NODEJS_VERSION, NPM_AUTH_KEY, NPM_EMAIL are set"
    withEnvMap.toString() == expectedMap
  }

  def "buildAngularApp NODEJS_HOME structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvMap
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '7.9.0'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[NODEJS_HOME=toolsdir/node-v7.9.0-linux-x64]"
    when: "I build angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.buildAngularApp(config)
    withEnvClosure.call()
    then: "The NODEJS_HOME is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "buildAngularApp PATH structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvMap
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[PATH=nodejshome/bin:path]"
    when: "I build angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.buildAngularApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The PATH is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "buildAngularApp error for empty build environment"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env  : [],
      echo : {},
      error: { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I try to build an angular app with an empty build environment"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: ''
    ]
    angular.buildAngularApp(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('The buildForEnvironment is required')
  }

  def "testAngularApp command structured correctly with code coverage"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'node_modules/@angular/cli/bin/ng test  --single-run --code-coverage'
    when: "I test angular app with code coverage"
    def angular = new Angular(jenkins)
    def config = [
      generateCodeCoverage: true
    ]
    angular.testAngularApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerInnerObj = withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerInnerObj.call() == expectedCmd
  }

  def "testAngularApp command structured correctly without code coverage"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureOuterObj
    def withEnvClosureInnerObj
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'node_modules/@angular/cli/bin/ng test  --single-run '
    when: "I test angular app without code coverage"
    def angular = new Angular(jenkins)
    def config = [
      generateCodeCoverage: false
    ]
    angular.testAngularApp(config)
    withEnvClosureOuterObj = withEnvClosure.call()
    withEnvClosureInnerObj = withEnvClosureOuterObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerObj.call() == expectedCmd
  }

  def "testAngularApp NODEJS_VERSION, NPM_AUTH_KEY, NPM_EMAIL set"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = "[NODEJS_VERSION=7.9.0, NPM_AUTH_KEY=npmAuthKey, NPM_EMAIL=noreply@optum.com]"
    when: "I test angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.testAngularApp(config)
    then: "NODEJS_VERSION, NPM_AUTH_KEY, NPM_EMAIL are set"
    withEnvMap.toString() == expectedMap
  }

  def "testAngularApp NODEJS_HOME structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvMap
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '7.9.0'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[NODEJS_HOME=toolsdir/node-v7.9.0-linux-x64]"
    when: "I test angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.testAngularApp(config)
    withEnvClosure.call()
    then: "The NODEJS_HOME is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "testAngularApp PATH structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvMap
    def jenkins = [
      env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[PATH=nodejshome/bin:path]"
    when: "I test angular app"
    def angular = new Angular(jenkins)
    def config = [
      buildForEnvironment: 'tst'
    ]
    angular.testAngularApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The PATH is structured correctly"
    withEnvMap.toString() == expectedMap
  }


  def "setupAngularBuildEnvironment command is structured correctly when useCache is true"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedCmd = 'npm-cache install'
    when: "I set up angular build environment"
    def angular = new Angular(jenkins)
    def useCache = true
    angular.setupAngularBuildEnvironment(useCache)
    then: "The command is structured correctly when useCache is true"
    calledJenkinsCommand.toString() == expectedCmd
  }

  def "setupAngularBuildEnvironment command is structured correctly when useCache is false"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedCmd = 'npm install'
    when: "I set up angular build environment"
    def angular = new Angular(jenkins)
    def useCache = false
    angular.setupAngularBuildEnvironment(useCache)
    then: "The command is structured correctly when useCache is false"
    calledJenkinsCommand.toString() == expectedCmd
  }
}
