#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers
import com.optum.utils.TimeTakenForTests

def call(Object egressInstanceFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String allNodes = sh(script: 'aws ec2 describe-instances --filters "Name=tag:aws:autoscaling:groupName,Values=' + egressInstanceFailover.ec2ASGName + '" --query \'Reservations[*].Instances[*].InstanceId\' --output text --profile saml', returnStdout: true).trim()
    String instances = ResiliencyHelpers.getPercentNodes(egressInstanceFailover.percentOfNodesToTest, allNodes)
    int initialLength = instances.split("\n").length


    if(egressInstanceFailover.dryRun == null || (egressInstanceFailover.dryRun !=null && egressInstanceFailover.dryRun == false))
    {
        println "The following insances have been randomly selected to be terminated: $instances"
        sh "aws ec2 terminate-instances --instance-ids $instances --profile saml"
        println "Instance termination initiated. Waiting for new instaces to come up ..."
    }

    sleep egressInstanceFailover.statusCheckSleepTimeInSeconds

    println "Checking status ..."

    instances = sh(script: 'aws ec2 describe-instances --query \'Reservations[*].Instances[*].InstanceId\' --filters "Name=tag:aws:autoscaling:groupName,Values=' + ec2Termination.ec2ASGName + '" --output text --profile saml', returnStdout: true).trim()
    String[] instanceArray = instances.split("\n")
    int counter = 0

    while (instanceArray.length < initialLength && counter++ < 10)
    {
        println "New instances are not up yet. Waiting to check status again ..."
        sleep egressInstanceFailover.statusCheckSleepTimeInSeconds

        println "$counter. Checking status ..."

        instances = sh(script: 'aws ec2 describe-instances --query \'Reservations[*].Instances[*].InstanceId\' --filters "Name=tag:aws:autoscaling:groupName,Values=' + ec2Termination.ec2ASGName + '" --output text --profile saml', returnStdout: true).trim()
        instanceArray = instances.split("\n")
    }

    int totalNodes = allNodes.split("\n").length
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
