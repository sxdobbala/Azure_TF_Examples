package com.optum.jenkins.pipeline.library.event

class ArtifactStoreEvent extends AbstractEvent {

  def jenkins
  Map config = [
    duration: null, //required
    status: null, //required
  ]

  ArtifactStoreEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    'devops.artifact.store'
  }

  @Override
  Map getConfig() {
    config
  }

  @Override
  Map getInvalidEventProperties(Map config) {
    Map invalidProps = [:]
    if(isEmpty(config.duration)){
      invalidProps.put('duration', config.duration)
    }
    if(isEmpty(config.status)){
      invalidProps.put('status', config.status)
    }
    return invalidProps
  }

  @Override
  def getJenkins() {
    jenkins
  }
}
