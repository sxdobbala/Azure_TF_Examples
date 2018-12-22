#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.angular

import com.optum.jenkins.pipeline.library.utils.Constants

class Angular implements Serializable {
  def jenkins

  Angular() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Angular(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Builds Angular JS application using node JS.
 *
 * @param nodeJSVersion String The version of Node JS to use. Defaults to one set in Constants.
 * @param npmAuthKey String The NPM Auth Key. Defaults to one set in Constants.
 * @param npmEmail String The NPM Email. Defaults to one set in Constants.
 * @param useNPMCache boolean Whether to use NPM Cache for the build or not. Defaults to true. Saves quite a bit of time.
 *                                Refer to for Docker template configuration
 *                                https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_nodejs
 * @param buildForEnvironment String Build for which environment. Environment specific values are injected. Required.
 * @param extraBuildOptions String
 *
 *  The following passed in parameters
 *         buildForEnvironment = "dev"
 *         useNPMCache = true
 * Will result in the following Node JS commands
 *            npm-cache install
 *         node_modules/@angular/cli/bin/ng build --environment=dev
 * */

  def buildAngularApp(Map<String, Object> params) {
    def defaults = [
      nodeJSVersion      : Constants.NODEJS_VERSION, //required
      npmAuthKey         : Constants.NPM_AUTH_KEY, //optional
      npmEmail           : Constants.NPM_EMAIL,  // optional
      useCache           : true,
      buildForEnvironment: 'dev',
      additionalOptions  : "" //optional
    ]
    def config = defaults + params

    jenkins.echo "buildAngularApp arguments: $config"

    if (!config.buildForEnvironment) {
      jenkins.error "The buildForEnvironment is required."
    }

    jenkins.withEnv(["NODEJS_VERSION=${config.nodeJSVersion}", "NPM_AUTH_KEY=${config.npmAuthKey}", "NPM_EMAIL=${config.npmEmail}"]) {
      jenkins.withEnv(["NODEJS_HOME=$jenkins.env.NODEJS_TOOLS_DIR/node-v$jenkins.env.NODEJS_VERSION-linux-x64"]) {
        jenkins.withEnv(["PATH=${jenkins.env.NODEJS_HOME}/bin:$jenkins.env.PATH"]) {

          def appBuildCmd = "node_modules/@angular/cli/bin/ng build --environment=$config.buildForEnvironment $config.additionalOptions"

          setupAngularBuildEnvironment(config.useCache)

          jenkins.echo "Running $appBuildCmd"
          jenkins.command(appBuildCmd)
        }
      }
    }
  }

/**
 * Unit Test Angular JS application using node JS.
 *
 * @param nodeJSVersion String The version of Node JS to use. Defaults to one set in Constants.
 * @param npmAuthKey String The NPM Auth Key. Defaults to one set in Constants.
 * @param npmEmail String The NPM Email. Defaults to one set in Constants.
 * @param useCache boolean Whether to use NPM Cache for the build or not. Defaults to true. Saves quite a bit of time.
 *                                Refer to for Docker template configuration
 *                                https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_nodejs
 * @param buildForEnvironment String Build for which environment. Environment specific values are injected. Required.
 * @param addnlTestOptions String
 * @param generateCodeCoverage String Whether to generate code coverage or not
 *
 *  The following passed in parameters
 *         generateCodeCoverage = true
 *         useCache = true
 * Will result in the following Node JS commands
 *            npm-cache install
 *         node_modules/@angular/cli/bin/ng test --single-run --code-coverage
 * */

  def testAngularApp(Map<String, Object> params) {
    def defaults = [
      nodeJSVersion       : Constants.NODEJS_VERSION, //required
      npmAuthKey          : Constants.NPM_AUTH_KEY, //optional
      npmEmail            : Constants.NPM_EMAIL,  // optional
      useCache            : true,
      buildForEnvironment : 'dev',
      additionalOptions   : "", //optional
      generateCodeCoverage: true
    ]
    def config = defaults + params

    jenkins.echo "testAngularApp arguments: $config"

    if (!config.buildForEnvironment) {
      jenkins.error "The buildForEnvironment is required."
    }

    jenkins.withEnv(["NODEJS_VERSION=${config.nodeJSVersion}", "NPM_AUTH_KEY=${config.npmAuthKey}", "NPM_EMAIL=${config.npmEmail}"]) {
      jenkins.withEnv(["NODEJS_HOME=$jenkins.env.NODEJS_TOOLS_DIR/node-v$jenkins.env.NODEJS_VERSION-linux-x64"]) {
        jenkins.withEnv(["PATH=${jenkins.env.NODEJS_HOME}/bin:$jenkins.env.PATH"]) {

          def appTestCmd = "node_modules/@angular/cli/bin/ng test $config.additionalOptions --single-run " + (config.generateCodeCoverage ? "--code-coverage" : "")

          setupAngularBuildEnvironment(config.useCache)

          jenkins.echo "Running $appTestCmd"
          jenkins.command(appTestCmd)
        }
      }
    }
  }

  def setupAngularBuildEnvironment(useCache) {

    jenkins.echo "setupAngularBuildEnvironment arguments: $useCache"
    def installDepndenciesCmd = (useCache ? "npm-cache install" : "npm install")
    jenkins.echo "Running $installDepndenciesCmd"
    jenkins.command(installDepndenciesCmd)
  }

}
