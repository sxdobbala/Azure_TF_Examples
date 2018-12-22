#!/usr/bin/env groovy
import com.optum.jenkins.pipeline.library.dotnet.DotNetStd

/**
 * Runs tests or C# application using .Net Standard version
 * @param testContainerFile String file that contains tests  Example: "HelloWorldJenkinsExample/HelloWorldJenkinsExample.Tests/bin/Debug/HelloWorldJenkinsExample.Tests.dll".
 * @param projectFile String Solution file name including path Example:"HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln".
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * <p>
 * Here is example calls to this method
 * <ul>
 *      <li>testDotNetStdApp( projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln", buildConfiguration : "Release",testContainerFile:     "HelloWorldJenkinsExample/HelloWorldJenkinsExample.Tests/bin/Release/HelloWorldJenkinsExample.Tests.dll" )</li>
 * </ul>
 * <p>
 * The following passed in parameters
 * Will result in the following commands
 *         nuget restore <params>
 *         MsBuild <params>
 *         MSTest /testcontainer:<PathToFileContainingTests>
 *
 * */
def call(Map<String, Object> config){
  DotNetStd dotNetStd = new DotNetStd(this)
  dotNetStd.testDotNetStdApp(config)
}
