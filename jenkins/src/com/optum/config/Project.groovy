package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Project implements Serializable 
{
    String gitUrl
    String credentialsId
    String pomPathExtension
}
