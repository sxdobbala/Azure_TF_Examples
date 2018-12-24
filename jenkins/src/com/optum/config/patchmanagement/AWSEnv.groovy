package com.optum.config.patchmanagement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AWSEnv implements Serializable
{
	String envName
	String[] autoscalingGroups
}