package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeployToAwsEnv implements Serializable
{
    String kubeconfigfileCredentialsId
    String namespace
    String envName
    String testTag
    String stageTag
    String yamlName
    String awsCredentialsId
}