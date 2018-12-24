package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Lambda implements Serializable
{
    PreExecutionScript preExecutionScript
    String runtime
    String functionName
    DeployToLambdaEnv[] deployToEnv
    Boolean deployPrompts
    Integer deployPromptAbortTimeout
    Boolean validatePrerequisites
    String testInvocationFile
    String s3CredentialsId
    String s3AccountId
    String s3Bucket
    String s3Key
    PostExecutionScript postExecutionScript
}