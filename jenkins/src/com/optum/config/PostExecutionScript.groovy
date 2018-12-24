package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class PostExecutionScript implements Serializable
{
    String[] commandset
    Credentials[] credentials	
}