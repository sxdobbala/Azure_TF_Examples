#!/usr/bin/env groovy
package com.optum.utils

import com.optum.config.resiliency.Config

class ResiliencyHelpers implements Serializable
{
    static String getPercentNodes(int percentNodes, String instances)
    {
        return getPercentNodes(percentNodes, instances, "\\s+|\\n")
    }
    
    static String getPercentNodes(int percentNodes, String instances, String regexPattern)
    {
        String[] instanceArray = instances.split(regexPattern)
        int arrayLength = instanceArray.length
        int numberOfNodes = arrayLength * percentNodes / 100
        Random random = new Random()
        int randomNumber
        String selectedInstances = ""

        (1..numberOfNodes).each
        {
            randomNumber = random.nextInt(arrayLength)
            selectedInstances += instanceArray[randomNumber] + " "
        }
        
        return selectedInstances.trim()
    }
}
