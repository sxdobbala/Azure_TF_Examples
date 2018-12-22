import com.optum.jenkins.pipeline.library.angular.AngularCli

/**
 * Builds Angular JS application using Angular Cli and NodeJs
 *

 * Refer to for Docker template configuration
 
 * @param angularCliVersion String Version of Angular Cli. Environment specific values are injected. Required.
 * @param buildForEnvironment String Build for which environment. Environment specific values are injected. Required.
 * @param additionalNgOptions String Additional arguments for ng command. Environment specific values are injected. Required.
 * @param additionalNpmOptions String Additional arguments for npm install command. Environment specific values are injected. Required.
 * @param useCache boolean Whether to use NPM Cache for the build or not. Defaults to true. Saves quite a bit of time.
 *                                Refer to for Docker template configuration
 *                                https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins/jenkins_mixin_angularcli
 *
 *  The following passed in parameters
 *         angularCliVersion = "1.7.3"
 *         buildForEnvironment = "prod"
 *         additionalNpmOptions = "--verbose"
 *         useCache = true
 * Will result in the following commands
 *            npm-cache install --verbose
 *            ng build --environment=prod
 * */

def call(Map<String, Object> config){
  AngularCli angularcli = new AngularCli(this)
  angularcli.buildAngularAppWithCli(config)
}
