package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Sonar implements Serializable 
{
    PreExecutionScript preExecutionScript
    boolean sonarPreview
    boolean shortLivedBranchScan
    String credentialsId
    String envUrl
    String projectKey
    String projectVersion
    String projectName
    String exclusions
    String ciLink
    String jacocoReportPaths
    String additionalParams
    Boolean ignoreFailure
    String metric
    String checkIncreaseOrDecrease
    String version
    PostExecutionScript postExecutionScript
}
