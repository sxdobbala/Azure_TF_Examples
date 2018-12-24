package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AWS implements Serializable
{
    PreExecutionScript preExecutionScript
    String app
    String namespace
    Boolean prompt
    String gitHubToken
    String dockerTag
    DeployToAwsEnv[] deployToAwsEnv
    Integer deployPromptAbortTimeout
    PostExecutionScript postExecutionScript
}
