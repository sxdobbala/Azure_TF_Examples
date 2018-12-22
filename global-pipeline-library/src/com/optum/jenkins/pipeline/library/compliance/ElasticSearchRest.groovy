package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.utils.http.RequestParameters
import com.optum.jenkins.pipeline.library.utils.http.RestClient
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader

class ElasticSearchRest implements RequestParameters, Serializable {

  Map config
  def jenkins
  final String contentType = 'application/json'

  def get(jenkins, Map params) {
    def response = null
    config = params
    this.jenkins = jenkins
    response = RestClient.get(getJenkins(), this)
    return response
  }

  Map getInvalidProperties(Map config) {
    Map invalidProps = [:]
    if(isEmpty(config.index)) {
      invalidProps.put('index', config.index)
    }
    if(isEmpty(config.buildUrl)) {
      invalidProps.put('buildUrl', config.buildUrl)
    }
    return invalidProps
  }

  String getElasticSearchEndpoint() {
    String endpoints = BuildInfoReader.getElasticSearchEndpoints(getJenkins())
    return endpoints
  }

  @Override
  String[] getHosts() {
    return getMessageEndpoints().split(',')
  }

  @Override
  String getPath() {
    '_sql' + getQueryParams()
  }

  String getQueryParams() {
    String query = 'SELECT%20*%20FROM%20'+config.index+"%20where%20buildUrl='"+config.buildUrl+"'"
    '?sql=' + query
  }

  @Override
  String getContentType() {
    contentType
  }

  String getMessageEndpoints() {
    String endpoints = BuildInfoReader.getElasticSearchEndpoints(getJenkins())
    return endpoints
  }

  @Override
  Map getHeaders() {
    [Accept: 'application/json']
  }

  protected static isEmpty(property) {
    return property == null || ''.equals(property)
  }

  def getJenkins(){
    return jenkins
  }

  @Override
  Map getBody() {
    Map finalConfig = getConfig()
    Map invalidConfigProps = getInvalidProperties(finalConfig)
    if(invalidConfigProps.isEmpty()){
      return [records :[ [value :finalConfig]]]
    } else if(!invalidConfigProps.isEmpty()) {
      def invalidProps = ''
      invalidConfigProps.each { invalidProps += " - property name: '${it.key}', invalid value: ${it.value}\n"}
      getJenkins().error "invalid properties (${this.getClass().getSimpleName()}):\n$invalidProps"
    }
  }
}
