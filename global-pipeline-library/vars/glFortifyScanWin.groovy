import com.optum.jenkins.pipeline.library.sca.FortifyWin

/**
 *
 * This class allows you to create a Fortify Scan.
 *
 * @param projectPath is path for the Project. Example: HelloWorldJenkinsExample.
 * @param fortifyFilePath is the path for FortifyFile. Example: "HelloWorldJenkinsExample\HelloWorldJenkinsExample\FortifyHelloWorldJenkinsExample.bat".
 *
 *
 * Example : "HelloWorldJenkinsExample\HelloWorldJenkinsExample\FortifyHelloWorldJenkinsExample.bat HelloWorldJenkinsExample\"
 */

def call(Map<String, Object> config){
  FortifyWin fortifyWin = new FortifyWin(this)
  fortifyWin.fortifyScanWin(config)
}
