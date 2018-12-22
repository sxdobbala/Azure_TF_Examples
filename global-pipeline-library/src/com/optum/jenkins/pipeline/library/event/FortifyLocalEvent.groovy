package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.utils.Constants

class FortifyLocalEvent extends AbstractEvent {
  final String topic = Constants.FORTIFY_LOCAL_EVENT_TOPIC
  Object jenkins
  Map config = [
    duration           : null, //required
    status             : null, //required
    translateExclusions: null, //required
    fortifyBuildName   : null, //required
    scarProjectName    : null,
    scarProjectVersion : null,
  ]

  FortifyLocalEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    topic
  }

  @Override
  Map getConfig() {
    Map fortifyConfig = config
    fortifyConfig.put('scanType', 'local')
    def issuesMap = fortifyConfig.remove('issuesMap')
    if (getInvalidEventProperties(config).isEmpty() && issuesMap) {
      fortifyConfig = issuesMap + fortifyConfig
    }
    fortifyConfig
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
    if (isEmpty(config.fortifyBuildName)) {
      invalidProps.put('fortifyBuildName', config.fortifyBuildName)
    }
    if (isEmpty(config.translateExclusions)) {
      invalidProps.put('translateExclusions', config.translateExclusions)
    }
    invalidProps
  }

  @Override
  Object getJenkins() {
    jenkins
  }

}
