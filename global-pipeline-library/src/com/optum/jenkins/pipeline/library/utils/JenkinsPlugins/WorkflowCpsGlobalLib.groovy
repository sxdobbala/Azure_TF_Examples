package com.optum.jenkins.pipeline.library.utils.JenkinsPlugins

//https://github.com/jenkinsci/workflow-cps-global-lib-plugin
class WorkflowCpsGlobalLib {

  //https://jenkins.io/doc/pipeline/steps/workflow-cps-global-lib/#libraryresource-load-a-resource-file-from-a-shared-library
  //https://github.com/jenkinsci/workflow-cps-global-lib-plugin/blob/master/src/main/java/org/jenkinsci/plugins/workflow/libs/ResourceStep.java
  // resourcePath start below /resources
  static String libraryResource(jenkins, resourcePath){
    jenkins.libraryResource resourcePath
  }
}
