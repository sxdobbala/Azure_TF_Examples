package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Docker implements Serializable 
{
    PreExecutionScript preExecutionScript
    String repo
    String credentialsId
    String awsAccountId
    String env
    String hub
    String tagIdentifier
    String repoGreen
    String dockerfilePathExtension
    String cloudEnv
    String backupDtrCredentialsId
    Boolean backupDtr
    Boolean prompt
    Integer promptAbortTimeout
    PostExecutionScript postExecutionScript
}
