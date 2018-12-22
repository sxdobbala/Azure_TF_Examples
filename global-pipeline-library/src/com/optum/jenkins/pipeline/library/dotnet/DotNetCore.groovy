#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.dotnet

import com.optum.jenkins.pipeline.library.utils.Constants

class DotNetCore implements Serializable {
  def jenkins

  DotNetCore() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  DotNetCore(jenkins) {
    this.jenkins = jenkins
  }
/**
 * Restores dependencies for C# application using .Net Core Framework. This routine should be
 * called from either build, publish or test functions since it requires environment to be setup
 *     Refer to for Docker template configuration
 *     https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_dotnet
 * @param dotNetCoreVersion String The version of .Net Core Framework to use. Defaults to one set in Constants.
 * @param projectFile String Solution file name including path
 * @param targetRuntime String Target Runtime for application
 * @param additionalRestoreOptions String Additional Options to restore dependencies
 *
 *  The following passed in parameters
 *
 * Will result in the following Node JS commands
 *         dotnet restore
 * */

  def restoreDotNetCoreDependencies(Map<String, Object> params) {
    def defaults = [
      dotNetCoreVersion       : Constants.DOTNETCORE_VERSION, //required
      projectFile             : "",
      targetRuntime           : "",
      additionalRestoreOptions: "" //optional
    ]
    def config = defaults + params

    jenkins.echo "restoreDotNetCoreDependencies arguments: $config"

    def appRestoreCmd = "dotnet restore $config.projectFile $config.additionalRestoreOptions" + " " +
      (config.targetRuntime == "" ? "" : "--runtime $config.targetRuntime")

    jenkins.echo "Running $appRestoreCmd"
    jenkins.command(appRestoreCmd)
  }

/**
 * Builds C# application using .Net Core Framework.
 *     Refer to for Docker template configuration
 *     https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_dotnet
 * @param dotNetCoreVersion String The version of .Net Core Framework to use. Defaults to one set in Constants.
 * @param projectFile String Solution file name including path
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * @param targetRuntime String Target Runtime for application
 * @param additionalRestoreOptions String Additional Options to restore dependencies
 * @param additionalBuildOptions String Additional Options to build application
 *
 * The following passed in parameters
 *
 *
 * Will result in the following commands
 *         dotnet restore
 *         dotnet build
 * */

  def buildDotNetCoreApp(Map<String, Object> params) {
    def defaults = [
      dotNetCoreVersion       : Constants.DOTNETCORE_VERSION, //required
      projectFile             : "",
      buildConfiguration      : "Release",
      targetRuntime           : "",
      additionalRestoreOptions: "", //optional
      additionalBuildOptions  : "" //optional
    ]

    def config = defaults + params

    jenkins.echo "buildDotNetCoreApp arguments: $config"

    if (config.projectFile != "" && !jenkins.fileExists(config.projectFile)) {
      jenkins.error "Project File $config.projectFile file must be present in the workspace."
    }

    jenkins.withEnv(["DOTNET_VERSION=${config.dotNetCoreVersion}", "DOTNET_CLI_TELEMETRY_OPTOUT='1'"]) {
      jenkins.withEnv(["DOTNET_HOME=$jenkins.env.DOTNET_TOOLS_DIR/$jenkins.env.DOTNET_VERSION"]) {
        jenkins.withEnv(["PATH=${jenkins.env.DOTNET_HOME}/bin:$jenkins.env.PATH"]) {

          def appBuildCmd = "dotnet build $config.projectFile $config.additionalBuildOptions" + " " +
            (config.targetRuntime == "" ? "" : "--runtime $config.targetRuntime") + " " +
            (config.buildConfiguration == "" ? "" : "--configuration $config.buildConfiguration")

          restoreDotNetCoreDependencies(config)

          jenkins.echo "Running $appBuildCmd"
          jenkins.command(appBuildCmd)
        }
      }
    }
  }

/**
 * Runs tests or C# application using .Net Core Framework.
 *     Refer to for Docker template configuration
 *     https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_dotnet
 * @param dotNetCoreVersion String The version of .Net Core Framework to use. Defaults to one set in Constants.
 * @param projectFile String Solution file name including path
 * @param testProjectFile String Unit Test project file name including path
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * @param targetRuntime String Target Runtime for application
 * @param additionalRestoreOptions String Additional Options to restore dependencies
 * @param additionalTestOptions String Additional Options to test application
 *
 * The following passed in parameters
 *
 *
 * Will result in the following commands
 *         dotnet restore
 *         dotnet test
 * */

  def testDotNetCoreApp(Map<String, Object> params) {
    def defaults = [
      dotNetCoreVersion       : Constants.DOTNETCORE_VERSION, //required
      projectFile             : "",
      testProjectFile         : "",
      buildConfiguration      : "Release",
      targetRuntime           : "",
      additionalRestoreOptions: "", //optional
      additionalTestOptions   : "" //optional
    ]

    def config = defaults + params

    jenkins.echo "testdDotNetCoreApp arguments: $config"

    if (config.projectFile != "" && !jenkins.fileExists(config.projectFile)) {
      jenkins.error "Project File $config.projectFile file must be present in the workspace."
    }

    jenkins.withEnv(["DOTNET_VERSION=${config.dotNetCoreVersion}", "DOTNET_CLI_TELEMETRY_OPTOUT='1'"]) {
      jenkins.withEnv(["DOTNET_HOME=$jenkins.env.DOTNET_TOOLS_DIR/$jenkins.env.DOTNET_VERSION"]) {
        jenkins.withEnv(["PATH=${jenkins.env.DOTNET_HOME}/bin:$jenkins.env.PATH"]) {

          def appTestCmd = "dotnet test $config.testProjectFile $config.additionalTestOptions" + " " +
            (config.buildConfiguration == "" ? "" : "--configuration $config.buildConfiguration")

          restoreDotNetCoreDependencies(config)

          jenkins.echo "Running $appTestCmd"
          jenkins.command(appTestCmd)
        }
      }
    }
  }

/**
 * Publish C# application using .Net Core Framework.
 *     Refer to for Docker template configuration
 *     https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_dotnet
 * @param dotNetCoreVersion String The version of .Net Core Framework to use. Defaults to one set in Constants.
 * @param projectFile String Solution file name including path
 * @param outputFolder String path to output published files
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * @param targetRuntime String Target Runtime for application
 * @param targetFramework String Target Framework for application
 * @param additionalRestoreOptions String Additional Options to restore dependencies
 * @param additionalPublishOptions String Additional Options to test application
 *
 * The following passed in parameters
 *
 *
 * Will result in the following commands
 *         dotnet restore
 *         dotnet publish
 * */

  def publishDotNetCoreApp(Map<String, Object> params) {
    def defaults = [
      dotNetCoreVersion       : Constants.DOTNETCORE_VERSION, //required
      projectFile             : "",
      outputFolder            : "",
      buildConfiguration      : "Release",
      targetRuntime           : "",
      targetFramework         : "",
      additionalRestoreOptions: "", //optional
      additionalPublishOptions: "" //optional
    ]

    def config = defaults + params

    jenkins.echo "publishDotNetCoreApp arguments: $config"

    if (config.projectFile != "" && !jenkins.fileExists(config.projectFile)) {
      jenkins.error "Project File $config.projectFile file must be present in the workspace."
    }

    jenkins.withEnv(["DOTNET_VERSION=${config.dotNetCoreVersion}", "DOTNET_CLI_TELEMETRY_OPTOUT='1'"]) {
      jenkins.withEnv(["DOTNET_HOME=$jenkins.env.DOTNET_TOOLS_DIR/$jenkins.env.DOTNET_VERSION"]) {
        jenkins.withEnv(["PATH=${jenkins.env.DOTNET_HOME}/bin:$jenkins.env.PATH"]) {

          def appPubCmd = "dotnet publish $config.projectFile $config.additionalPublishOptions" + " " +
            (config.outputFolder == "" ? "" : "-o $config.outputFolder") + " " +
            (config.targetRuntime == "" ? "" : "-r $config.targetRuntime") + " " +
            (config.buildConfiguration == "" ? "" : "-c $config.buildConfiguration") + " " +
            (config.targetFramework == "" ? "" : "-f $config.targetFramework")


          restoreDotNetCoreDependencies(config)

          jenkins.echo "Running $appPubCmd"
          jenkins.command(appPubCmd)
        }
      }
    }
  }
}
