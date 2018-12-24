package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def getCredentials(Object creds)
{
    Map<String, String> namepass = new HashMap<String, String>()

    for (j=0; j < creds.size(); j++)
    {
        if(creds[j].type == 'usernamePassword')
        {
            String username
            String password
            (username,password)  = getUsernamePassword(creds[j].credentialsId)
            namepass.put(creds[j].usernameVariable,username)
            namepass.put(creds[j].passwordVariable,password)
        }
        else if(creds[j].type == 'token')
        {
            namepass.put(creds[j].tokenVariable, getToken(creds[j].credentialsId))
        }
        else if(creds[j].type == 'kubeconfig')
        {
            namepass.put(creds[j].kubeConfigVariable, getKubeConfig(creds[j].credentialsId))
        }
    }

    return namepass
}

def getToken(String credsID)
{
    String TokenValue
    withCredentials([string(credentialsId: credsID, variable: 'token')])
    { TokenValue = "${env.token}" }
    return TokenValue
}

def getUsernamePassword(String credsID)
{
    String Uname
    String Pass
    withCredentials([
        usernamePassword(credentialsId: credsID, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')
    ])
    {
        Uname = "${env.USERNAME}"
        Pass = "${env.PASSWORD}"
    }
    return [Uname, Pass]
}

def getKubeConfig(String credsID)
{
    String KubeConf
    withCredentials([file(credentialsId: credsID, variable: 'kubeconfig')])
    { KubeConf = "${env.kubeconfig}" }
    return KubeConf
}
