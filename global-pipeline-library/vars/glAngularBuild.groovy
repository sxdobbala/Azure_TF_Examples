import com.optum.jenkins.pipeline.library.angular.Angular

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

def call(Map<String, Object> config){
  Angular angular = new Angular(this)
  angular.buildAngularApp(config)
}
