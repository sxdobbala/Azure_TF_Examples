package com.optum.jenkins.pipeline.library.event

class DeployEvent extends AbstractEvent {
  final String topic = 'devops.deploy'
  def jenkins
  Map config = [
    duration: null, //required
    status: null, //required
    deployTool: null, //required
    production: null, //required
    env: null, // required
    platform: null // required
  ]

  DeployEvent (jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    topic
  }

  @Override
  Map getConfig() {
    Map deployConfig = config
    if (getInvalidEventProperties(config).size() == 0) {
      if (config.production) {
        Map changeTicket = this.getChangeTicket(deployConfig)
        deployConfig = changeTicket + deployConfig
      }
    }
    return deployConfig
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
    if(isEmpty(config.deployTool)){
      invalidProps.put('deployTool', config.deployTool)
    }
    if(isEmpty(config.production)){
      invalidProps.put('production', config.production)
    } else {
      if((config.production).getClass() != Boolean) {
        invalidProps.put('production', config.production)
      }
    }
    if(isEmpty(config.env)){
      invalidProps.put('env', config.env)
    }
    if(isEmpty(config.platform)){
      invalidProps.put('platform', config.platform)
    }
    return invalidProps
  }

  @Override
  def getJenkins(){
    return jenkins
  }

  // Add changeTicket when and integrate with compliance
  Map getChangeTicket(config) {
    return [:]
  }
}
