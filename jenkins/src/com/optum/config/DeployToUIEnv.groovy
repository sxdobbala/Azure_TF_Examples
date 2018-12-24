package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeployToUIEnv implements Serializable
{
    String awsCredentialsId
    String awsAccountId
    String envName
    String s3Bucket
}