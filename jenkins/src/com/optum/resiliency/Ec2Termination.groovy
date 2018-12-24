#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers
import com.optum.utils.TimeTakenForTests

def call(Object ec2Termination)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()
    
    String allNodes = sh(script: 'aws ec2 describe-instances --filters "Name=tag:aws:autoscaling:groupName,Values=' + ec2Termination.ec2ASGName + '" --query \'Reservations[*].Instances[*].InstanceId\' --output text --profile saml', returnStdout: true).trim()
    String instances = ResiliencyHelpers.getPercentNodes(ec2Termination.percentOfNodesToTest, allNodes)
    int initialLength = instances.split(/\s+|\n/).length
    
    if(ec2Termination.dryRun == null || (ec2Termination.dryRun != null && ec2Termination.dryRun == false))
    {
        println "Currently running nodes:\n$allNodes"
        println "${ec2Termination.percentOfNodesToTest}% of the nodes which is ${initialLength} nodes will be terminated."
        println "The following insances have been randomly selected to be terminated: $instances"
        sh "aws ec2 terminate-instances --instance-ids $instances --profile saml"
        println "Instance termination initiated. Waiting for new instaces to come up ..."
    }

    sleep ec2Termination.statusCheckSleepTimeInSeconds
    
    println "Checking status ..."
    
    instances = sh(script: 'aws ec2 describe-instances --query \'Reservations[*].Instances[*].InstanceId\' --filters "Name=tag:aws:autoscaling:groupName,Values=' + ec2Termination.ec2ASGName + '" --output text --profile saml', returnStdout: true).trim()
    String[] instanceArray = instances.split(/\s+|\n/)
    int counter = 0
    
    while (instanceArray.length < initialLength && counter++ < 10)
    {
        println "New instances are not up yet. Waiting to check status again ..."
        sleep ec2Termination.statusCheckSleepTimeInSeconds
        
        println "$counter. Checking status ..."
        
        instances = sh(script: 'aws ec2 describe-instances --query \'Reservations[*].Instances[*].InstanceId\' --filters "Name=tag:aws:autoscaling:groupName,Values=' + ec2Termination.ec2ASGName + '" --output text --profile saml', returnStdout: true).trim()
        instanceArray = instances.split(/\s+|\n/)
    }
    
    int totalNodes = allNodes.split(/\s+|\n/).length
    if (instanceArray.length == totalNodes)
    {
        println "New instances are up. Test complete!!!"
    }
    else
    {
        println "New instances are not up yet. There might be something wrong. Please check."
    }

    timeTakenForTest.setTestEndTime()
}
