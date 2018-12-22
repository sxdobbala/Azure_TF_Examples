#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.angular

import com.optum.jenkins.pipeline.library.event.BuildEvent
import com.optum.jenkins.pipeline.library.event.TestEvent
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.TestType
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.Utils

class AngularCli implements Serializable {

  def jenkins

  AngularCli() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  AngularCli(jenkins) {
    this.jenkins = jenkins
  }

  def buildAngularAppWithCli(Map<String, Object> params) {
    def defaults = [
      angularCliVersion   : Constants.ANGULAR_CLI_VERSION, //optional
      buildForEnvironment : 'dev', //required
      additionalNgOptions : "", //optional
      additionalNpmOptions: "", //optional
      useCache            : false //coming soon
    ]
    def config = defaults + params
    def startTime = new Date()

    jenkins.echo "buildAngularApp arguments: $config"

    if (!config.buildForEnvironment) {
      jenkins.error "The buildForEnvironment is required."
    }

    try {
      jenkins.withEnv(["ANGULARCLI_VERSION=${config.angularCliVersion}"]) {
        jenkins.withEnv(["PATH=PATH=/tools/angularcli/angularcli-$jenkins.env.ANGULARCLI_VERSION/node-v8.11.1-linux-x64/bin:$jenkins.env.PATH"]) {
          def installDependenciesCmd = (config.useCache ? "npm-cache install $config.additionalNpmOptions" : "npm install $config.additionalNpmOptions")
          jenkins.echo "Running $installDependenciesCmd"
          jenkins.command(installDependenciesCmd)

          def envArg = config.angularCliVersion.take(config.angularCliVersion.indexOf('.')).toInteger() >= 6 ? '--configuration' : '--environment'
          def appBuildCmd = "ng build $envArg=$config.buildForEnvironment $config.additionalNgOptions"
          jenkins.echo "Running $appBuildCmd"
          jenkins.command(appBuildCmd)
        }
      }
      sendBuildEvent(startTime)
    } catch (e) {
      sendBuildEvent(startTime, EventStatus.FAILURE)
      throw e
    }
  }

  def testAngularAppWithCli(Map<String, Object> params) {
    def defaults = [
      angularCliVersion   : Constants.ANGULAR_CLI_VERSION, //optional
      generateCodeCoverage: true
    ]
    def config = defaults + params
    def startTime = new Date()

    jenkins.echo "testAngularApp arguments: $config"

    try {
      jenkins.withEnv(["ANGULARCLI_VERSION=${config.angularCliVersion}"]) {
        jenkins.withEnv(["PATH=PATH=/tools/angularcli/angularcli-$jenkins.env.ANGULARCLI_VERSION/node-v8.11.1-linux-x64/bin:$jenkins.env.PATH"]) {
          def envArg = config.angularCliVersion.take(config.angularCliVersion.indexOf('.')).toInteger() >= 6 ? '' : '--single-run'
          def appTestCmd = "ng test $envArg " + (config.generateCodeCoverage ? "--code-coverage" : "")
          jenkins.echo "Running $appTestCmd"
          jenkins.command(appTestCmd)
        }
      }
      sendUnitTestEvent(startTime)
    } catch (e) {
      sendUnitTestEvent(startTime, EventStatus.FAILURE)
      throw e
    }
  }

  def sendBuildEvent(Date processStart, EventStatus status = jenkins.currentBuild.currentResult) {
    new BuildEvent(jenkins, [
      status   : status,
      duration : new Utils(jenkins).getDuration(processStart).toString(),
      buildtool: 'angular-cli']).send()
  }

  def sendUnitTestEvent(Date processStart, EventStatus status = jenkins.currentBuild.currentResult) {
    new TestEvent(jenkins, [
      status   : status,
      duration : new Utils(jenkins).getDuration(processStart).toString(),
      testtype: TestType.UNIT]).send()
  }

}
