package com.optum.jenkins.pipeline.library.event

import com.google.gson.Gson

class ContrastEvent extends AbstractEvent {
  final String topic = 'devops.contrast'
  def jenkins

  Map config = [
      duration: null, //required
      status: null, //required
      contrastSummary: null 
  ]

  ContrastEvent(jenkins, Map customConfig) {
    this.jenkins = jenkins
    config = config + customConfig
  }

  @Override
  String getTopic() {
    topic
  }

  @Override
  Map getConfig() {
    if (config.contrastPayload != null) {
      // Emit contrast information as a well defined structure
      config.contrastSummary = new Gson().fromJson(config.contrastPayload, ApplicationStatus.class);
    }

    // Discard now that string "json" (i.e., contrastPayload) is in object form in "contrastSummary"
    config.contrastPayload = null
    config.remove('contrastPayload')

    config
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
    if(isEmpty(config.contrastSummary)){
      invalidProps.put('contrastSummary', config.contrastSummary)
    }
    return invalidProps
  }

  @Override
  def getJenkins(){
    return jenkins
  }

  // These support the output of well-defined structured data
  // to the devops event store.
  public static class ApplicationStatus implements Serializable {
    String buildId
    String buildNote // TODO: For future use; example--> report why a build failed based on issues found by Contrast
    String orgUuid
    String appUuid
    String appName
    String appPath
    String appLetterGrade
    String appLanguage
    String appVersionTag
    Integer appVulnTotalLibrary

    BuildTotals buildTotals

    List<VulnStatusSummary> vulnStatusSummary
    List<LibraryGradeSummary> libraryGradeSummary
    List<LibraryAttributes> vulnerableLibraries
  }  

  public static class BuildTotals implements Serializable {
    Integer vulnTotal
    Integer vulnTotalOpen
    Integer critical
    Integer high
    Integer medium
    Integer note
  }

  public static class VulnStatusSummary implements Serializable {
    String status
    Integer count
  }

  public static class LibraryGradeSummary implements Serializable {
    String grade
    Integer count
  }

  public static class LibraryAttributes implements Serializable {
    String fileName
    String fileVersion
    String fileHash
    String grade
    Integer vulnTotal
  }  
}