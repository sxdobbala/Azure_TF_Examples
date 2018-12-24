#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers
import com.optum.utils.TimeTakenForTests

def call(Object diskStarvation)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String allNodes = sh(script: 'aws ec2 describe-instances --filters "Name=tag:aws:autoscaling:groupName,Values=' + diskStarvation.ec2ASGName + '" --query \'Reservations[*].Instances[*].InstanceId\' --output text --profile saml', returnStdout: true).trim()
    String instanceIds = ResiliencyHelpers.getPercentNodes(diskStarvation.percentOfNodesToTest, allNodes)
    instanceIds = '"' + instanceIds.replaceAll(/\s+/, /\" \"/) + '"'
    int numberOfTimes = diskStarvation.numberOfTimes ? diskStarvation.numberOfTimes : 1
    String hddArgs = "--hdd ${diskStarvation.hdd}"
    
    if (diskStarvation.hddBytes)
    {
        hddArgs += " --hdd-bytes ${diskStarvation.hddBytes}"
    }
    
    if (diskStarvation.hddNoclean && diskStarvation.hddNoclean == true)
    {
        hddArgs += " --hdd-noclean"
    }
    
    sh '''
        . /etc/profile.d/jenkins.sh
        for number in {1..''' + numberOfTimes + '''}
        do
            commandId=`aws ssm send-command --instance-ids ''' + instanceIds + ''' --document-name "AWS-RunShellScript" --parameters '{"commands":["docker pull progrium/stress","docker run --rm progrium/stress ''' + hddArgs + ''' --timeout ''' + diskStarvation.timeout + '''"]}' --query "Command.CommandId" --output text --profile saml`
            sleep ''' + diskStarvation.timeout + '''
            commandStatus=`aws ssm list-command-invocations --command-id $commandId --query "CommandInvocations[*].StatusDetails" --output text --profile saml`
            count=0
            while [ "$command_status" = "InProgress" ] && [ $count < 3 ]
            do
                count=$((count+1))
                sleep ''' + diskStarvation.statusCheckSleepTimeInSeconds + '''
                echo "Command is still executing....." 
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
