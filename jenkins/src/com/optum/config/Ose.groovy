package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Ose implements Serializable 
{
    PreExecutionScript preExecutionScript
    String credentialsId
    String server
    String ctcServer
    Boolean deployPrompts
    DeployToEnv[] deployToEnv
    Integer deployPromptAbortTimeout
    PostExecutionScript postExecutionScript
}
