package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.utils.Constants

class ComplianceCheckEvent extends AbstractEvent {
    final String topic = Constants.COMPLIANCE_EVENT_TOPIC
    Object jenkins
    Map config = [
      agileReleaseScope:null, //required
      CM1Data:null, //required
      CM2Data:null, //required
      CM3Data:null, // required
    ]

    ComplianceCheckEvent(jenkins, Map customConfig) {
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
        if (isEmpty(config.agileReleaseScope)) {
            invalidProps.put('agileReleaseScope', config.agileReleaseScope)
        }
        if (isEmpty(config.CM1Data)) {
            invalidProps.put('CM1Data', config.CM1Data)
        }
        if (isEmpty(config.CM2Data)) {
            invalidProps.put('CM2Data', config.CM2Data)
        }
        if (isEmpty(config.CM3Data)) {
          invalidProps.put('CM3Data', config.CM3Data)
        }
        invalidProps
    }

    @Override
    Object getJenkins() {
        jenkins
    }
}
