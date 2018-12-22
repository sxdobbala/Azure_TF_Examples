package com.optum.jenkins.pipeline.library.utils.JenkinsPlugins

// wrapper to track usage of the jenkins plugin Pipeline-Utility-Steps-Plugin
// https://github.com/jenkinsci/pipeline-utility-steps-plugin

class PipelineUtilitySteps {
  static readMavenPom(jenkins, Map props = [:]) {
    jenkins.readMavenPom props
  }

  static readYaml(jenkins, Map props = [:]) {
    try {
      jenkins.readYaml props
    } catch (FileNotFoundException e){
      jenkins.echo('optumfile yaml not found')
      return null
    } catch (Exception e) {
      jenkins.echo('Unknown error occured' + e.getMessage())
      return null
    }
  }

  static readJSON(jenkins, Map props = [:]) {
    jenkins.readJSON props
  }
}
