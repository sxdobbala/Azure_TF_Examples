import com.optum.jenkins.pipeline.library.angular.AngularCli

/**
 * Unit Test Angular JS application using node JS.
 *
 * @param angularCliVersion String Version of Angular Cli. Environment specific values are injected. Required.
 * @param generateCodeCoverage String Whether to generate code coverage or not
 *
 *  The following passed in parameters
 *         generateCodeCoverage = true
 * Will result in the following Node JS commands
 *         ng test --single-run --code-coverage
 * */

def call(Map<String, Object> config){
  AngularCli angularcli = new AngularCli(this)
  angularcli.testAngularAppWithCli(config)
}
