import com.optum.jenkins.pipeline.library.dotnet.DotNetStd


/**
 * Builds C# application using .Net Standard Version.
 * @param projectFile String Solution file name including path Example:"HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln".
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * <p>
 * Here is example calls to this method
 * <ul>
 *      <li>buildDotNetStdApp( projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln", buildConfiguration : "Release")</li>
 * </ul>
 * <p>
 *
 * The following passed in parameters
 * Will result in the following commands
 *         nuget restore <params>
 *         MsBuild <params>
 *
 * */

def call(Map<String, Object> config){
  DotNetStd dotNetStd = new DotNetStd(this)
  dotNetStd.buildDotNetStdApp(config)
}
