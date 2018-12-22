package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.WorkflowBasicSteps

class JenkinsfileReader {
  private static final String JENKINSFILE_ENV_VAR = "JENKINSFILE_LOCATION"

  static Library[] getLibraries(jenkins) {
    def libs = []
    if (jenkins.env.jenkinsLibraries != null && jenkins.env.jenkinsLibraries != "" && jenkins.env.jenkinsLibraries != [] && jenkins.env.jenkinsLibraries != "[]") {
      jenkins.echo "Info: Getting jenkins libraries from jenkins env"
      def librariesForEnv = jenkins.env.jenkinsLibraries.toString().replace('[','').replace(']','').split(",")
      librariesForEnv.each {
        libs.add(new Library(id: it.split(":")[0].trim(), version: it.split(":")[1].trim()))
      }
      return libs
    }
    def jenkinsfile = getJenkinsfile(jenkins)
    // \s for cases of tab or space issue before @Library declaration
    // For all instances with @version
    def libraries = (jenkinsfile =~ /\n[\s]*@Library\(["']+([^)^@]+)@([^)^@]+)["']+\).*/)
    // Library in one line format like @Library(["com.optum.jenkins.abc@master", "com.optum.jenkins.xyz@0.1.0"]) _ or
    // Library spread to multiple lines formatting, pull these in with \s\S
    def librariesString = (jenkinsfile =~/\n[\s]*@Library\(\[["'][\s\S]*]\)/)
    def librariesCollection
    if (librariesString.getCount() > 0) {
      librariesCollection = (librariesString.collect() =~ /["']+([^)^@^,]+)@([^)^@,]+)["']+/)

      if (librariesCollection.getCount() > 0) {
        librariesCollection.collect { libs.add(new Library(id: it[1], version: it[2])) }
      }
    }
    if (libraries.hasGroup()) {
      libraries.collect { libs.add(new Library(id: it[1], version: it[2])) }
    }

    // For all instances without @version, set version to 'unknown'
    libraries = (jenkinsfile =~ /\n[\s]*@Library\(["']+([^)^@^,]+)["']+\).*/)
    librariesString = (jenkinsfile =~/\n[\s]*@Library\(\[["'][\s\S]*\]\)/)
    if (librariesString.getCount() > 0) {
      librariesCollection = (librariesString.collect() =~ /["']+([^)^@^,]+)["']+/)

      if (librariesCollection.getCount() > 0) {
        librariesCollection.collect { libs.add(new Library(id: it[1], version: 'unknown')) }
      }
    }
    if (libraries.hasGroup()) {
      libraries.collect { libs.add(new Library(id: it[1], version: 'unknown')) }
    }
    def jenkinsLibraries = []
    libs.each {
      jenkinsLibraries.add(it.id + ":" + it.version)
    }
    jenkins.env.jenkinsLibraries = jenkinsLibraries
    return libs
  }

  static String getJenkinsfile(jenkins) {
    def envConf = jenkins.env."$JENKINSFILE_ENV_VAR"
    def file
    if (envConf) {
      if (WorkflowBasicSteps.fileExists(jenkins, [file: envConf])) {
        file = WorkflowBasicSteps.readFile(jenkins, [file: envConf])
      } else {
        jenkins.error "Could not find Jenkinsfile at location '$envConf'. Pease check the configuration of environment variable $JENKINSFILE_ENV_VAR,\n" +
                "or remove the environment variable to use the default Jenkinsfile in your project root"
      }
    } else {
      if (WorkflowBasicSteps.fileExists(jenkins, [file: 'Jenkinsfile'])) {
        file = WorkflowBasicSteps.readFile(jenkins, [file: 'Jenkinsfile'])
      } else {
        jenkins.error "Jenkinsfile not found in project root directory. Please move it there or set environment variable $JENKINSFILE_ENV_VAR to the relative location of the file"
      }
    }
    return file
  }

  protected static class Library implements Serializable {
    def id
    def version
  }
}
