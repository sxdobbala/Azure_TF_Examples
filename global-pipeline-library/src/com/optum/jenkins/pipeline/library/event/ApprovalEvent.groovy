package com.optum.jenkins.pipeline.library.event

class ApprovalEvent extends AbstractEvent {
  final String topic = 'devops.approval'
  def jenkins
  Map config = [
      duration: null, //required
      status: null, //required
  ]

  ApprovalEvent(jenkins, Map customConfig) {
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
    return invalidProps
  }

  @Override
  def getJenkins(){
    return jenkins
  }
}
