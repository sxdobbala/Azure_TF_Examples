package com.optum.jenkins.pipeline.library.utils.JenkinsPlugins

// https://plugins.jenkins.io/workflow-basic-steps
// A component of Pipeline Plugin. https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin
class WorkflowBasicSteps {
  // @file
  // @encoding
  static def readFile(jenkins, Map props = [:]) {
    jenkins.readFile props
  }

  static def fileExists(jenkins, Map props = [:]) {
    jenkins.fileExists props
  }
}
