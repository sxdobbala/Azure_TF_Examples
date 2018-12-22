package com.optum.jenkins.pipeline.library.maven

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.event.ArtifactStoreEvent
import com.optum.jenkins.pipeline.library.event.BuildEvent
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.Constants
import spock.lang.Specification
import spock.lang.Unroll

class MavenBuildSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def mavenBuild = new MavenBuild(jenkins)
    then: "Jenkins context is available"
    mavenBuild.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def mavenBuild = new MavenBuild()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  @Unroll
  def "buildWithMaven command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : {},
      step        : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> calledJenkinsCommand = cmd },
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I run buildWithMaven"
    def maven = new MavenBuild(jenkins)
    maven.buildWithMaven(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName                           | config                                       | expectedCmd
    'default'                          | [:]                                          | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -B '
    'mavenGoals'                       | [mavenGoals: '-U -e clean']                  | 'mvn -U -e clean'
    'pomFile'                          | [pomFile: 'pomSpecial.xml']                  | 'mvn -U -e clean -f ' + config.pomFile + ' org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -B '
    'JaCoCo, ignoreTestFailures false' | [ignoreTestFailures: false]                  | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -B '
    'No JaCoCo, unit test'             | [runJacocoCoverage: false, skipTests: false] | 'mvn -U -e clean install -Dmaven.test.failure.ignore=true -B '
    'No JaCoCo, no unit test'          | [runJacocoCoverage: false, skipTests: true]  | 'mvn -U -e clean install -Dmaven.test.skip=true -B '
    'isDebugMode true'                      | [isDebugMode: true]                          | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -X -B '
    'isBatchMode false'                      | [isBatchMode: false]                         | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true '
    'additionalProps'                  | [additionalProps: ['ci.env': '']]            | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -B -Dci.env="" '
    'mavenProfiles'                    | [mavenProfiles: 'profile1']                  | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -B -Pprofile1 '
    'settingsXml'                      | [settingsXml: 'settings.xml']                | 'mvn -U -e clean org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':prepare-agent install org.jacoco:jacoco-maven-plugin:' + Constants.JACOCO_MAVEN_PLUGIN_VERSION + ':report -Dmaven.test.failure.ignore=true -B -s settings.xml '
  }

  def "buildWithMaven JAVA_VERSION, MAVEN_VERSION, MAVEN_OPTS set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : {},
      step        : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> 'nop'},
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I run buildWithMaven"
    def maven = new MavenBuild(jenkins)
    def config = [mavenOpts:"-Xmx1024m"]
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION + ', MAVEN_VERSION=' +Constants.MAVEN_VERSION + ', MAVEN_OPTS=' +config.mavenOpts +']'
    maven.buildWithMaven(config)
    then: "JAVA_VERSION, MAVEN_VERSION, MAVEN_OPTS are set"
    withEnvMap.toString() == expectedMap
  }

  def "buildWithMaven junit configured correctly"() {
    given:
    LinkedHashMap junitMap
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : { java.util.LinkedHashMap l -> junitMap = l},
      step        : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> 'nop'},
      withEnv     : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I run buildWithMaven"
    def maven = new MavenBuild(jenkins)
    def config = [:]
    maven.buildWithMaven(config)
    then: "junit is configured correctly"
    junitMap.toString() == '[allowEmptyResults:true, testResults:**/target/surefire-reports/*.xml]'
  }

  def "buildWithMaven exception if issue with uploadJacocoResults"() {
    given:
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> 'nop' },
      withEnv     : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    def buildEvent = GroovySpy(BuildEvent, global: true, useObjenesis: true)
    buildEvent.send() >> 'nop'
    when: "I run buildWithMaven without jenkins.step to cause an exception"
    def maven = new MavenBuild(jenkins)
    def config = [:]
    maven.buildWithMaven(config)
    then: "An exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('You either need to set \'uploadJacocoResults = false\' in your config that you are passing in or you need to install the JaCoCo plugin into your Jenkins Instance')
  }

  @Unroll
  def "deployToArtifactory command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withCredentialsClosure
    def withCredentialsClosureObj
    def jenkins = [
      echo            : {},
      env             : [],
      error           : { msg -> throw new JenkinsErrorException(msg) },
      junit           : {},
      step            : {},
      currentBuild    : [currentResult: 'SUCCESS'],
      command         : { String cmd -> calledJenkinsCommand = cmd },
      withEnv         : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    def artifactStoreEvent = GroovySpy(ArtifactStoreEvent, global: true, useObjenesis: true)
    artifactStoreEvent.send() >> 'nop'
    when: "I run deployToArtifactory"
    def maven = new MavenBuild(jenkins)
    maven.deployToArtifactory(config)
    withCredentialsClosureObj = withEnvClosure.call()
    withCredentialsClosureObj.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName            | config                              | expectedCmd
    'default'           | [:]                                 | 'mvn deploy -B -f pom.xml -e -Dmaven.test.skip=true -Dci.env= '
    'isDebugMode true'  | [isDebugMode: true]                 | 'mvn deploy -X -B -f pom.xml -e -Dmaven.test.skip=true -Dci.env= '
    'isBatchMode false' | [isBatchMode: false]                | 'mvn deploy -f pom.xml -e -Dmaven.test.skip=true -Dci.env= '
    'deployAtEnd'       | [deployAtEnd: true]                 | 'mvn deploy -B -f pom.xml -e -Dmaven.test.skip=true -DdeployAtEnd=true -Dci.env= '
    'additionalProps'   | [additionalProps: ['ci.env': '']]   | 'mvn deploy -B -f pom.xml -e -Dmaven.test.skip=true -Dci.env="" '
    'mavenProfiles'     | [mavenProfiles: 'profile1']         | 'mvn deploy -B -f pom.xml -e -Dmaven.test.skip=true -Dci.env= -Pprofile1 '
    'settingsXml'       | [settingsXml: 'settings.xml']       | 'mvn deploy -B -f pom.xml -e -Dmaven.test.skip=true -Dci.env= -s settings.xml '
  }

  def "deployToArtifactory JAVA_VERSION, MAVEN_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : {},
      step        : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> 'nop'},
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def artifactStoreEvent = GroovySpy(ArtifactStoreEvent, global: true, useObjenesis: true)
    artifactStoreEvent.send() >> 'nop'
    when: "I run deployToArtifactory"
    def maven = new MavenBuild(jenkins)
    def config = [:]
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION + ', MAVEN_VERSION=' +Constants.MAVEN_VERSION + ']'
    maven.deployToArtifactory(config)
    then: "JAVA_VERSION, MAVEN_VERSION are set"
    withEnvMap.toString() == expectedMap
  }

  def "deployToArtifactory credentials set"() {
    given:
    def withEnvClosure
    def withCredentialsMap
    def jenkins = [
      echo        : {},
      error       : { msg -> throw new JenkinsErrorException(msg) },
      junit       : {},
      step        : {},
      currentBuild: [currentResult: 'SUCCESS'],
      command     : { String cmd -> 'nop'},
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a }
    ]
    def expectedMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:MAVEN_USER, passwordVariable:MAVEN_PASS]]"
    def artifactStoreEvent = GroovySpy(ArtifactStoreEvent, global: true, useObjenesis: true)
    artifactStoreEvent.send() >> 'nop'
    when: "I run deployToArtifactory"
    def maven = new MavenBuild(jenkins)
    def config = [artifactoryUserCredentialsId: 'credentials']
    maven.deployToArtifactory(config)
    withEnvClosure.call()
    then: "credentials are set"
    withCredentialsMap.toString() == expectedMap
  }

  def "sendBuildEvent, with jenkins build status, no error"() {
    given:
    def jenkins =[
      echo : {},
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendBuildEvent is called with jenkins build status'
    def maven = new MavenBuild(jenkins)
    maven.sendBuildEvent(jenkins, new Date(), 'maven')
    then: 'No exception is thrown'
    noExceptionThrown()
  }

  def "sendBuildEvent no error"() {
    given:
    def jenkins =[
      echo : {}
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendBuildEvent is called'
    def maven = new MavenBuild(jenkins)
    maven.sendBuildEvent(jenkins, new Date(), 'maven', EventStatus.SUCCESS )
    then: 'No exception is thrown'
    noExceptionThrown()
  }

  def "sendArtifactStoreEvent, with jenkins build status, no error"() {
    given:
    def jenkins =[
      echo : {},
      currentBuild: [currentResult: 'SUCCESS']
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendArtifactStoreEvent is called with jenkins build status'
    def maven = new MavenBuild(jenkins)
    maven.sendArtifactStoreEvent(jenkins, new Date())
    then: 'No exception is thrown'
    noExceptionThrown()
  }

  def "sendArtifactStoreEvent no error"() {
    given:
    def jenkins =[
      echo : {}
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendArtifactStoreEvent is called'
    def maven = new MavenBuild(jenkins)
    maven.sendArtifactStoreEvent(jenkins, new Date(), EventStatus.SUCCESS)
    then: 'No exception is thrown'
    noExceptionThrown()
  }
}
