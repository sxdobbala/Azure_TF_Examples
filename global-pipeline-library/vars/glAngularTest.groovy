import com.optum.jenkins.pipeline.library.angular.Angular

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

def call(Map<String, Object> config){
  Angular angular = new Angular(this)
  angular.testAngularApp(config)
}
