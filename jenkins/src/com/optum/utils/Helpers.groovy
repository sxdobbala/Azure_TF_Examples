package com.optum.utils

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonBuilder
import com.cloudbees.groovy.cps.NonCPS
import com.optum.config.Config

//@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.7')

class Helpers
{
    static def steps
    
    static def setSteps(steps)
    {
        this.steps = steps
    }
    
    @NonCPS
    static List<Map> mapToList(Map input)
    {
        return input.collect { k, v -> [key: k, value: v]}
    }
    
    @NonCPS
    static String toJson(Object item)
    {
        ObjectMapper objectMapper = new ObjectMapper()
        String json = objectMapper.writeValueAsString(item)
        return json
    }
    
    @NonCPS
    static <T> T convertValue(Object fromValue, Class<T> toValueType)
    {
        ObjectMapper objectMapper = new ObjectMapper()
        T toValue = objectMapper.convertValue(fromValue, toValueType)
        return toValue
    }
    
    @NonCPS
    static Map merge(Map[] sources)
    {
        if (sources.length == 0) return [:]
        if (sources.length == 1) return sources[0]
            
        (Map) sources.inject([:]) { result, source ->
            source.each { k, v ->
                result[k] = result[k] instanceof Map ? merge(result[k], v) : v
            }
            result
        }
    }
    
    @NonCPS
    static String getTag(String tag, String tagIdentifier)
    {
        if (tagIdentifier != null && !tagIdentifier.allWhitespace)
        {
            tag = tag + "-" + tagIdentifier
        } 
        return tag   
    }
    
    @NonCPS
    static String mktempDir()
    {
        String tempDir = steps.sh(script: 'mktemp -d', returnStdout: true).trim()
        return tempDir
    }
    
    @NonCPS
    static String mktemp()
    {
        String temp = steps.sh(script: 'mktemp', returnStdout: true).trim()
        return temp
    }
    
    @NonCPS
    static String getNode(String jenkinsNode)
    {        
        return jenkinsNode ? jenkinsNode : 'docker-maven-slave'
    }
    
    // Generates the request body including the flowdock message.
    @NonCPS
    static requestBodyFlowdock(String message)
    {
        def jsonBuilder = new JsonBuilder()
        def root = jsonBuilder.call(
            event: "message",
            content: message
        )
        return jsonBuilder.toString()
    }
    
    @NonCPS
    static Integer getTimeout(Integer userInputTimeout)
    {
        return userInputTimeout ? userInputTimeout: 10
    }

    @NonCPS
    static String getNameSpace(String namespace, Object deployBranchSetting)
    {
        def nameSpace
        if(namespace != null)
        {
            nameSpace = namespace
        }
        else
        {
            nameSpace = deployBranchSetting.aws.namespace
        }
        return nameSpace
    }

    @NonCPS
    static String getAWSUrl(String branchName, Config config, String yamlName)
    {
        def url = config.project.gitUrl.replace('https://github.optum.com/','https://github.optum.com/raw/')
        def replaceString = '/' + branchName + '/' + yamlName
        def awsUrl = url.replace('.git' , replaceString)

        return awsUrl
    }

    @NonCPS
    static String getHelmUrl(String branchName, Config config , String yamlName, String chartName)
    {
        def url = config.project.gitUrl.replace('https://github.optum.com/','https://github.optum.com/raw/')
        def replaceString = '/'+ branchName + '/' + chartName + '/' + yamlName
        def helmUrl = url.replace('.git' , replaceString)

        return helmUrl
    }
    
    @NonCPS
    static Object jsonParse(String json)
    {
        return new groovy.json.JsonSlurperClassic().parseText(json)
    }
}
