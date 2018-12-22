package com.optum.jenkins.pipeline.library.sonar

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.event.SonarEvent
import com.optum.jenkins.pipeline.library.scm.Git
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import org.apache.maven.model.Model
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SonarSpec extends Specification {

  @Shared
  String partialSonarCmd = '-Dsonar.projectVersion=10 -Dsonar.links.scm=https://github.optum.com/someorg/somerepo.git' +
    ' -Dsonar.links.ci=https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library' +
    ' -Dsonar.projectKey=someProjectKey -Dsonar.projectName=someProjectName -Dsonar.branch.name=somebranch' +
    ' -Dsonar.host.url=' + Constants.SONAR_HOST_URL + ' -Dsonar.login=' + Constants.SONAR_LOGIN + ' '

  @Shared
  String partialMavenCmd = 'mvn -e org.sonarsource.scanner.maven:sonar-maven-plugin:' + Constants.SONAR_MAVEN_PLUGIN_VERSION +
    ':sonar ' +partialSonarCmd

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def sonar = new Sonar(jenkins)
    then: "Jenkins context is available"
    sonar.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def sonar = new Sonar()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  @Unroll
  def "scanWithMaven command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo        : {},
      env         : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild: [currentResult: 'SUCCESS', displayName: '10'],
      command     : { String cmd -> calledJenkinsCommand = cmd },
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom: { return new Model() }
    ]
    stubUtility(jenkins)
    when: "I run scanWithMaven"
    def sonar = new Sonar(jenkins)
    sonar.scanWithMaven(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName        | config                        | expectedCmd
    'default'       | [:]                           | partialMavenCmd + '-B '
    'pomFile'       | [pomFile: 'pomSpecial.xml']   | partialMavenCmd + '-B -f pomSpecial.xml '
    'isDebugMode'   | [isDebugMode: true]           | partialMavenCmd + '-X -B '
    'isBatchMode'   | [isBatchMode: false]          | partialMavenCmd
    'mavenProfiles' | [mavenProfiles: 'profile1']   | partialMavenCmd + '-B -Pprofile1 '
    'settingsXml'   | [settingsXml: 'settings.xml'] | partialMavenCmd + '-B -s settings.xml '
  }

  @Unroll
  def "scanWithMaven command contains correct values '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo        : {},
      env         : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild: [currentResult: 'SUCCESS', displayName: '10'],
      command     : { String cmd -> calledJenkinsCommand = cmd },
      withEnv     : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom: { return new Model() }
    ]
    stubUtility(jenkins)
    when: "I run scanWithMaven"
    def sonar = new Sonar(jenkins)
    sonar.scanWithMaven(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand.contains(expectedCmd)
    where:
    testName                  | config                                | expectedCmd
    'sonarProjectVersion'     | [sonarProjectVersion: 1]              | '-Dsonar.projectVersion=1'
    'sonarExclusions'         | [sonarExclusions: 'dir/**/*']         | '-Dsonar.exclusions=dir/**/*'
    'sonarCoverageExclusions' | [sonarCoverageExclusions: 'dir/**/*'] | '-Dsonar.coverage.exclusions=dir/**/*'
    'additionalProps'         | [additionalProps: ['ci.env': '']]     | '-Dci.env=""'
  }

  def "scanWithMaven command correct for PR from branch"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withCredentialsClosure
    def withCredentialsClosureObj
    def calledJenkinsEcho
    def jenkins = [
      echo           : { def printout -> calledJenkinsEcho = printout},
      env            : [
        JOB_NAME      : 'job/PR-jobname',
        JOB_URL       : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library',
        CHANGE_TARGET : 'changeTarget',
        githubOauth   : 'githubOauthKey'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      withCredentials: { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    stubUtility(jenkins)
    when: "I run scanWithMaven on a PR from branch"
    def sonar = new Sonar(jenkins)
    def config = [gitUserCredentialsId:'credId']
    sonar.scanWithMaven(config)
    withCredentialsClosure.call()
    then: "The command is structured correctly"
    calledJenkinsEcho.contains('-Dsonar.analysis.mode=preview -Dsonar.branch.target=changeTarget')
    calledJenkinsEcho.contains('-Dsonar.github.pullRequest=jobname -Dsonar.github.repository=someorg/somerepo -Dsonar.github.endpoint=https://github.optum.com/api/v3')

  }

  def "scanWithMaven command does not contain branch when on master"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withCredentialsClosure
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'job/jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      withCredentials: { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    Git gitStub = Stub()
    GroovyStub(Git, global: true)
    new Git(jenkins) >> gitStub
    gitStub.getBranch() >> 'master'
    gitStub.getRemoteUrl() >> "https://github.optum.com/someorg/somerepo.git"
    GroovyStub(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'someProjectKey'
    OptumFileReader.getProjectFriendlyName(_) >> 'someProjectName'
    def sonarEvent = GroovySpy(SonarEvent, global: true, useObjenesis: true)
    sonarEvent.send() >> 'nop'
    when: "I run scanWithMaven against master"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithMaven(config)
    withEnvClosure.call()
    then: "The command is structured without branch"
    !calledJenkinsCommand.contains('-Dsonar.branch.name=')
  }

  def "scanWithMaven command does not contain branch when on a specific main branch"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withCredentialsClosure
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'job/jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      withCredentials: { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    Git gitStub = Stub()
    GroovyStub(Git, global: true)
    new Git(jenkins) >> gitStub
    gitStub.getBranch() >> 'myMain'
    gitStub.getRemoteUrl() >> "https://github.optum.com/someorg/somerepo.git"
    GroovyStub(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'someProjectKey'
    OptumFileReader.getProjectFriendlyName(_) >> 'someProjectName'
    def sonarEvent = GroovySpy(SonarEvent, global: true, useObjenesis: true)
    sonarEvent.send() >> 'nop'
    when: "I run scanWithMaven against master"
    def sonar = new Sonar(jenkins)
    def config = [mainBranchName:'myMain']
    sonar.scanWithMaven(config)
    withEnvClosure.call()
    then: "The command is structured without branch"
    !calledJenkinsCommand.contains('-Dsonar.branch.name=')
  }

  def "scanWithMaven command is structured correctly when using credentials"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withCredentialsClosure
    def withCredentialsClosureObj
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      withCredentials: { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    stubUtility(jenkins)
    when: "I run scanWithMaven with credentials"
    def sonar = new Sonar(jenkins)
    def config = [gitUserCredentialsId:'credId']
    sonar.scanWithMaven(config)
    withCredentialsClosureObj = withEnvClosure.call()
    withCredentialsClosureObj.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == partialMavenCmd + '-B '
  }

  def "scanWithMaven JAVA_VERSION, MAVEN_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> 'nop' },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvMap = a },
      readMavenPom   : { return new Model() }
    ]
    stubUtility(jenkins)
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION + ', MAVEN_VERSION=' +Constants.MAVEN_VERSION +']'
    when: "I run scanWithMaven"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithMaven(config)
    then: "JAVA_VERSION, MAVEN_VERSION are set"
    withEnvMap.toString() == expectedMap
  }

  def "scanWithGradle command is structured correctly when isUnix"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      isUnix         : { return true }
    ]
    stubUtility(jenkins)
    when: "I run scanWithGradle when isUnix is true"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithGradle(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == './gradlew sonarqube ' + partialSonarCmd
  }

  def "scanWithGradle command is structured correctly when not isUnix"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withEnvClosureInnerObj
    def withEnvClosureInnerInnerObj
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> calledJenkinsCommand = cmd },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom   : { return new Model() },
      string         : {},
      isUnix         : { return false }
    ]
    stubUtility(jenkins)
    when: "I run scanWithGradle when isUnix is false"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithGradle(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == 'gradlew.bat sonarqube ' + partialSonarCmd
  }

  def "scanWithGradle JAVA_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> 'nop' },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvMap = a },
      readMavenPom   : { return new Model() }
    ]
    stubUtility(jenkins)
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION_FOR_SONAR + ']'
    when: "I run scanWithGradle"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithGradle(config)
    then: "JAVA_VERSION is set"
    withEnvMap.toString() == expectedMap
  }

  def "scanWithSonarScanner command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withSonarQubeEnvClosure
    def withSonarQubeEnvClosureObj
    def jenkins = [
      echo             : {},
      env              : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error            : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild     : [currentResult: 'SUCCESS', displayName: '10'],
      command          : { String cmd -> calledJenkinsCommand = cmd },
      withEnv          : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom     : { return new Model() },
      string           : {},
      withSonarQubeEnv : { String s, Closure c -> withSonarQubeEnvClosure = c}
    ]
    stubUtility(jenkins)
    def expectedCmd = 'sonar-scanner -Dsonar.projectVersion=10 -Dsonar.links.scm=https://github.optum.com/someorg/somerepo.git -Dsonar.links.ci=https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library -Dsonar.projectKey=someProjectKey -Dsonar.projectName=someProjectName -Dsonar.branch.name=somebranch -Dsonar.sources="sources" '
    when: "I run scanWithSonarScanner"
    def sonar = new Sonar(jenkins)
    def config = [sources:'sources']
    sonar.scanWithSonarScanner(config)
    withSonarQubeEnvClosureObj = withEnvClosure.call()
    withSonarQubeEnvClosureObj.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
  }

  def "scanWithSonarScanner error for missing sources parameter"() {
    given:
    def jenkins = [
      echo             : {},
      env              : [:],
      error            : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild     : [:],
      command          : { String cmd -> 'nop' },
      withEnv          : { java.util.ArrayList a, Closure c -> 'nop' },
      readMavenPom     : {},
      string           : {},
      withSonarQubeEnv : { String s, Closure c -> 'nop'}
    ]
    when: "I run scanWithSonarScanner without sources parameter"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithSonarScanner(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('Sources property is required')
  }

  def "scanWithSonarScanner JAVA_VERSION and SONAR_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> 'nop' },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvMap = a },
      readMavenPom   : { return new Model() }
    ]
    stubUtility(jenkins)
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION_FOR_SONAR + ', SONAR_VERSION=' + Constants.SONAR_TOOL_VERSION + ']'
    when: "I run scanWithSonarScanner"
    def sonar = new Sonar(jenkins)
    def config = [sources:'sources']
    sonar.scanWithSonarScanner(config)
    then: "JAVA_VERSION is set"
    withEnvMap.toString() == expectedMap
  }

  def "scanWithNpm command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def jenkins = [
      echo             : {},
      env              : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error            : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild     : [currentResult: 'SUCCESS', displayName: '10'],
      command          : { String cmd -> calledJenkinsCommand = cmd },
      readMavenPom     : { return new Model() },
      string           : {}
    ]
    stubUtility(jenkins)
    def expectedCmd = 'node_modules/sonarqube-scanner/dist/bin/sonar-scanner ' +partialSonarCmd
    when: "I run scanWithNpm"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanWithNpm(config)
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
  }

  @Unroll
  def "scanUsingSonarScannerWithPropertiesFile command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def withSonarQubeEnvClosure
    def withSonarQubeEnvClosureObj
    def jenkins = [
      echo             : {},
      env              : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error            : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild     : [currentResult: 'SUCCESS', displayName: '10'],
      command          : { String cmd -> calledJenkinsCommand = cmd },
      withEnv          : { java.util.ArrayList a, Closure c -> withEnvClosure = c },
      readMavenPom     : { return new Model() },
      string           : {},
      withSonarQubeEnv : { String s, Closure c -> withSonarQubeEnvClosure = c},
      fileExists       : { true }
    ]
    stubUtility(jenkins)
    when: "I run scanUsingSonarScannerWithPropertiesFile"
    def sonar = new Sonar(jenkins)
    sonar.scanUsingSonarScannerWithPropertiesFile(config)
    withSonarQubeEnvClosureObj = withEnvClosure.call()
    withSonarQubeEnvClosureObj.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName            | config                              | expectedCmd
    'default'           | [:]                                 | 'sonar-scanner  -Dsonar.projectKey=someProjectKey -Dsonar.projectName=someProjectName '
    'additionalProps'   | [additionalProps: ['ci.env': '']]   | 'sonar-scanner [ci.env:] -Dsonar.projectKey=someProjectKey -Dsonar.projectName=someProjectName '
  }

  def "scanUsingSonarScannerWithPropertiesFile error when file does not exist"() {
    given:
    def jenkins = [
      echo             : {},
      env              : [:],
      error            : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild     : [:],
      command          : { String cmd -> 'nop' },
      withEnv          : { java.util.ArrayList a, Closure c -> 'nop' },
      readMavenPom     : {},
      string           : {},
      withSonarQubeEnv : { String s, Closure c -> 'nop'},
      fileExists       : { false }
    ]
    when: "I run scanUsingSonarScannerWithPropertiesFile with missing file"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanUsingSonarScannerWithPropertiesFile(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains('sonar-project.properties file must be present in the workspace.')
  }

  def "scanUsingSonarScannerWithPropertiesFile JAVA_VERSION and SONAR_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo           : {},
      env            : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ],
      error          : { msg -> throw new JenkinsErrorException(msg) },
      currentBuild   : [currentResult: 'SUCCESS', displayName: '10'],
      command        : { String cmd -> 'nop' },
      withEnv        : { java.util.ArrayList a, Closure c -> withEnvMap = a },
      readMavenPom   : { return new Model() },
      fileExists     : { true }
    ]
    stubUtility(jenkins)
    def expectedMap = '[JAVA_VERSION=' +Constants.JAVA_VERSION_FOR_SONAR + ', SONAR_VERSION=' + Constants.SONAR_TOOL_VERSION + ']'
    when: "I run scanUsingSonarScannerWithPropertiesFile"
    def sonar = new Sonar(jenkins)
    def config = [:]
    sonar.scanUsingSonarScannerWithPropertiesFile(config)
    then: "JAVA_VERSION and SONAR_VERSION is set"
    withEnvMap.toString() == expectedMap
  }

  def "sendSonarEvent no error"() {
    given:
    def jenkins =[
      echo : {},
      env  : [
        JOB_NAME: 'jobname',
        JOB_URL : 'https://jenkins.optum.com/central/job/jpac/job/jenkins-pipelines/job/global-pipeline-library'
      ]
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendSonarEvent is called'
    def sonar = new Sonar(jenkins)
    sonar.sendSonarEvent(jenkins, new Date(), EventStatus.SUCCESS,'sonar' )
    then: 'No exception is thrown'
    noExceptionThrown()
  }

  def stubUtility(Map<String, Object> jenkins) {
    Git gitStub = Stub()
    GroovyStub(Git, global: true)
    new Git(jenkins) >> gitStub
    gitStub.getBranch() >> 'somebranch'
    gitStub.getRemoteUrl() >> "https://github.optum.com/someorg/somerepo.git"
    GroovyStub(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'someProjectKey'
    OptumFileReader.getProjectFriendlyName(_) >> 'someProjectName'
    def sonarEvent = GroovySpy(SonarEvent, global: true, useObjenesis: true)
    sonarEvent.send() >> 'nop'
  }
}
