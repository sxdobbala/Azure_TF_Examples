#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers
import com.optum.utils.TimeTakenForTests

def call(Object cpuStarvation)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String allNodes = sh(script: 'aws ec2 describe-instances --filters "Name=tag:aws:autoscaling:groupName,Values=' + cpuStarvation.ec2ASGName + '" --query \'Reservations[*].Instances[*].InstanceId\' --output text --profile saml', returnStdout: true).trim()
    String instanceIds = ResiliencyHelpers.getPercentNodes(cpuStarvation.percentOfNodesToTest, allNodes)
    instanceIds = '"' + instanceIds.replaceAll(/\s+/, /\" \"/) + '"'
    int numberOfTimes = cpuStarvation.numberOfTimes ? cpuStarvation.numberOfTimes : 1
   
    sh '''
        . /etc/profile.d/jenkins.sh
        for number in {1..''' + numberOfTimes + '''}
        do
            commandId=`aws ssm send-command --instance-ids ''' + instanceIds + ''' --document-name "AWS-RunShellScript" --parameters '{"commands":["docker pull progrium/stress","docker run --rm progrium/stress --cpu ''' + cpuStarvation.cpu + ''' --timeout ''' + cpuStarvation.timeout + '''"]}' --query "Command.CommandId" --output text --profile saml`
            sleep ''' + cpuStarvation.timeout + '''
            commandStatus=`aws ssm list-command-invocations --command-id $commandId --query "CommandInvocations[*].StatusDetails" --output text --profile saml`
            count=0
            while [ "$command_status" = "InProgress" ] && [ $count < 3 ]
            do
                count=$((count+1))
                sleep ''' + cpuStarvation.statusCheckSleepTimeInSeconds + '''
                echo "Command is still executing ...." 
                commandStatus=`aws ssm list-command-invocations --command-id $commandId --query "CommandInvocations[*].StatusDetails" --output text --profile saml`
            done
            if [ "$command_status" = "InProgress" ]
            then
                echo "Command took too long to complete."
            else
                echo "Command completed with the status: $commandStatus"    
            fi
       done
    '''

    timeTakenForTest.setTestEndTime()
}
