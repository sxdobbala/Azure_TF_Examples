package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.sonar.Sonar
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader

class SonarEvent extends AbstractEvent {
  def jenkins
  Map config = [
    duration: null, //required
    status  : null, //required
    scanTool : null, //required
    targetQG : null  //required, will be read from optumfile if null
  ]

  SonarEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    'devops.sonar'
  }

  @Override
  Map getConfig() {
    if(config.targetQG == null){
      config.targetQG = OptumFileReader.getTargetQG(jenkins)
    }
    Map sonarConfig = config
    if (getInvalidEventProperties(config).size() == 0) {
      Map sonarMetrics = this.getSonarMetrics(sonarConfig)
      sonarConfig = sonarMetrics + sonarConfig
      Map sonarQualityGate = this.getSonarQualityGate(sonarConfig)
      sonarConfig = sonarConfig + sonarQualityGate
    }
    return sonarConfig
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
    if (isEmpty(config.scanTool)) {
      invalidProps.put('scanTool', config.scanTool)
    }
    if (isEmpty(config.targetQG)) {
      invalidProps.put('targetQG', config.targetQG)
    }
    return invalidProps
  }

  @Override
  def getJenkins() {
    return jenkins
  }

  Map getSonarMetrics(config) {
    Sonar sonar = new Sonar(jenkins)
    if (config) {
      if (config.isPreview) {
        sonar.setPreview(config.isPreview)
      }
    }
    return sonar.getSonarMetrics(['additionalMetrics': 'false_positive_issues,wont_fix_issues'])
  }

  Map getSonarQualityGate(config) {
    Sonar sonar = new Sonar(jenkins)
    if (config) {
      if (config.isPreview) {
        sonar.setPreview(config.isPreview)
      }
    }
    return sonar.getSonarQualityGate(['additionalMetrics': 'false_positive_issues,wont_fix_issues'])
  }
}