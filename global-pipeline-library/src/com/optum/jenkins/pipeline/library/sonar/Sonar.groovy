package com.optum.jenkins.pipeline.library.sonar

import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.SonarEvent
import com.optum.jenkins.pipeline.library.scm.Git
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.Utils
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import org.jenkinsci.plugins.credentialsbinding.impl.CredentialNotFoundException
import groovy.json.JsonSlurperClassic


class Sonar implements Serializable {
  private final String SONAR_API_TOKEN = '092c919905283a6d35ff1abe2128fc52e48a6156'
  def jenkins
  def startTime
  def isPreview

  Sonar() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Sonar(jenkins) {
    this.jenkins = jenkins
    startTime = new Date()
    isPreview = false
  }

  def getPreview() {
    return isPreview
  }
  def setPreview(boolean value) {
    isPreview = value
  }

/**
 * Runs Sonar scan using Maven.
 * <p>
 * If using Codehub GIT:
 * You only need to pass the first 2 arguments if you do not need to exclude anything.
 * </p>
 * <p>
 * If using GitHub:<br/>
 * When you pass in a credentialsID that has permissions to 'write' to the GitHub project then this Sonar Scan will
 * write comments back to the GIT Pull Request with pass/fail information. See
 * https://docs.sonarqube.org/display/PLUG/GitHub+Plugin for details. This assumes you are using a GitHub organization
 * in Jenkins.
 * </p>
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to and the project name comes from optumfile.yml<br/>
 * Here are 3 different example calls to this method
 * <ul>
 *      <li>sonarScan('ICP','Webapp')</li>
 *      <li>sonarScan('ICP','AdminWeb')</li>
 *      <li>sonarScan('ICP','SearchService')</li>
 * </ul>
 * In sonar would appear like this
 * <ul>
 *      <li>ICP-Webapp</li>
 *      <li>ICP-AdminWeb</li>
 *      <li>ICP-SearchService</li>
 * </ul>
 * <p>
 * String pattern examples: http://wiki.optum.com/pages/viewpage.action?pageId=61742964
 * </p>
 *
 *
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param javaVersionForSonar The Java version to use. Defaults to Constants.JAVA_VERSION_FOR_SONAR.
 * @param mavenVersion The Maven version to use. Defaults to Constants.MAVEN_VERSION.
 * @param isDebugMode Indicates if Maven should be run in debug (-X). Defaults to false.
 * @param isBatchMode boolean Indicates if Maven should be run in batch mode (-B). Defaults to true.
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing results to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param sonarHostUrl Url to Sonar. Defaults to Constants.SONAR_HOST_URL.
 * @param sonarLogin Login for Sonar. Defaults to Constants.SONAR_LOGIN.
 * @param sonarMavenPluginVersion Sonar Maven plugin version. Defaults to Constants.SONAR_MAVEN_PLUGIN_VERSION.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *         main branch, you will need to specify it here. See details here:
 *         https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 * @param pomFile Allows you to override the pom.xml on the command line so you can use one from your repo that's different from standard
 */
  def scanWithMaven(Map<String, Object> params) {
    def defaults = [
      javaVersionForSonar    : Constants.JAVA_VERSION_FOR_SONAR,
      mavenVersion           : Constants.MAVEN_VERSION,
      mavenProfiles          : null,
      isDebugMode            : false,
      isBatchMode            : true,
      sonarHostUrl           : Constants.SONAR_HOST_URL,
      sonarLogin             : Constants.SONAR_LOGIN,
      sonarMavenPluginVersion: Constants.SONAR_MAVEN_PLUGIN_VERSION,
      settingsXml            : null,
      pomFile                : null,
      mainBranchName         : Constants.SONAR_MAIN_BRANCH_NAME
    ]
    validateInputParameters(params)
    def config = defaults + params
    jenkins.echo "scanWithMaven arguments: $config"
    def mavenGoals = "-e org.sonarsource.scanner.maven:sonar-maven-plugin:${config.sonarMavenPluginVersion}:sonar "
    mavenGoals += createProps(config, true)
    mavenGoals += "-Dsonar.host.url=${config.sonarHostUrl} "
    mavenGoals += "-Dsonar.login=${config.sonarLogin} "

    if (config.isDebugMode) {
      mavenGoals += "-X "
    }

    if (config.isBatchMode) {
      jenkins.echo "enabling batch mode"
      mavenGoals += "-B "
    }

    mavenGoals += (config.pomFile ? "-f " + config.pomFile + " " : "")

    if (config.settingsXml) {
      mavenGoals += "-s " + config.settingsXml + " "
    }

    if (config.mavenProfiles) {
      mavenGoals += "-P" + config.mavenProfiles + " "
    }

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersionForSonar}", "MAVEN_VERSION=${config.mavenVersion}"]) {
        //jenkins.command will echo full maven goals including the secret string into jenkins console
        //wrap this jenkins command inside withCredentials block if gitUserCredentialsId provided in the parameter
        //so the the sensitive information is masked in console
        if (config.gitUserCredentialsId) {
          jenkins.withCredentials([jenkins.string(credentialsId: config.gitUserCredentialsId, variable: 'githubOauth')]) {
            jenkins.command "mvn ${mavenGoals}"
          }
        } else {
          jenkins.command "mvn ${mavenGoals}"
        }
      }
    }
    catch (CredentialNotFoundException cnfe) {
      jenkins.error "Unable to find the correctly configured oauth credentials in Jenkins '${config.gitUserCredentialsId}'.  The credential in Jenkins needs to be of type 'secrettext'. Please look at this article: https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/"
    }
    catch (Exception ex) {
      jenkins.echo ex.getMessage()
      sendSonarEvent(jenkins, startTime, EventStatus.FAILURE, 'maven')
      jenkins.error("Sonar scan failed")
    }
    sendSonarEvent(jenkins, startTime, EventStatus.SUCCESS, 'maven')
  }

/**
 * Runs Sonar scan using Gradle (using gradle wrapper.)
 * <p>
 * If using Codehub GIT:
 * You only need to pass the first 2 arguments if you do not need to exclude anything.
 * </p>
 * <p>
 * If using GitHub:<br/>
 * When you pass in a credentialsID that has permissions to 'write' to the GitHub project then this Sonar Scan will
 * write comments back to the GIT Pull Request with pass/fail information. See
 * https://docs.sonarqube.org/display/PLUG/GitHub+Plugin for details. This assumes you are using a GitHub organization
 * in Jenkins.
 * </p>
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to.<br/>
 * Here are 3 different example calls to this method
 * <ul>
 *      <li>sonarScan('ICP','Webapp')</li>
 *      <li>sonarScan('ICP','AdminWeb')</li>
 *      <li>sonarScan('ICP','SearchService')</li>
 * </ul>
 * In sonar would appear like this
 * <ul>
 *      <li>ICP-Webapp</li>
 *      <li>ICP-AdminWeb</li>
 *      <li>ICP-SearchService</li>
 * </ul>
 * <p>
 * String pattern examples: http://wiki.optum.com/pages/viewpage.action?pageId=61742964
 * </p>
 *
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param javaVersionForSonar The Java version to use. Defaults to Constants.JAVA_VERSION_FOR_SONAR.
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param sonarHostUrl Url to Sonar. Defaults to Constants.SONAR_HOST_URL.
 * @param sonarLogin Login for Sonar. Defaults to Constants.SONAR_LOGIN.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *          main branch, you will need to specify it here. See details here:
 *          https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 */
  def scanWithGradle(Map<String, Object> params) {
    def defaults = [
      javaVersionForSonar: Constants.JAVA_VERSION_FOR_SONAR,
      sonarHostUrl       : Constants.SONAR_HOST_URL,
      sonarLogin         : Constants.SONAR_LOGIN,
      sonarProjectVersion: null,
      mainBranchName     : Constants.SONAR_MAIN_BRANCH_NAME
    ]
    validateInputParameters(params)
    def config = defaults + params

    jenkins.echo "scanWithGradle arguments: $config"

    def gradleTasks = "sonarqube "
    gradleTasks += createProps(config, false)
    gradleTasks += "-Dsonar.host.url=${config.sonarHostUrl} " +
      "-Dsonar.login=${config.sonarLogin} "

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersionForSonar}"]) {
        if (jenkins.isUnix()) {
          jenkins.command "./gradlew ${gradleTasks}"
        } else {
          jenkins.command "gradlew.bat ${gradleTasks}"
        }
      }
    } catch (Exception ex) {
      sendSonarEvent(jenkins, startTime, EventStatus.FAILURE, 'gradle')
      jenkins.error("Sonar scan failed: " + ex.getMessage())
    }
    sendSonarEvent(jenkins, startTime, EventStatus.SUCCESS, 'gradle')
  }

/**
 * Runs Sonar scan using the Sonar Scanner plugin.
 * See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
 * <p>
 * If using Codehub GIT:
 * You only need to pass the first 2 arguments if you do not need to exclude anything.
 * </p>
 * <p>
 * If using GitHub:<br/>
 * When you pass in a credentialsID that has permissions to 'write' to the GitHub project then this Sonar Scan will
 * write comments back to the GIT Pull Request with pass/fail information. See
 * https://docs.sonarqube.org/display/PLUG/GitHub+Plugin for details. This assumes you are using a GitHub organization
 * in Jenkins.
 * </p>
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to.<br/>
 *
 * @param sources Sonar sources value.. See sonar.sources property here https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param javaVersionForSonar The Java version to use. Defaults to Constants.JAVA_VERSION_FOR_SONAR.
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param sonarToolVersion The version of sonar tool for the mixin. default to 'sonar-scanner-2.8'
 *          since it's the only version currently defined in mixin
 *          https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_sonar
 * @param sonarServer The name of the Sonar server instance configured in Jenkins. See Manage Jenkins > Configure
 *          System > SonarQube configuration. Defaults to 'Sonar OCD'.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *          main branch, you will need to specify it here. See details here:
 *          https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 */
  def scanWithSonarScanner(Map<String, Object> params) {
    def defaults = [
      javaVersionForSonar: Constants.JAVA_VERSION_FOR_SONAR,
      sonarToolVersion   : Constants.SONAR_TOOL_VERSION,
      sonarServer        : 'Sonar OCD',
      sonarProjectVersion: null,
      mainBranchName     : Constants.SONAR_MAIN_BRANCH_NAME
    ]
    validateInputParameters(params)
    def config = defaults + params

    jenkins.echo "scanWithSonarScanner arguments: $config"

    if (!config.sources) {
      jenkins.error "Sources property is required"
    }

    def sonarProps = createProps(config)
    sonarProps += "-Dsonar.sources=\"$config.sources\" "

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersionForSonar}", "SONAR_VERSION=${config.sonarToolVersion}"]) {
        jenkins.withSonarQubeEnv(config.sonarServer) {
          jenkins.command "sonar-scanner ${sonarProps}"
        }
      }
    } catch (Exception ex) {
      sendSonarEvent(jenkins, startTime, EventStatus.FAILURE, 'sonarscanner')
      jenkins.error("Sonar scan failed: " + ex.getMessage())
    }
    sendSonarEvent(jenkins, startTime, EventStatus.SUCCESS, 'sonarscanner')
  }
/**
 * Runs Sonar scan using the Sonar Scanner plugin for .Net core.
 * See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+MSBuild
 * @param sources Sonar sources value.. See sonar.sources property here https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param javaVersionForSonar The Java version to use. Defaults to Constants.JAVA_VERSION_FOR_SONAR.
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param projectKey String that will be present in Optum.yml file.
 * @param projectName String that will be present in Optum.yml file which indicates the name of the project that was analyzed.
 * @param projectVersion String that will be present in Optum.yml file which indicates the Version of the project that was analyzed.
 * @param sonarTool The name of the Sonar tool instance configured in Jenkins. See Manage Jenkins > Global Tool
 *          Configuration > SonarQube Scanner. Defaults to 'sonar'.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *          main branch, you will need to specify it here. See details here:
 *          https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 * @param testProjectFile The Test Project file name.
 * @param additionalTestOptions An optional map of any additional properties that should be set for Test.
 */
  def scanWithSonarScannerDotnetCore(Map<String, Object> params) {
    def defaults = [
      sonarHostUrl            : Constants.SONAR_HOST_URL,
      sonarLogin              : Constants.SONAR_LOGIN,
      dotNetCoreVersion       : Constants.DOTNETCORE_VERSION,
      projectFile             : '',
      testProjectFile         : '',
      buildConfiguration      : "debug",
      targetRuntime           : '',
      additionalRestoreOptions: '', //optional
      additionalBuildOptions  : '', //optional
      additionalTestOptions   : '', //optional
      additionalProps         : '',
      mainBranchName          : Constants.SONAR_MAIN_BRANCH_NAME,
      dotNetSonarVersion      : Constants.SONAR_DOTNET_TOOL_VERSION
    ]
    validateInputParameters(params)
    def config = defaults + params
    jenkins.echo "scanWithSonarScannerarguments:$config"
    if (config.projectFile != "" && !jenkins.fileExists(config.projectFile)) {
      jenkins.error "Project File $config.projectFile file must be present in the workspace."
    }

    try {
        jenkins.withEnv(["SONAR_VERSION=${config.dotNetSonarVersion}",
                         "DOTNET_VERSION=${config.dotNetCoreVersion}",
                         "DOTNET_CLI_TELEMETRY_OPTOUT='1'"]) {
          def appBuildCmdbegin = "dotnet \${SONAR_DOTNETCORE_HOME}/SonarScanner.MSBuild.dll begin ${config.additionalProps} " +
            "/k:"  + OptumFileReader.getProjectKey(jenkins) + " " +
            "/n:"  + OptumFileReader.getProjectFriendlyName(jenkins) + " " +
            "/v:"  + OptumFileReader.getProjectVersion(jenkins) + " " +
            "/d:sonar.host.url=${config.sonarHostUrl} " + ' ' +
            "/d:sonar.login=${config.sonarLogin} " + ' '
          jenkins.command(appBuildCmdbegin)
          def appBuildCmd = "dotnet build $config.projectFile $config.additionalBuildOptions" + " " +
            (config.targetRuntime == "" ? "" : "--runtime $config.targetRuntime") + " " +
            (config.buildConfiguration == "" ? "" : "--configuration $config.buildConfiguration")
          jenkins.echo "Running $appBuildCmd"
          jenkins.command(appBuildCmd)
          def appTestCmd = "dotnet test $config.testProjectFile $config.additionalTestOptions " +
            (config.buildConfiguration == '' ? '' : "--configuration $config.buildConfiguration")
          jenkins.echo "Running $appTestCmd"
          jenkins.command(appTestCmd)
          def appBuildCmdend = "dotnet \${SONAR_DOTNETCORE_HOME}/SonarScanner.MSBuild.dll end /d:sonar.login=${config.sonarLogin}"
          jenkins.echo "Running $appBuildCmdend"
          jenkins.command(appBuildCmdend)
        }
    } catch (Exception ex) {
      jenkins.echo ex.getMessage()
      jenkins.error("Sonarscanfailed:" + ex.getMessage())
    }
  }
/**
 * Runs Sonar scan using NPM sonarqube-scanner plugin.
 * See https://www.npmjs.com/package/sonarqube-scanner
 * <p>
 * If using Codehub GIT:
 * You only need to pass the first 2 arguments if you do not need to exclude anything.
 * </p>
 * <p>
 * If using GitHub:<br/>
 * When you pass in a credentialsID that has permissions to 'write' to the GitHub project then this Sonar Scan will
 * write comments back to the GIT Pull Request with pass/fail information. See
 * https://docs.sonarqube.org/display/PLUG/GitHub+Plugin for details. This assumes you are using a GitHub organization
 * in Jenkins.
 * </p>
 * <p>
 * Will only work with NodeJS >= v4.8.7 because of required ES6 arrow functions
 * If you are using Jenkins mixin for NodeJS set NODEJS_VERSION to one of the available versions (ex. 6.9.1)
 * https://codehub.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_nodejs
 * </p>
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to.<br/>
 *
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param
 * Id The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *          main branch, you will need to specify it here. See details here:
 *          https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 */
  def scanWithNpm(Map<String, Object> params) {
    def defaults = [
      sonarHostUrl   : Constants.SONAR_HOST_URL,
      sonarLogin     : Constants.SONAR_LOGIN,
      mainBranchName : Constants.SONAR_MAIN_BRANCH_NAME
    ]
    validateInputParameters(params)

    def config = defaults + params

    jenkins.echo "scanWithNpm arguments: $config"

    def scanProps = createProps(config, false)
    scanProps += "-Dsonar.host.url=${config.sonarHostUrl} " +
      "-Dsonar.login=${config.sonarLogin} "

    try {
      jenkins.command "node_modules/sonarqube-scanner/dist/bin/sonar-scanner ${scanProps}"
    } catch (Exception ex) {
      sendSonarEvent(jenkins, startTime, EventStatus.FAILURE, 'npm')
      jenkins.error("Sonar scan failed: " + ex.getMessage())
    }
    sendSonarEvent(jenkins, startTime, EventStatus.SUCCESS, 'npm')
  }

/**
 * Runs Sonar scan using the Sonar Scanner plugin importing properties from file.
 * See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
 * <p>
 * If using Codehub GIT:
 * You only need to pass the first 2 arguments if you do not need to exclude anything.
 * </p>
 * <p>
 * If using GitHub:<br/>
 * When you pass in a credentialsID that has permissions to 'write' to the GitHub project then this Sonar Scan will
 * write comments back to the GIT Pull Request with pass/fail information. See
 * https://docs.sonarqube.org/display/PLUG/GitHub+Plugin for details. This assumes you are using a GitHub organization
 * in Jenkins.
 * </p>
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to.<br/>
 *
 * @param sources Sonar sources value.. See sonar.sources property here https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
 * @param scmRepoUrl The URL to the Git repo. Defaults to using Git to find the remote URL.
 * @param javaVersionForSonar The Java version to use. Defaults to Constants.JAVA_VERSION_FOR_SONAR.
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param sonarToolVersion The version of sonar tool for the mixin. default to 'sonar-scanner-2.8'
            since it's the only version currently defined in mixin
            https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_sonar
 * @param sonarServer The name of the Sonar server instance configured in Jenkins. See Manage Jenkins > Configure
 *          System > SonarQube configuration. Defaults to 'Sonar OCD'.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param mainBranchName The name of the main branch. This defaults to 'master'. If your Sonar project has a different
 *          main branch, you will need to specify it here. See details here:
 *          https://docs.sonarqube.org/display/SONARQUBE67/Branch+Plugin
 * @param additionalProps An optional map of any additional properties that should be set.
 */
  def scanUsingSonarScannerWithPropertiesFile(Map<String, Object> params) {
    def defaults = [
      javaVersionForSonar: Constants.JAVA_VERSION_FOR_SONAR,
      sonarToolVersion   : Constants.SONAR_TOOL_VERSION,
      sonarServer        : 'Sonar OCD',
      additionalProps    : '',
      mainBranchName     : Constants.SONAR_MAIN_BRANCH_NAME
    ]
    validateInputParameters(params)
    def config = defaults + params

    jenkins.echo "scanUsingSonarScannerWithPropertiesFile arguments: $config"

    if (!jenkins.fileExists('sonar-project.properties')) {
      jenkins.error "sonar-project.properties file must be present in the workspace."
    }

    try {
      jenkins.withEnv(["JAVA_VERSION=${config.javaVersionForSonar}", "SONAR_VERSION=${config.sonarToolVersion}"]) {
        jenkins.withSonarQubeEnv(config.sonarServer) {
          jenkins.command "sonar-scanner ${config.additionalProps} " +
            "-Dsonar.projectKey=" + OptumFileReader.getProjectKey(jenkins) + " " +
            "-Dsonar.projectName=" + OptumFileReader.getProjectFriendlyName(jenkins) + " "
        }
      }
    } catch (Exception ex) {
      sendSonarEvent(jenkins, startTime, EventStatus.FAILURE, 'sonarscannerwithpropertiesfile')
      jenkins.error("Sonar scan failed: " + ex.getMessage())
    }
    sendSonarEvent(jenkins, startTime, EventStatus.SUCCESS, 'sonarscannerwithpropertiesfile')
  }

  /**
   * Converts a map of sonar metrics into html, that can be sent in email attachment or displayed on Jenkins dashboard
   * e.g. {blocker_violations=39, critical_violations=14}*
   * @param sonarMetricsMap
   */
  def convertSonarMetricsMapToHTML(sonarMetricsMap) {
    String htmlResult = "<html><head><style type='text/css'>.header { margin: 30px;padding: 5px;background-color: #d9d9d9} .row {margin: 30px;padding: 5px;background-color:  #b3f0ff}</style></head><body><h2>sonar quality metrics</h2><table><tr><th class='header'>metric</th><th class='header'>value</th></tr>"
    for (mapEntry in sonarMetricsMap) {
      htmlResult += "<tr><td>${mapEntry.key}</td><td>${mapEntry.value}</td></tr>"
    }
    htmlResult += "</table></body></html>"
    //jenkins.echo htmlResult

    return htmlResult
  }

  /**
   * Retrieves sonar metrics using API call to sonar, converts into a simple map of key value
   * e.g. {blocker_violations=39, critical_violations=14}* Following metrics will be retrieved by default:
   *              "blocker_violations,critical_violations,major_violations,new_blocker_violations,new_critical_violations,new_major_violations,coverage,new_coverage,skipped_tests,test_errors,test_failures"
   * @param additionalMetrics You can specify additional metrics to get information on
   * @param sonarMetricsApiBaseURL The URL where sonar metrics
   */
  def getSonarMetrics(Map<String, Object> params) {
    def defaults = [
      sonarHostUrl          : Constants.SONAR_HOST_URL,
      sonarMetricsApiBaseURL: '/api/measures/component',
      additionalMetrics     : '',
      isPreview             : false,
      isDebugMode           : false
    ]

    def config = defaults + params
    if (!config.scmRepoUrl) {
      Git gitInfo = new Git(jenkins)
      config.branchName = gitInfo.getBranch()
    }

    jenkins.echo "getSonarMetrics arguments: $config"

    def DEFAULT_DESIRED_METRICS = 'blocker_violations,critical_violations,major_violations,new_blocker_violations,new_critical_violations,new_major_violations,coverage,new_coverage,skipped_tests,test_errors,test_failures,tests,ncloc'
    def desiredMetricsListStr = DEFAULT_DESIRED_METRICS
    def RegExPattern4AdditionalMetrics = /([a-zA-Z_]+,)*([a-zA-Z_]+)/
    def additionalMetrics = config.additionalMetrics
    def returnMetricsMap = [:]

    if (!additionalMetrics.empty) {
      if (!(additionalMetrics ==~ RegExPattern4AdditionalMetrics)) {
        jenkins.echo "additionalMetrics=" + additionalMetrics
        jenkins.error "Error...additionalMetrics can only contain alphabetic underscore and comma characters and each metric separated by comma, cannot be duplicates"
      }
    }

    if (!additionalMetrics.empty) {
      desiredMetricsListStr += "," + additionalMetrics
    }

    jenkins.echo 'isPreview: ' + config.isPreview

    if (config.isPreview || getPreview()) {
      jenkins.echo 'This Sonar Scan is for Preview mode'
    } else {
      def branchName = config.branchName
      jenkins.echo "getSonarMetrics arguments with branchname: $config"
      def sonarProjectKey = OptumFileReader.getProjectKey(jenkins)

      if (branchName != null) {
        sonarProjectKey = sonarProjectKey + "&branch=" + branchName
      }

      def desiredMetricsList = desiredMetricsListStr.split(',')
      def sonarMetricApiURL = config.sonarHostUrl + config.sonarMetricsApiBaseURL + "?componentKey=" + sonarProjectKey + '&metricKeys=' + desiredMetricsListStr

      // make call to sonar API using curl
      def metricJson = jenkins.sh returnStdout: true, script: "curl -u $SONAR_API_TOKEN: \'$sonarMetricApiURL\'"
      jenkins.echo "metricJson value:  + $metricJson"
      // metricJson
      // sample output:
      //('{"component":{"id":"AVai3ZLeOJ_mgqaQbPR5","key":"ClaimsAdmin_GatewayMVC:dev","name":"GatewayMVC dev","qualifier":"TRK","measures":[{"metric":"new_blocker_violations","periods":[{"index":1,"value":"22"},{"index":2,"value":"22"},{"index":3,"value":"22"}]},{"metric":"critical_violations","value":"23","periods":[{"index":1,"value":"-96"},{"index":2,"value":"9"},{"index":3,"value":"-55"}]},{"metric":"new_critical_violations","periods":[{"index":1,"value":"10"},{"index":2,"value":"9"},{"index":3,"value":"18"}]},{"metric":"major_violations","value":"3134","periods":[{"index":1,"value":"-137"},{"index":2,"value":"190"},{"index":3,"value":"-642"}]},{"metric":"new_major_violations","periods":[{"index":1,"value":"350"},{"index":2,"value":"228"},{"index":3,"value":"1341"}]},{"metric":"blocker_violations","value":"61","periods":[{"index":1,"value":"0"},{"index":2,"value":"22"},{"index":3,"value":"61"}]}]}}')

      // convert JSON result to filtered list of measures
      def jsonSlurper = new JsonSlurperClassic()
      def jsonSlurperResponse
      def resultMetricsList

      try {
        jsonSlurperResponse = jsonSlurper.parseText(metricJson)

        //if we got error report it
        //{"errors":[{"asdfmsg":"The following metric keys are not found: asdf"}]}
        if (jsonSlurperResponse.errors) {
          throw new Exception(jsonSlurperResponse.toString())
        }

        resultMetricsList = jsonSlurperResponse.component.measures
        if (!resultMetricsList) {
          jenkins.error "Error...Metrics data not found for: try this URL manually\n${sonarMetricApiURL}"
        }
      }
      catch (Exception e) {
        jenkins.error "Error...JSON parsing/ no metric data: try this URL manually\n${sonarMetricApiURL}\n" + e.toString()
      }

      // prepare name-value pairs (as Map) of metrics that we are interested in
      for (desiredMetric in desiredMetricsList) {
        def value = "-1"     // -1 value instead of 'missing' enable us to graph out the data
        for (resultMetricItem in resultMetricsList) {
          if (resultMetricItem.metric == desiredMetric) {
            value = resultMetricItem.value
            if (resultMetricItem.value == null) {  //take value from inside array at index1
              value = resultMetricItem.periods[0].value
            }//if
            break
          }//if
        }//for
        returnMetricsMap[desiredMetric] = value
      }
    }
    return returnMetricsMap
  }

  /**
   * Retrieves sonar metrics using API call to sonar, converts into a simple map of key value
   * e.g. {blocker_violations=39, critical_violations=14}* Following metrics will be retrieved by default:
   *              "blocker_violations,critical_violations,major_violations,new_blocker_violations,new_critical_violations,new_major_violations,coverage,new_coverage,skipped_tests,test_errors,test_failures"
   * @param additionalMetrics You can specify additional metrics to get information on
   * @param sonarQualityGateApiBaseURL The URL where sonar metrics
   */
  def getSonarQualityGate(Map<String, Object> params) {

    def defaults = [
      sonarHostUrl          : Constants.SONAR_HOST_URL,
      sonarQualityGateApiBaseURL: '/api/qualitygates/get_by_project',
      additionalMetrics     : '',
      isPreview             : false,
      isDebugMode           : false
    ]

    def config = defaults + params

    if (!config.scmRepoUrl) {
      Git gitInfo = new Git(jenkins)
      config.branchName = gitInfo.getBranch()
    }

    def returnQualityGate = [:]
    def resultQualityGate = '-9'

    jenkins.echo 'isPreview:' + config.isPreview
    if (config.isPreview || getPreview()) {
      jenkins.echo 'This Sonar Scan is for Preview mode'
      returnQualityGate['qualityGate'] = '-4'     // For preview mode
    } else {
      def branchName = config.branchName
      jenkins.echo "getSonarMetrics arguments with branchname: $config"
      def sonarProjectKey = OptumFileReader.getProjectKey(jenkins)
      if (branchName != null) {
        sonarProjectKey = sonarProjectKey + "&branch=" + branchName
      }
      def sonarQualityGateApiURL = config.sonarHostUrl + config.sonarQualityGateApiBaseURL + "?project=" + sonarProjectKey

      // make call to sonar API using curl
      def metricJson1 = jenkins.sh returnStdout: true, script: "curl -u $SONAR_API_TOKEN: \'$sonarQualityGateApiURL\'"

      // metricJson
      // sample output:
      //{"qualityGate":{"id":"35","name":"GATE_05","default":false}}

      // convert JSON result to filtered list of measures
      def jsonSlurper = new JsonSlurperClassic()   // JsonSlurper is not serializable
      def jsonSlurperResponse
      def resultMetricsList

      try {
        jsonSlurperResponse = jsonSlurper.parseText(metricJson1)

        //if we got error report it
        //{"errors":[{"msg":"Component key '454' not found"}]}
        if (jsonSlurperResponse.errors) {
          throw new Exception(jsonSlurperResponse.toString())
        }

        resultMetricsList = jsonSlurperResponse.qualityGate

        if (!resultMetricsList) {
          jenkins.error "Error...Quality Gate information not found: try this URL manually\n" + sonarQualityGateApiURL
        } else {
          if (resultMetricsList.containsKey('name')) {
            switch (resultMetricsList.name) {
              case 'GATE_00':
                resultQualityGate = '0'
                break
              case 'GATE_01':
                resultQualityGate = '1'
                break
              case 'GATE_02':
                resultQualityGate = '2'
                break
              case 'GATE_03':
                resultQualityGate = '3'
                break
              case 'GATE_04':
                resultQualityGate = '4'
                break
              case 'GATE_05':
                resultQualityGate = '5'
                break
              case 'GATE_06':
                resultQualityGate = '6'
                break
              case 'GATE_07':
                resultQualityGate = '7'
                break
              case 'GATE_08':
                resultQualityGate = '8'
                break
              case 'GATE_09':
                resultQualityGate = '9'
                break
              case 'ADOPTION':
                resultQualityGate = '-1'
                break
              case 'GATE_EXEMPT':
                resultQualityGate = '-2'
                break
              case 'GATE_REDZONE':
                resultQualityGate = '-3'
                break
              default:
                resultQualityGate = '-9'
                break
            }//switch
          }//if
        }
      }
      catch (Exception e) {
        jenkins.error "Error...JSON parsing no Quality Gate data: try this URL manually\n${sonarQualityGateApiURL}\n" + e.toString()
      }
      returnQualityGate['qualityGate'] = resultQualityGate
    }
    return returnQualityGate
  }

/**
 * Internal method for creating Sonar properties based on arguments and the current build environment.
 * The sonar project name will be set based upon the first occurance of the following:
 *  1) The Optumfile.yml file contains the projectName
 *  2) The 'sonar.projectKey' property is set from the Optumfile.yml
 *  3) The 'name' element is found in the POM OR
 *  4) The 'groupId' and 'artifactId' in the POM
 * @param config configuration from the caller
 * @param useMaven indicates if Maven should be used as a fall-back to determine Sonar project name
 */
  def createProps(config, useMaven = false) {
    jenkins.echo "createProps arguments: $config"

    setPreview(false)
    Git gitInfo = new Git(jenkins)
    if (!config.scmRepoUrl) {
      config.scmRepoUrl = gitInfo.getRemoteUrl()
      config.branchName = gitInfo.getBranch()
    } else {    // config.branchName is not set otherwise
      if (!config.branchName) {
        jenkins.echo "The scmRepoUrl parameter is provided but branchName parameter is not, we'll use gitInfo.getBranch() and attempt to retrieve branchName"
        config.branchName = gitInfo.getBranch()
      }
    }

    def sonarProjectVersion = null
    def pom = null

    // determine the sonarProjectVersion
    if (useMaven && !config.sonarProjectVersion) {
      jenkins.echo "Setting the sonar.projectVersion from the pom.version element"
      pom = jenkins.readMavenPom file: (config.pomFile ? config.pomFile : "pom.xml")
      sonarProjectVersion = pom.'version'
    } else {
      sonarProjectVersion = config.sonarProjectVersion
    }

    if (!sonarProjectVersion) {
      jenkins.echo "Since the pom.version element was empty, setting the sonar.projectVersion using the displayName of the current Jenkins Build"
      sonarProjectVersion = jenkins.currentBuild.displayName
    }
    def sonarProps = "-Dsonar.projectVersion=${sonarProjectVersion} " +
      "-Dsonar.links.scm=${config.scmRepoUrl} " +
      "-Dsonar.links.ci=${jenkins.env.JOB_URL} " +
      "-Dsonar.projectKey=" + OptumFileReader.getProjectKey(jenkins) + " " +
      "-Dsonar.projectName=" + OptumFileReader.getProjectFriendlyName(jenkins) + " "

    if (config.sonarExclusions) {
      sonarProps += "-Dsonar.exclusions=${config.sonarExclusions} "
    }
    if (config.sonarCoverageExclusions) {
      sonarProps += "-Dsonar.coverage.exclusions=${config.sonarCoverageExclusions} "
    }

    def jobName = jenkins.env.JOB_NAME.tokenize('/').last()
    def branchName = config.branchName
    if (branchName != config.mainBranchName) {
      sonarProps += "-Dsonar.branch.name=${branchName} "
    }

    if (config.additionalProps) {
      for (def entry : config.additionalProps) {
        if ((entry.value).equalsIgnoreCase('true') || (entry.value).equalsIgnoreCase('false')){
          // Do not put in quote for boolean case
          sonarProps += "-D$entry.key=$entry.value "
        } else {
          sonarProps += "-D$entry.key=\"$entry.value\" "
        }
      }
    }

    jenkins.echo 'sonarProps: ' + sonarProps

    if (jobName.startsWith("PR-")) {
      jenkins.echo "This Sonar Scan is for Pull Request ${jobName}"
      setPreview(true)
      def tokens = gitInfo.remoteUrl.startsWith('git@') ? '@:/' : '/'
      def repoTokens = gitInfo.remoteUrl.tokenize(tokens)
      def githubRepo = "${repoTokens[repoTokens.size() - 2]}/${repoTokens.last()}".replace('.git', '')
      def pullRequestId = jobName.split('-')[1]

      sonarProps += '-Dsonar.analysis.mode=preview ' +
        "-Dsonar.branch.target=${jenkins.env.CHANGE_TARGET} "

      // The credentialId is optional, don't populate the relevant sonar properties if credentialId not provided
      if(config.gitUserCredentialsId){
        // The credentialId user MUST have 'Write' access to the GitHub repository
        jenkins.withCredentials([jenkins.string(credentialsId: config.gitUserCredentialsId, variable: 'githubOauth')]) {
          sonarProps += "-Dsonar.github.oauth=${jenkins.env.githubOauth} " +
            "-Dsonar.github.pullRequest=${pullRequestId} " +
            "-Dsonar.github.repository=${githubRepo} " +
            '-Dsonar.github.endpoint=https://github.optum.com/api/v3 '
          jenkins.echo 'sonarProps: ' + sonarProps
        }
      }
    }

    // For explicit preview mode
    if (sonarProps.contains('-Dsonar.analysis.mode="preview"')) {
      jenkins.echo 'Set Preview mode to true'
      setPreview(true)
    }
    return sonarProps
  }

/**
 * Wrapper for backwards compatibility.
 */
  def scanWithMaven(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanWithMaven(config)
  }

/**
 * Wrapper for backwards compatibility.
 */
  def scanWithGradle(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanWithGradle(config)
  }

/**
 * Wrapper for backwards compatibility.
 */
  def scanWithSonarScanner(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanWithSonarScanner(config)
  }

/**
 * Wrapper for backwards compatibility.
 */
  def scanWithNpm(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanWithNpm(config)
  }

/**
 * Wrapper for backwards compatibility.
 */
  def scanUsingSonarScannerWithPropertiesFile(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanUsingSonarScannerWithPropertiesFile(config)
  }


/**
 * Wrapper for backwards compatibility.
 */
  def scanWithSonarScannerDotnetCore(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    scanWithSonarScannerDotnetCore(config)
  }

  def sendSonarEvent(jenkins, Date processStart, EventStatus status, String scanTool){
    new SonarEvent(jenkins, [duration: new Utils(jenkins).getDuration(processStart).toString(), status: status, scanTool: scanTool, isPreview: getPreview()]).send()
  }

  def validateInputParameters(Map<String, Object> params) {
    if ( params.containsKey("projectKey") || params.containsKey("projectName")) {
      throw new SonarInvalidParameterException("ERROR: Do not pass projectKey or projectName in the config parameters. They should be set in the Optumfile.yml")
    }
  }
}
