import com.optum.jenkins.pipeline.library.sonar.SonarWinDotNet

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
 *
 */

def call(Map<String, Object> config){
  SonarWinDotNet sonar = new SonarWinDotNet(this)
  sonar.scanWithSonarScannerWin(config)
}
