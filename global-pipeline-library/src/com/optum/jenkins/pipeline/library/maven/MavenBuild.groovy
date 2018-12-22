#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.maven

import com.optum.jenkins.pipeline.library.event.BuildEvent
import com.optum.jenkins.pipeline.library.event.ArtifactStoreEvent
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.PluginValidator
import com.optum.jenkins.pipeline.library.utils.Utils

class MavenBuild implements Serializable {
  def jenkins

  MavenBuild() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  MavenBuild(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Runs a Maven build
 * @param mavenGoals String Providing a way to override the default Maven Goals
 * @param mavenOpts String Sets the environment variable MAVEN_OPTS
 * @param mavenProfiles String Comma seperated list of Maven Profiles to pass into the command line.  Defaults to null.  Ex: j2ee1.6,sonar,
 * @param javaVersion String The Java version to use. Defaults to Constants.JAVA_VERSION.
 * @param mavenVersion String The Maven version to use. Defaults to Constants.MAVEN_VERSION
 * @param jacocoMavenPluginVersion String The JaCoCo Maven plugin version. (See http://www.eclemma.org/jacoco/trunk/doc/maven.html)
 * @param mavenVersionPluginVersion String The Maven Versions Plugin Version, allows you to specify a new version number to your artifact
 * @param isDebugMode boolean Indicates if Maven should be run in debug (-X). Defaults to false.
 * @param isBatchMode boolean Indicates if Maven should be run in batch mode (-B). Defaults to true.
 * @param skipTests boolean Indicates if UnitTests should be skipped. Defaults to false.
 * @param surefireReportsPath Allows you to override the default surefireReportsPath
 * @param uploadUnitTestResults Defaults to true.  Allows you to override the default
 * @param uploadJacocoResults Defaults to true.  Allows you to override the default
 * @param runJacocoCoverage boolean Indicates if JaCoCo Code Coverage should be run. Defaults to true.
 * @param ignoreTestFailures boolean Indicates if Test Failures should be ignored. Defaults to true.  The following Sonar Scan will handle test failures.
 * @param settingsXml Allows you to override the settings.xml on the command line so you can use one from your repo
 * @param additionalProps Map An optional map of any additional properties that should be set.
 * @param pomFile Allows you to override the pom.xml on the command line so you can use one from your repo that's different from standard
 * @param newVersion Allows you to give your artifact a unique version number other than the one mentioned in pom.xml
 */
  def buildWithMaven(Map<String, Object> params) {
    def defaults = [
      mavenGoals              : null,  // defaulting to an invalid value so that it can be replaced later with values from the config
      mavenOpts               : "-Xmx1024m",
      mavenProfiles           : null,
      javaVersion             : Constants.JAVA_VERSION,
      mavenVersion            : Constants.MAVEN_VERSION,
      jacocoMavenPluginVersion: Constants.JACOCO_MAVEN_PLUGIN_VERSION,
      mavenVersionPluginVersion : Constants.MAVEN_VERSIONS_PLUGIN_VERSION,
      isDebugMode             : false,
      isBatchMode             : true,
      skipTests               : false,
      surefireReportsPath     : "**/target/surefire-reports/*.xml",
      uploadUnitTestResults   : true,
      uploadJacocoResults     : true,
      runJacocoCoverage       : true,
      ignoreTestFailures      : true,
      settingsXml             : null,
      additionalProps         : null,
      pomFile                 : null,
      newVersion              : null
    ]
    def config = defaults + params
    def startTime = new Date()
    def EVENT_BUILDTOOL = 'maven'
    jenkins.echo "buildWithMaven arguments: $config"
    def mavenGoals
    def mavenVersionGoals = ""


    if (config.newVersion) {
      jenkins.echo "setting to a version"
      mavenVersionGoals += " mvn org.codehaus.mojo:versions-maven-plugin:${config.mavenVersionPluginVersion}:set -DnewVersion=${config.newVersion} "
    }
    
    // if the mavenGoals were not overwritten in the config, then use the defaults above in the config
    if (config.mavenGoals) {
      // if you are passing in the mavenGoals, all other properties passed in are ignored.  You can pass in everything you need as part of these mavenGoals
      // additional note with regards of the new config pomFile, if you're passing in the mavenGoals, the presumption is you can also pass the pom file in the goals
      mavenGoals = config.mavenGoals
    } else {
      // Building up the mavenGoals based upon the config passed in
      mavenGoals = "-U -e clean "

      // allow for non-standard POM file
      mavenGoals += (config.pomFile ? "-f " + config.pomFile + " " : "")

      if (config.runJacocoCoverage) {
        // if you are running JaCoCo code coverage then it is assumed that you want to run the Unit Tests.  The skipTests parameter is ignored.
        mavenGoals += "org.jacoco:jacoco-maven-plugin:${config.jacocoMavenPluginVersion}:prepare-agent install org.jacoco:jacoco-maven-plugin:${config.jacocoMavenPluginVersion}:report "
        if (config.ignoreTestFailures) {
          mavenGoals += "-Dmaven.test.failure.ignore=true "
        }
      } else {

        jenkins.echo "disabling JaCoCo code coverage"
        if (config.skipTests) {

          // do not run JaCoCo Code Coverage and skip the Unit tests
          jenkins.echo "disabling unit test execution"
          mavenGoals += "install -Dmaven.test.skip=true "

        } else {

          // do not run JaCoCo Code Coverage but run the unit tests
          mavenGoals += "install "
          if (config.ignoreTestFailures) {
            mavenGoals += "-Dmaven.test.failure.ignore=true "
          }
        }

      }

      if (config.isDebugMode) {
        jenkins.echo "enabling debug mode"
        mavenGoals += "-X "
      }

      if (config.isBatchMode) {
        jenkins.echo "enabling batch mode"
        mavenGoals += "-B "
      }

      if (config.additionalProps) {
        for (def entry : config.additionalProps) {
          mavenGoals += "-D$entry.key=\"$entry.value\" "
        }
      }

      if (config.mavenProfiles) {
        mavenGoals += "-P" + config.mavenProfiles + " "
      }

      if (config.settingsXml) {
        mavenGoals += "-s " + config.settingsXml + " "
      }
    }

    mavenGoals = "mvn " + mavenGoals

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersion}", "MAVEN_VERSION=${config.mavenVersion}", "MAVEN_OPTS=${config.mavenOpts}"]) {
        if(mavenVersionGoals){
          jenkins.command(mavenVersionGoals)
        }
        jenkins.command(mavenGoals)
      }
    } catch(Exception e) {
      sendBuildEvent(jenkins, startTime, EVENT_BUILDTOOL, EventStatus.FAILURE)
      throw e
    }
    // publish junit results and JaCoCo code coverage results back to Jenkins
    if (config.uploadUnitTestResults) {
      jenkins.junit allowEmptyResults: true, testResults: config.surefireReportsPath
    }

    // This step is dependent upon the JaCoCo Plugin being installed into your Jenkins Instance.
    try {
      if (config.uploadJacocoResults) {
        jenkins.echo "uploading the JaCoCo results into Jenkins"
        jenkins.step([$class: 'JacocoPublisher'])
      }
    }
    catch (Exception ex) {
      sendBuildEvent(jenkins, startTime, EVENT_BUILDTOOL, EventStatus.FAILURE)
      jenkins.error("You either need to set 'uploadJacocoResults = false' in your config that you are passing in or you need to install the JaCoCo plugin into your Jenkins Instance")
    }
    sendBuildEvent(jenkins, startTime, EVENT_BUILDTOOL)
  }
/**
 * Deploys to artifactory.  It is expected that the DistributionManagment (see https://maven.apache.org/pom.html#Distribution_Management)
 * section will already be defined.  You can always override what is defined in your POM using the "additionalProps" Map.
 * -DdistributionManagement.repository.id= -DdistributionManagement.repository.name=  -DdistributionManagement.repository.url=
 * OR this might also work -DaltDeploymentRepository=repositoryId::repoName::http://WhatEverURL
 * @param mavenGoals String Allows you to override the default behavior so that you can pass in the pom file for the specific module that you want to deploy.
 * @param mavenProfiles String Comma seperated list of Maven Profiles to pass into the command line.  Defaults to null.  Ex: j2ee1.6,sonar,
 * @param javaVersion String The Java version to use. Defaults to Constants.JAVA_VERSION.
 * @param mavenVersion String The Maven version to use. Defaults to Constants.MAVEN_VERSION
 * @param isDebugMode boolean Indicates if Maven should be run in debug (-X). Defaults to false.
 * @param isBatchMode boolean Indicates if Maven should be run in batch mode (-B). Defaults to true.
 * @param deployAtEnd boolean Inicates if the deployment for multi module projects should be delayed until the very end so that they all deploy together
 * @param additionalProps Map An optional map of any additional properties that should be set.
 * @param pomFile Allows you to override the pom.xml on the command line so you can use one from your repo that's different from standard
 */
  def deployToArtifactory(Map<String, Object> params) {
    def defaults = [
      mavenGoals                  : "deploy ",
      mavenProfiles               : null,
      javaVersion                 : Constants.JAVA_VERSION,
      mavenVersion                : Constants.MAVEN_VERSION,
      isDebugMode                 : false,
      isBatchMode                 : true,
      deployAtEnd                 : false,  // see http://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html
      settingsXml                 : null,
      artifactoryUserCredentialsId: null,
      pomFile                     : null
    ]
    def config = defaults + params
    def startTime = new Date()

    jenkins.echo "deployToArtifactory arguments: $config"
    def mavenGoals = config.mavenGoals

    if (config.isDebugMode) {
      jenkins.echo "enabling debug mode"
      mavenGoals += "-X "
    }

    if (config.isBatchMode) {
      jenkins.echo "enabling batch mode"
      mavenGoals += "-B "
    }

    mavenGoals += "-f " + (config.pomFile ? config.pomFile : "pom.xml") + " -e -Dmaven.test.skip=true "

    if (config.deployAtEnd) {
      jenkins.echo "enabling deployAtEnd to deploy the deployment for multi module projects until the very end"
      mavenGoals += "-DdeployAtEnd=true "
    }

    // Adding the additional properties from the config onto the command line
    if (config.additionalProps) {
      for (def entry : config.additionalProps) {
        mavenGoals += "-D$entry.key=\"$entry.value\" "
      }
    }

    // After adding additional properties if ci.env is not defined then set it to blank
    if (!mavenGoals.contains("ci.env")) {
      mavenGoals += "-Dci.env= "
    }

    if (config.settingsXml) {
      mavenGoals += "-s " + config.settingsXml + " "
    }

    if (config.mavenProfiles) {
      mavenGoals += "-P" + config.mavenProfiles + " "
    }

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersion}", "MAVEN_VERSION=${config.mavenVersion}"]) {
        jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: config.artifactoryUserCredentialsId, usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASS']]) {
          jenkins.command "mvn ${mavenGoals}"
        }
      }
    } catch (Exception ex) {
      sendArtifactStoreEvent(jenkins, startTime, EventStatus.FAILURE)
      jenkins.error("There was a problem during the Artifactory Deploy." + ex.getMessage())
    }
    sendArtifactStoreEvent(jenkins, startTime)
  }

  // uses the current jenkins env build status
  def sendBuildEvent(jenkins, Date processStart, String toolName){
    new BuildEvent(jenkins, [status: jenkins.currentBuild.currentResult, duration: new Utils(jenkins).getDuration(processStart).toString(), buildtool: toolName]).send()
  }

  def sendBuildEvent(jenkins, Date processStart, String toolName, EventStatus status){
    new BuildEvent(jenkins, [status: status, duration: new Utils(jenkins).getDuration(processStart).toString(), buildtool: toolName]).send()
  }

  // uses the current jenkins env build status
  def sendArtifactStoreEvent(jenkins, Date processStart){
    new ArtifactStoreEvent(jenkins, [status: jenkins.currentBuild.currentResult, duration: new Utils(jenkins).getDuration(processStart).toString()]).send()
  }

  def sendArtifactStoreEvent(jenkins, Date processStart, EventStatus status){
    new ArtifactStoreEvent(jenkins, [status: status, duration: new Utils(jenkins).getDuration(processStart).toString()]).send()
  }

}
