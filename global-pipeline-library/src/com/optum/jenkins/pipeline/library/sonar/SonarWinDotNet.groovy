package com.optum.jenkins.pipeline.library.sonar

import com.optum.jenkins.pipeline.library.utils.Utils
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import com.optum.jenkins.pipeline.library.utils.Constants



class SonarWinDotNet implements Serializable {
  def jenkins
  def startTime

  SonarWinDotNet() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  SonarWinDotNet(jenkins) {
    this.jenkins = jenkins
    startTime = new Date()
  }

/**
 * Runs Sonar scan using MSBuild.
 * <p>
 * It is important that you pick a descriptive product and project name so that it is clear what product the projects
 * belong to and the project name comes from optumfile.yml<br/>
 * Here is a example calls to this method
 * <ul>
 *      <li>glDotNetSonarScan( sonarScannerPath : "F:/Build/Agent1/tasks/SonarQubeScannerMsBuildBegin/2.0.0/SonarQubeScannerMsBuild", projectToScan : "https://github.optum.com/IDWS/idws-dotnet-poc.git", projectFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample/HelloWorldJenkinsExample.csproj", testContainerFile : "HelloWorldJenkinsExample/HelloWorldJenkinsExample.Tests/bin/Debug/HelloWorldJenkinsExample.Tests.dll")</li>    
 * </ul>
 *
 *
 * @param sonarHostUrl Url to Sonar. Defaults to Constants.SONAR_HOST_URL.
 * @param sonarLogin Login for Sonar. Defaults to Constants.SONAR_LOGIN.
 * @param projectFile String Solution file name including path. Example:"HelloWorldJenkinsExample/HelloWorldJenkinsExample.sln"
 * @param testContainerFile String file that contains tests.  
 * @param sonarScannerPath String that implies the path where sonar scanner is installed.
 * @param projectKey String that will be present in Optum.yml file.
 * @param projectName String that will be present in Optum.yml file which indicates the name of the project that was analyzed.
 * @param projectToScan String indicates the name of the project that needs to be analyzed.
 * @param sonarOptionalParams String if provided codeCoverage action executes.
 * @param CodeCoverageExecutablePath String  implies the path where codeCoverage.exe is installed.
 * @param CoverageFile String implies coverage file name along with absolute path.
 * @param VSTestConsolePath String implies the path where vstest.console.exe is installed.
 * @CovergaeXMLFile String  String implies the coveragexml file name along with absolute path where it needs to be saved.
 *
 */


def scanWithSonarScannerWin(Map<String, Object> params){
	def defaults = [

	sonarHostUrl				: Constants.SONAR_HOST_URL,  
    sonarLogin				    : Constants.SONAR_LOGIN,     //Required
	sonarScannerPath			: null,  //Required
	projectKey					: OptumFileReader.getProjectKey(jenkins),
	projectName					: OptumFileReader.getProjectFriendlyName(jenkins),
	projectToScan				: null,  //Required
	projectFile					: null,  //Required
	testContainerFile			: "",   //Optional
	sonarOptionalParams			: "",     //Optional
	CodeCoverageExecutablePath 	: "",   //Optional
	CoverageFile 				: "",   //Optional
	VSTestConsolePath  			: "",   //Optional
	CoverageXMLFile 			: "",    //Optional
	settingsFile				: "",	//Optional
	
	
	
    ]
	def config = defaults + params
	if ((config.sonarScannerPath == null ) || ( config.projectToScan == null) || (config.projectFile == null)){
		jenkins.echo " sonarScannerPath	: ${config.sonarScannerPath}"
		jenkins.echo " projectToScan	: ${config.projectToScan}"
		jenkins.echo " projectFile	: ${config.projectFile}"
		
		
		throw new Exception( " sonarScannerPath or projectToScan or projectFile is/are missing " )

	} 

	jenkins.echo "scanWithSonarScannerWin aurguments : ${config}"

	// Starting of sonar analysis for the project that will be scanned	
      
	def sonarBegin = "${config.sonarScannerPath}\\MSBuild.SonarQube.Runner.exe begin /k:\"${config.projectKey}\" /n:\"${config.projectName}\" /v:\"1.0\" /d:sonar.host.url=\"${config.sonarHostUrl}\" /d:sonar.login=\"${config.sonarLogin}\"  /d:scmRepoUrl=\"${config.projectToScan}\" ${config.sonarOptionalParams}"
	jenkins.echo "Running $sonarBegin"
	jenkins.command(sonarBegin)

	//MSBuid for sonar analysis
	def dotNetBuild = "MSBuild ${config.projectFile} /t:Rebuild"	
	jenkins.echo "Running $dotNetBuild"
	jenkins.command(dotNetBuild)
	
    
    //CodeCoverage for Sonar Analysis	
			
	if  ( ! config.sonarOptionalParams.isEmpty() ){
		
		def codeCoverage = "\"${config.VSTestConsolePath}\\vstest.console.exe\" ${config.testContainerFile} /Settings:\"${config.settingsFile}\" /InIsolation /Logger:trx"
			jenkins.command(codeCoverage)	
		
		def batchCommand = "For /R TestResults %%G IN (*.coverage) do copy \"%%G\" \"${config.CoverageFile}\" /Y"
			jenkins.command(batchCommand)
		def codeCoverageAnalyse = "\"${config.CodeCoverageExecutablePath}\\CodeCoverage.exe\" analyze /output:\"${config.CoverageXMLFile}\" \"${config.CoverageFile}\"" 
			jenkins.command(codeCoverageAnalyse)
		
	}
	
	//End of sonar analysis.
	def sonarEnd = "${config.sonarScannerPath}\\MSBuild.SonarQube.Runner.exe end /d:sonar.login=\"${config.sonarLogin}\""
	jenkins.echo "Running $sonarEnd"	
	jenkins.command(sonarEnd)

}
}
