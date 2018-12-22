package com.optum.jenkins.pipeline.library.compliance.services

import com.cloudbees.groovy.cps.NonCPS
import hudson.FilePath
import jenkins.model.Jenkins

class FileSaver {
  @NonCPS
  static void saveFile(Object jenkins, String relativePath, byte[] content) {
    try {
      InputStream is = new ByteArrayInputStream(content)
      FilePath inputFilePath = new FilePath(
        Jenkins.getInstance().getComputer(jenkins.env.NODE_NAME as String).getChannel(),
        (jenkins.env.WORKSPACE as String) + '/' + relativePath)
      inputFilePath.copyFrom(is)
      is.close()
    } catch (IOException ex) {
      jenkins.error('Unexpected error while creating file:  ' + ex.getMessage() + ex.getCause())
    }
  }
}
