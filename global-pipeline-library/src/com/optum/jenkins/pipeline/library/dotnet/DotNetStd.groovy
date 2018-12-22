#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.dotnet

class DotNetStd implements Serializable {
  def jenkins

  DotNetStd() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  DotNetStd(jenkins) {
    this.jenkins = jenkins
  }
/**
 * NugetRestores dependencies for C# application using .Net Standard Version. This routine should be
 * called from either build, publish or test functions since it requires environment to be setup
 *
 * @param projectFile String Solution file name including path Example:"HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln".
 * <p>
 * Here is example calls to this method
 * <ul>
 *      <li>nugetRestoreDotNetStdDependencies( projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln")</li>
 * </ul>
 * <p>
 *
 *  The following passed in parameters
 *
 * Will result in the following commands
 *         nuget restore
 * */

def nugetRestoreDotNetStdDependencies(Map<String, Object> params) {
    def defaults = [
	projectFile : null //required
    ]
    def config = defaults + params
    if( config.projectFile == null){
    	throw new Exception("projectFile is missing")
    }
    jenkins.echo "nugetRestoreDotNetStdDependencies arguments: $config"
    def nugetCmd="nuget restore ${config.projectFile}"
    jenkins.echo "Running $nugetCmd"
    jenkins.command(nugetCmd)
}

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

def buildDotNetStdApp(Map<String, Object> params) {
    def defaults = [
	projectFile		: null, //required
	buildConfiguration	: "Release"


    ]
    def config = defaults + params
    if ((config.projectFile == null)) {
	throw new Exception("projectFile is missing")
    }
    jenkins.echo "buildDotNetStdApp arguments: $config"
    def buildProj = "MSBuild ${config.projectFile} /p:Configuration=${config.buildConfiguration}"

    nugetRestoreDotNetStdDependencies(config)

    jenkins.echo "Running $buildProj"
    jenkins.command(buildProj)

}
/**
 * Copying the Artifacts C# application using .Net Standard Version.
 * @param sourceFolder String sourceFolder name indicates the path from where the artifacts are copied from Example:"F:\\Jenkins\\_work\\workspace\\idws\\Legacy\\web-ui\\IdwsWebUI\\Scripts\\vendors\\jQuery-Smart-Wizard". 
 * @param targetFolder String targetFolder name indicates the path to where the artifacts are copied to Example:"WEBED0939\\IDWS_Future_WebUI\\Scripts\\Vendors\\jQuery-Smart-Wizard".
 * @param  buildArtifact String indicates the name of the file/folder to be copied
 * <p> 
 * Here is example calls to this method 
 * <ul> 
 *      <li>CopyArtifactsDotNetStdApp( glDotNetStdCopyArtifacts( sourceFolder : "F:\\Jenkins\\_work\\workspace\\idws\\Legacy\\web-ui\\IdwsWebUI\\Scripts\\vendors\\jQuery-Smart-Wizard" , buildArtifact : "", targetFolder : "\\\\WEBED0939\\IDWS_Future_WebUI\\Scripts\\Vendors\\jQuery-Smart-Wizard" ))</li>  
 * </ul>  
 * <p> 
 *
 * The following passed in parameters
 * Will result in the following commands
 *         xcopy <params>
 *         
 *           
 * */

def CopyArtifactsDotNetStdApp(Map<String, Object> params) {
    def defaults = [
	sourceFolder    	: null, //required
	targetFolder 		: null, //required
	buildArtifact	    : "" //optional
    ] 
   def config = defaults + params
    if ((config.sourceFolder == null || config.targetFolder == null)) {
	throw new Exception("source or targetFolder is missing")	
    }
    jenkins.echo "CopyArtifactsDotNetStdApp arguments: $config"
    
	def artifactsCmd = "xcopy /y /E ${config.sourceFolder}${config.buildArtifact} ${config.targetFolder}"
    jenkins.echo "Running $artifactsCmd"
    jenkins.command(artifactsCmd)
}
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
 *         MSTest /testcontainer:<PathToFileContainingTests>
 *
 * */

def testDotNetStdApp(Map<String, Object> params) {
    def defaults = [
	testContainerFile	: null, //required
	projectFile 		: null, //required
	buildConfiguration	: "Release"
    ]
   def config = defaults + params
    if ((config.testContainerFile == null)) {
	throw new Exception("File that contains tests is missing")
    }


    jenkins.echo "testDotNetStdApp arguments: $config"
    def testsCmd = "MSTest /testcontainer:${config.testContainerFile}"
    jenkins.echo "Running $testsCmd"
    jenkins.command(testsCmd)
}

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

def publishDotNetStdApp(Map<String, Object> params) {
    def defaults = [
	projectFile		: null, //required
	buildConfiguration	: "Release",
	deployBuild		: "true",
        PublishProfile		: null, //required
	testsRun		: "true",
        testContainerFile	: null  //required
    ]
    def config = defaults + params
    if ((config.projectFile == null) || (config.PublishProfile == null)) {
	throw new Exception("projectFile or PublishProfile is missing")
    }
    jenkins.echo "publishDotNetStdApp arguments: $config"
    def publishProj = "MSBuild ${config.projectFile} /p:Configuration=${config.buildConfiguration} /p:DeployOnBuild=${config.deployBuild} /p:PublishProfile=${config.PublishProfile}"

    nugetRestoreDotNetStdDependencies(config)


    jenkins.echo "Running $publishProj"

    if ((config.testsRun == "true") && (config.testContainerFile != null)){
	def testsCmd ="MSTest /testcontainer:${config.testContainerFile}"
	jenkins.echo "Running $testsCmd"
	jenkins.command(testsCmd)
    } else if ((config.testsRun == "true") && (config.testContainerFile == null)) {
	throw new Exception("testContainerFile is missing")
    }
    jenkins.command(publishProj)
}
}
