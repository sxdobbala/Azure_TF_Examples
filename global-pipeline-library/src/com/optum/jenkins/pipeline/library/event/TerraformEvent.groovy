package com.optum.jenkins.pipeline.library.event

class TerraformEvent extends AbstractEvent {
  final String topic = 'devops.cloud.provision'
  def jenkins
  Map config = [
    duration: null, // required
    status: null, // required
    accountId: "N/A",
    environment: "N/A",
    cloudProvider: "N/A"
  ]

  TerraformEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config += customConfig
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
    if (isEmpty(config.duration)) {
      invalidProps.put('duration', config.duration)
    }
    if (isEmpty(config.status)) {
      invalidProps.put('status', config.status)
    }

    return invalidProps
  }

  @Override
  def getJenkins() {
    return jenkins
  }
}
