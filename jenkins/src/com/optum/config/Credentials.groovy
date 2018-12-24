package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Credentials implements Serializable
{
    String credentialsId
    String type
    String usernameVariable  //only if type = usernamePassword
    String passwordVariable  //only if type = usernamePassword
    String tokenVariable  //only if type = token
    String kubeConfigVariable  //only if type = kubeconfig
}