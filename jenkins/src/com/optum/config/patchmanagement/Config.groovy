package com.optum.config.patchmanagement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.optum.config.Flowdock

@JsonIgnoreProperties(ignoreUnknown = true)
class Config implements Serializable
{
	String notificationEmail
	Flowdock flowdock
	String jenkinsNode
	String awsCredentialsId
	String awsAccountId
	String kubeconfigfileCredentialsId
	AWSEnv[] awsEnv
}