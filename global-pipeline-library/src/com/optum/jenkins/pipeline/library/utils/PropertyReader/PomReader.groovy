package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.PipelineUtilitySteps

class PomReader implements Serializable{
  static String defaultPomFile = 'pom.xml'

  static getProjectGroupId(def file = defaultPomFile){
    def root = PipelineUtilitySteps.readMavenPomWrap file: file
    return root.groupId
  }

  static getArtifactId(def file = defaultPomFile){
    def root = PipelineUtilitySteps.readMavenPomWrap file: file
    return root.artifactId
  }

  static getVersion(def file = defaultPomFile){
    def root = PipelineUtilitySteps.readMavenPomWrap file: file
    return root.version ?: root.parent.version
  }

  static getModel(def file = defaultPomFile){
    PipelineUtilitySteps.readMavenPomWrap file: file
  }
}
