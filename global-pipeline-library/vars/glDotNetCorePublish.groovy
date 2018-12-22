import com.optum.jenkins.pipeline.library.dotnet.DotNetCore

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

def call(Map<String, Object> config){
  DotNetCore dotNetCore = new DotNetCore(this)
  dotNetCore.publishDotNetCoreApp(config)
}
