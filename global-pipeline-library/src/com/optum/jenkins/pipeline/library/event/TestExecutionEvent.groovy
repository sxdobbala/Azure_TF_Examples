package com.optum.jenkins.pipeline.library.event

class TestExecutionEvent extends AbstractEvent {
    final String topic = 'devops.test'
    Object jenkins
    Map config = [
      duration:null, //required
      testType:null, //required
      totalTests:null, //required
      testsExecuted:null, //required
      testsPassed:null, //required
      testsFailed:null, //required
    ]

    TestExecutionEvent(jenkins, Map customConfig) {
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
        if (isEmpty(config.duration)) {
            invalidProps.put('duration', config.duration)
        }
        if (isEmpty(config.testType)) {
            invalidProps.put('testType', config.testType)
        }
        if (isEmpty(config.totalTests)) {
            invalidProps.put('totalTests', config.totalTests)
        }
        if (isEmpty(config.testsExecuted)) {
            invalidProps.put('testsExecuted', config.testsExecuted)
        }
        if (isEmpty(config.testsPassed)) {
            invalidProps.put('testsPassed', config.testsPassed)
        }
        if (isEmpty(config.testsFailed)) {
            invalidProps.put('testsFailed', config.testsFailed)
        }
        invalidProps
    }

    @Override
    Object getJenkins() {
        jenkins
    }
}
