
/**
 * A helper method to execute a sh command (Unix).
 * Every call to command.groovy will source the jenkins.sh file which enables the mixins to work that have
 * been configured in your Jenkins instance.  Since the command.groovy class is in the "vars" directory,
 * every class implicitly has access to the command class.
 *
 * Example of usage:
 *   def mvnCmd = "mvn clean install"
 *   command mvnCmd
 *
 * @param cmd String The command to execute
 * @param returnStdout set to true to have this function return the result of the passed in command
 */
def call(String cmd, Boolean returnStdout = false, String shabang = '#!/bin/sh -ex') {
  try {
    if (isUnix()) {
      sh(returnStdout: returnStdout, script: "${shabang} \nif [ \"\${JENKINS_JPAC_USECUSTOMSLAVE}x\" == \"x\" ]; then export JENKINS_JPAC_USECUSTOMSLAVE='false'; fi\n\${JENKINS_JPAC_USECUSTOMSLAVE} || . /etc/profile.d/jenkins.sh > /dev/null 2>&1 \n${cmd}")?.trim()
    } else {
      if (returnStdout) {
        bat(returnStdout: returnStdout, script: "@echo off && ${cmd}").trim()
      } else {
        bat cmd
      }
    }
  } catch (Exception e) {
    // Display full cmd string exposes credential information if exists in cmd.  Display only the first 2 words of the string
    // value1 is a tool while value2 is the command, ex. oc login where value1=oc and value2=login
    // will work for single command and display the failed command.  Multiple commands only return the first command
    def (value1, value2) = cmd.split()
    throw new RuntimeException("Executing command '${value1} ${value2} ... ' on failed.  ", e)
  }
}
