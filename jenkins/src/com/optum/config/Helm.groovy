package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Helm implements Serializable
{
    PreExecutionScript preExecutionScript
    String app
    String releaseName
    String namespace
    Boolean deployPrompts
    String chartName
    String gitHubToken
    String dockerTag
    String ecrAccountId
    DeployToHelmEnv[] deployToHelmEnv
    Integer deployPromptAbortTimeout
    PostExecutionScript postExecutionScript
}
