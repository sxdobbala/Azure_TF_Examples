package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.utils.Cache

class BuildInfoReader implements Serializable{

  static String DEFAULT_DEVOPS_METRICS_ENDPOINTS = 'http://metrics-rest-proxy-devops01.ose-elr-core.optum.com/'
  static String DEFAULT_ELASTIC_SEARCH_ENDPOINTS = 'http://apsrd5304:9200/'
  static String DEFAULT_DTC_ENDPOINTS = 'https://dtc.optum.com/'

  //current build job number
  static def getBuildNumber(jenkins) {
    jenkins.env.BUILD_ID
  }

  //url of the current build
  static def getBuildUrl(jenkins) {
    jenkins.env.BUILD_URL
  }

  //Jenkins instance url
  static def getBuildRootUrl(jenkins) {
    jenkins.env.JENKINS_URL
  }
  
  //is DevOps Metrics Enabled
  static boolean isDevopsMetricsEnabled(jenkins) {
    boolean isEnabled = true
    if (jenkins.env.DEVOPS_METRICS_ENABLED != null) {
      isEnabled = Boolean.parseBoolean(jenkins.env.DEVOPS_METRICS_ENABLED)
    }

    return  isEnabled
  }

  //endpoint to send the pipeline metrics to
  static String getDevopsMetricsEndpoints(jenkins) {
    String envVar_DEVOPS_METRICS_ENDPOINTS =  jenkins.env.DEVOPS_METRICS_ENDPOINTS
    if (envVar_DEVOPS_METRICS_ENDPOINTS != null && envVar_DEVOPS_METRICS_ENDPOINTS != '[]' && envVar_DEVOPS_METRICS_ENDPOINTS != '') {
      return  envVar_DEVOPS_METRICS_ENDPOINTS
    } else {
      return DEFAULT_DEVOPS_METRICS_ENDPOINTS
    }
  }

  //endpoint to get the pipeline metrics from
  static String getElasticSearchEndpoints(jenkins) {
    String envVar_ELASTIC_SEARCH_ENDPOINTS =  jenkins.env.ELASTIC_SEARCH_ENDPOINTS
    if (envVar_ELASTIC_SEARCH_ENDPOINTS != null && envVar_ELASTIC_SEARCH_ENDPOINTS != '[]' && envVar_ELASTIC_SEARCH_ENDPOINTS != '') {
      return  envVar_ELASTIC_SEARCH_ENDPOINTS
    } else {
      return DEFAULT_ELASTIC_SEARCH_ENDPOINTS
    }
  }

  //endpoint to DTC
  static String getDTCEndpoints(jenkins) {
    String envVar_DTC_ENDPOINTS =  jenkins.env.DTC_ENDPOINTS
    if (envVar_DTC_ENDPOINTS != null && envVar_DTC_ENDPOINTS != '[]' && envVar_DTC_ENDPOINTS != '') {
      return  envVar_DTC_ENDPOINTS
    } else {
      return DEFAULT_DTC_ENDPOINTS
    }
  }

  static def getJenkinsVersion(jenkins) {
    Cache cache = Cache.getInstance()
    String cacheKey = "JENKINS_VERSION"
    String version = cache.getValue(cacheKey)
    if (!version) {
      def headerprefix = 'X-Jenkins: '
      version = jenkins.command("curl -i --silent \"${getBuildRootUrl(jenkins)}api/json?tree=nodeDescription\" --stderr - | grep -Eo \"${headerprefix}[0-9.]+\"", true)
      jenkins.echo "Cache empty, requesting Jenkins version, storing return value in cache: $version"
      if (version.startsWith(headerprefix)) {
        version = version.replaceFirst(headerprefix, '')
      } else {
        version = 'na'
      }
      cache.setValue(cacheKey, version)
    }
    return version
  }
}