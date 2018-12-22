#!/usr/bin/env groovy
import com.optum.jenkins.pipeline.library.dotnet.DotNetStd

/**
 * Publish C# application using .Net Standard Version.
 * @param projectFile String Project file name including path  Example:"HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln"
 * @param testContainerFile String file that contains tests  Example: "HelloWorldJenkinsExample/HelloWorldJenkinsExample.Tests/bin/Debug/HelloWorldJenkinsExample.Tests.dll".
 * @param buildConfiguration String Build for which buildConfiguration (Release or Debug).
 * @param PublishProfile String is for Publish Example: Dev.pubxml
 * <p>
 * Here are 2 examples calls to this method 1 --> without testcases  2 --> with testcases
 * <ul>
 *      <li>publishDotNetStdApp( projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln", buildConfiguration : "Release", deployBuild : "true", PublishProfile : "Dev.pubxml",     testsRun : "false")</li>
 *      <li>publishDotNetStdApp( projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln", buildConfiguration : "Release", deployBuild : "true", PublishProfile : "Dev.pubxml",    testContainerFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.Tests/bin/Release/HelloWorldJenkinsExample.Tests.dll" )</li>
 * </ul>
 * <p>
 * The following passed in parameters
 * Will result in the following commands with tests
 *         nuget restore <params>
 *         MsBuild <params>
 *         MSTest /testcontainer:<PathToFileContainingTests>
 *
 * The following passed in parameters
 * Will result in the following commands without tests
 *         nuget restore <params>
 *         MsBuild <params>
 *
 * */

def call(Map<String, Object> config){
  DotNetStd dotNetStd = new DotNetStd(this)
  dotNetStd.publishDotNetStdApp(config)
}
