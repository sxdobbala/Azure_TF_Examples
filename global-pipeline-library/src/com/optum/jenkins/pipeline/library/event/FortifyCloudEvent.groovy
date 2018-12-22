package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader

class FortifyCloudEvent extends AbstractEvent {
    final String topic = 'devops.fortify.cloud'
    Object jenkins
    Map config = [
      duration:null, //required
      status:EventStatus.STARTED, //required
      cloudJobSubmitToken:null, //required
      fortifyBuildName:null, //required
      translateExclusions:null, //required
      scarProjectName:null,
      scarProjectVersion:null,
    ]

    FortifyCloudEvent(jenkins, Map customConfig) {
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
      fortifyConfig.put('scanType', 'cloud')
      fortifyConfig
    }

    @Override
    Map getInvalidEventProperties(Map config) {
        Map invalidProps = [:]
        if (isEmpty(config.duration)) {
            invalidProps.put('duration', config.duration)
        }
        if (isEmpty(config.cloudJobSubmitToken)) {
            invalidProps.put('cloudJobSubmitToken', config.cloudJobSubmitToken)
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
