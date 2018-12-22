import com.optum.jenkins.pipeline.library.dotnet.DotNetCore


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
def call(Map<String, Object> config){
  DotNetCore dotNetCore = new DotNetCore(this)
  dotNetCore.testDotNetCoreApp(config)
}
