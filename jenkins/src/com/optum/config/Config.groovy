package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Config implements Serializable 
{   
    DeploymentBranchSetting[] deploymentBranchSetting 
    Flowdock flowdock
    String notificationEmail
    String jenkinsNode
    Project project
}