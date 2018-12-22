package com.optum.jenkins.pipeline.library.event

import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.jenkins.pipeline.library.utils.PropertyReader.JenkinsfileReader
import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import com.optum.jenkins.pipeline.library.utils.http.RequestParameters
import com.optum.jenkins.pipeline.library.utils.http.RestClient

abstract class AbstractEvent implements RequestParameters, Serializable {

  Map getMetaData() {
    //wrapped in function for lazy loading
    Object jenkins = getJenkins()
    [
      askId:OptumFileReader.getAskId(jenkins),
      caAgileId:OptumFileReader.getCAAgileId(jenkins),
      projectKey:OptumFileReader.getProjectKey(jenkins),
      componentType:OptumFileReader.getComponentType(jenkins),
      timestamp:new Date().toTimestamp(),
      pipelineLibraries:JenkinsfileReader.getLibraries(jenkins),
      buildId:BuildInfoReader.getBuildNumber(jenkins),
      buildUrl:BuildInfoReader.getBuildUrl(jenkins),
      buildSystemVersion:BuildInfoReader.getJenkinsVersion(jenkins),
    ]
  }

  abstract String getTopic()
  abstract Map getConfig()
  abstract Map getInvalidEventProperties(Map config)
  abstract getJenkins()

  def send() {
    def response = null
    if (isEventMessagingEnabled()) {
      response = RestClient.post(getJenkins(), this)
    }
    return response
  }

  Map getInvalidMetaProperties(Map config) {
    Map invalidProps = [:]
    if(isEmpty(config.askId)) {
      invalidProps.put('askId', config.askId)
    }
    if(isEmpty(config.caAgileId)) {
      invalidProps.put('caAgileId', config.caAgileId)
    }
    if(isEmpty(config.timestamp)) {
      invalidProps.put('timestamp', config.timestamp)
    }
    if(isEmpty(config.buildId)) {
      invalidProps.put('buildId', config.buildId)
    }
    if(isEmpty(config.buildUrl)) {
      invalidProps.put('buildUrl', config.buildUrl)
    }
    if(isEmpty(config.buildSystemVersion)) {
      invalidProps.put('buildSystemVersion', config.buildSystemVersion)
    }
    return invalidProps
  }

  String getMessageEndpoints() {
    String endpoints = BuildInfoReader.getDevopsMetricsEndpoints(getJenkins())
    return endpoints
  }

  @Override
  String[] getHosts() {
    return getMessageEndpoints().split(',')
  }

  @Override
  String getPath() {
    'topics/' + getTopic()
  }

  @Override
  String getContentType() {
    'application/vnd.kafka.json.v2+json'
  }

  @Override
  Map getBody() {
    Map finalConfig = getMetaData() + getConfig()
    Map invalidMetaDataProps = getInvalidMetaProperties(finalConfig)
    Map invalidEventConfigProps = getInvalidEventProperties(finalConfig)
    if(invalidMetaDataProps.isEmpty() && invalidEventConfigProps.isEmpty()){
      return [records :[ [value :finalConfig]]]
    } else if(!invalidMetaDataProps.isEmpty()) {
      def invalidProps = ''
      invalidMetaDataProps.each { invalidProps += " - property name: '${it.key}', invalid value: ${it.value}\n"}
      getJenkins().error "invalid event metadata properties (${this.getClass().getSimpleName()}):\n$invalidProps"
    } else if(!invalidEventConfigProps.isEmpty()) {
      def invalidProps = ''
      invalidEventConfigProps.each { invalidProps += " - property name: '${it.key}', invalid value: ${it.value}\n"}
      getJenkins().error "invalid event specific properties (${this.getClass().getSimpleName()}):\n$invalidProps"
    }
  }

  @Override
  Map getHeaders() {
    [Accept: 'application/json']
  }

  def isEventMessagingEnabled() {
    boolean returnValue = false

    if (BuildInfoReader.isDevopsMetricsEnabled(getJenkins())){
      String hosts = getMessageEndpoints()
      if (hosts == null || hosts.trim().isEmpty()) {
        getJenkins().error "EVENT '${this.getTopic()}': DevOps Event Logging has been enabled by setting DEVOPS_METRICS_ENABLED to true; however, \nEVENT: the variable DEVOPS_METRICS_ENDPOINTS has not been set; therefore, \nEVENT: The DevOps Event Logging is still disabled\n"
      } else {
        returnValue = true
      }
    } else {
      getJenkins().echo "EVENT '${this.getTopic()}': DevOps Event Logging has been disabled.\nEVENT: To enable this beta feature please \nEVENT:   -set DEVOPS_METRICS_ENABLED=true\n"

    }
    
    return returnValue
  }

  protected static isEmpty(property) {
    return property == null || ''.equals(property)
  }
}

