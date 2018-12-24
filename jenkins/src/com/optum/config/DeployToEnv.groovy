package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeployToEnv implements Serializable
{
    String project
    String envName
    Boolean multiDataCenterDeployment
    String app
    String appGreen
    String testTag
    String stageTag
    String pullTag
    String pushTag
    Boolean skipDeploy
}