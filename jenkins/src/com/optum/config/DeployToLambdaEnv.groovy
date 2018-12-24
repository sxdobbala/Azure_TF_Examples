package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeployToLambdaEnv implements Serializable
{
    String envName
    String awsCredentialsId
    String awsAccountId
    String version
    String alias
    String deployType // AllAtOnce or Canary
    int initialWeight // In percent. Only for Canary deployType
    int duration // In minutes. Only for Canary deployType
}