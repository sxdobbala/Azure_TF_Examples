package com.optum.jenkins.pipeline.library.event

class BuildEvent extends AbstractEvent {
  final String topic = 'devops.build'
  def jenkins
  Map config = [
    duration: null, //required
    status: null, //required
    buildtool: null //required
  ]

  BuildEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    topic
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
    if(isEmpty(config.buildtool)){
      invalidProps.put('buildtool', config.buildtool)
    }
    return invalidProps
  }

  @Override
  def getJenkins(){
    return jenkins
  }
}
