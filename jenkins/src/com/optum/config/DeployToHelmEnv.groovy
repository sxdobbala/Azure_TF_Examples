package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeployToHelmEnv implements Serializable
{
    String kubeconfigfileCredentialsId
    String envName
    String yamlName
    String awsCredentialsId
    String awsAccountId
    Boolean skipDeploy
}