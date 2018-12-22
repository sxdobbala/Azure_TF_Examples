import com.optum.jenkins.pipeline.library.sonar.Sonar

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
 * @param sonarTool The name of the Sonar tool instance configured in Jenkins. See Manage Jenkins > Global Tool
 *          Configuration > SonarQube Scanner. Defaults to 'sonar'.
 * @param sonarServer The name of the Sonar server instance configured in Jenkins. See Manage Jenkins > Configure
 *          System > SonarQube configuration. Defaults to 'Sonar OCD'.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param additionalProps An optional map of any additional properties that should be set.
 */
def call(Map<String, Object> config){
  Sonar sonar = new Sonar(this)
  sonar.scanUsingSonarScannerWithPropertiesFile(config)
}
