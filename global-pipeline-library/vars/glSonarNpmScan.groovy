import com.optum.jenkins.pipeline.library.sonar.Sonar

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
 * @param gitUserCredentialsId The ID for Git credentials stored in Jenkins as a String/Secred Text. This is only used
 *          publishing reuslts to GitHub for pull requests. See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 *          for info on how to set up the Personal Token / OAuth key.
 * @param sonarExclusions Exclude the code that matches the pattern from both Sonar Rule Violation scans and The Unit
 *          Test Code Coverage. Defaults to reading excludes from Maven POM.
 * @param sonarCoverageExclusions Only exclude code that matches the pattern from the Unit Test Code Coverage. Defaults
 *          to reading excludes from Maven POM.
 * @param branchName The name of the branch. Defaults to reading from Git.
 * @param additionalProps An optional map of any additional properties that should be set.
 */

def call(Map<String, Object> config){
  Sonar sonar = new Sonar(this)
  sonar.scanWithNpm(config)
}
