package com.optum.jenkins.pipeline.library.dotnet

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class DotNetCoreSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def dotnetcore = new DotNetCore(jenkins)
    then: "Jenkins context is available"
    dotnetcore.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def dotnetcore = new DotNetCore()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "restoreDotNetCoreDependencies dotnet command is structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedCmd = "dotnet restore projectFileA  --runtime targetRuntimeA"
    when: "I restore dotnet core dependencies"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.restoreDotNetCoreDependencies(config)
    then: "The dotnet command is structured correctly"
    calledJenkinsCommand.toString() == expectedCmd
  }

  def "buildDotNetCoreApp command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '1', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'dotnet build projectFileA  --runtime targetRuntimeA --configuration Release'
    when: "I build dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.buildDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerInnerObj = withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerInnerObj.call() == expectedCmd
  }

  def "buildDotNetCoreApp DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT set"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = "[DOTNET_VERSION=2.1.0, DOTNET_CLI_TELEMETRY_OPTOUT='1']"
    when: "I build dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.buildDotNetCoreApp(config)
    then: "DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT are set"
    withEnvMap.toString() == expectedMap
  }

  def "buildDotNetCoreApp DOTNET_HOME structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[DOTNET_HOME=toolsdir/2.1.0]"
    when: "I build dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.buildDotNetCoreApp(config)
    withEnvClosure.call()
    then: "The DOTNET_HOME is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "buildDotNetCoreApp PATH structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[PATH=dotnethome/bin:path]"
    when: "I build dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.buildDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The PATH is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "buildDotNetCoreApp error if project file does not exist"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> false},
      withEnv : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I build dotnet core app with no file in the workspace when a filename has been given"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.buildDotNetCoreApp(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('file must be present in the workspace')
  }

  def "testDotNetCoreApp command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '1', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'dotnet test   --configuration Release'
    when: "I test dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.testDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerInnerObj = withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerInnerObj.call() == expectedCmd
  }

  def "testDotNetCoreApp DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT set"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = "[DOTNET_VERSION=2.1.0, DOTNET_CLI_TELEMETRY_OPTOUT='1']"
    when: "I test dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.testDotNetCoreApp(config)
    then: "DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT are set"
    withEnvMap.toString() == expectedMap
  }

  def "testDotNetCoreApp DOTNET_HOME structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[DOTNET_HOME=toolsdir/2.1.0]"
    when: "I test dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.testDotNetCoreApp(config)
    withEnvClosure.call()
    then: "The DOTNET_HOME is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "testDotNetCoreApp PATH structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[PATH=dotnethome/bin:path]"
    when: "I test dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.testDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The PATH is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "testDotNetCoreApp error if project file does not exist"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> false},
      withEnv : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I test dotnet core app with no file in the workspace when a filename has been given"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.testDotNetCoreApp(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('file must be present in the workspace')
  }

  def "publishDotNetCoreApp command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '1', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedCmd = 'dotnet publish projectFileA   -r targetRuntimeA -c Release '
    when: "I publish dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.publishDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerInnerObj = withEnvClosureInnerObj.call()
    then: "The command is structured correctly"
    withEnvClosureInnerInnerObj.call() == expectedCmd
  }

  def "publishDotNetCoreApp DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT set"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = "[DOTNET_VERSION=2.1.0, DOTNET_CLI_TELEMETRY_OPTOUT='1']"
    when: "I publish dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.publishDotNetCoreApp(config)
    then: "DOTNET_VERSION, DOTNET_CLI_TELEMETRY_OPTOUT are set"
    withEnvMap.toString() == expectedMap
  }

  def "publishDotNetCoreApp DOTNET_HOME structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[DOTNET_HOME=toolsdir/2.1.0]"
    when: "I publish dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.publishDotNetCoreApp(config)
    withEnvClosure.call()
    then: "The DOTNET_HOME is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "publishDotNetCoreApp PATH structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvMap
    def jenkins = [
      env     : [DOTNET_TOOLS_DIR: 'toolsdir', DOTNET_VERSION: '2.1.0', DOTNET_HOME: 'dotnethome', PATH: 'path'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      fileExists: {String projectFilename -> true},
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a; withEnvClosure = c }
    ]
    def expectedMap = "[PATH=dotnethome/bin:path]"
    when: "I publish dotnet core app"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.publishDotNetCoreApp(config)
    withEnvClosureInnerObj = withEnvClosure.call()
    withEnvClosureInnerObj.call()
    then: "The PATH is structured correctly"
    withEnvMap.toString() == expectedMap
  }

  def "publishDotNetCoreApp error if project file does not exist"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      fileExists: {String projectFilename -> false},
      withEnv : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I publish dotnet core app with no file in the workspace when a filename has been given"
    def dotnetcore = new DotNetCore(jenkins)
    def config = [
      projectFile: 'projectFileA',
      targetRuntime: 'targetRuntimeA'
    ]
    dotnetcore.publishDotNetCoreApp(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('file must be present in the workspace')
  }
}
