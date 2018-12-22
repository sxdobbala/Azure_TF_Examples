package com.optum.jenkins.pipeline.library.sca

import com.cloudbees.groovy.cps.NonCPS
import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.WorkflowBasicSteps
import com.optum.jenkins.pipeline.library.utils.Utils


class FortifyWin implements Serializable {
  Object jenkins

  FortifyWin() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  FortifyWin(jenkins) {
    this.jenkins = jenkins
  }

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
  def fortifyScanWin(Map<String, Object> params){

    def defaults = [
         fortifyFilePath  : null,  //required
         projectPath      : null  //required
      ]

      def config = defaults + params
      def startTime = new Date()

      if ((config.fortifyFilePath == null ) || ( config.projectPath == null )) {
		throw new Exception("fortifyFilePath or projectpath is/are missing")
      }
      //Execution of fortify bat file.
      jenkins.echo "fortifyScanWin arguments: $config"
      def fortifyCmd="${config.fortifyFilePath} ${config.projectPath}"
      jenkins.echo "Running $fortifyCmd"
        jenkins.command(fortifyCmd)
      }

  }
