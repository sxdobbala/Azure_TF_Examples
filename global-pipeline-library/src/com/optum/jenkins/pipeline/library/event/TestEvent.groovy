package com.optum.jenkins.pipeline.library.event

/**
 * Event for when tests are run.
 */
class TestEvent extends AbstractEvent {
  final String topic = 'devops.test'
  def jenkins
  Map config = [
    duration: null, //required
    status: null, //required
    testtype: null //required
  ]

  TestEvent(jenkins, Map customConfig) {
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
    if(isEmpty(config.testtype) || !(config.testtype instanceof TestType)){
      invalidProps.put('testtype', config.testtype)
    }
    return invalidProps
  }

  @Override
  def getJenkins(){
    return jenkins
  }
}
