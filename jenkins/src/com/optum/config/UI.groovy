package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class UI implements Serializable
{
    PreExecutionScript preExecutionScript
    DeployToUIEnv[] deployToEnv
    String stashName
    String stashDirectory
    Integer deployPromptAbortTimeout
    PostExecutionScript postExecutionScript
}
