#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.TimeTakenForTests

def call(Object totalDbFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String dbSGGroupId = sh(script: 'aws rds describe-db-clusters --filters "Name=db-cluster-id,Values=' + totalDbFailover.dbClusterId + '" --query \'DBClusters[*].VpcSecurityGroups[*].VpcSecurityGroupId\' --output text --profile saml', returnStdout: true).trim()
    String dbSourceGroupID = sh(script: 'aws ec2 describe-security-groups --group-ids $dbSGGroupId --query \'SecurityGroups[*].IpPermissions[*].UserIdGroupPairs[*].GroupId\' --output text --profile saml', returnStdout: true).trim()
    Integer dbSourcePort = sh(script: 'aws rds describe-db-clusters --filters "Name=db-cluster-id,Values=' + totalDbFailover.dbClusterId + '" --query \'DBClusters[*].Port\' --output text --profile saml', returnStdout: true).trim()


    if(totalDbFailover.dryRun == null || (totalDbFailover.dryRun !=null && totalDbFailover.dryRun == false))
    {
        println "Initiating redis failover ..."
        sh "aws ec2 revoke-security-group-ingress --group-id $dbSGGroupId --protocol tcp --port $dbSourcePort --source-group $dbSourceGroupID --profile saml"
        println "Redis failover command executed. Waiting for the failover complete ..."
    }

    sleep totalDbFailover.statusCheckSleepTimeInSeconds

    if(totalDbFailover.dryRun == null || (totalDbFailover.dryRun !=null && totalDbFailover.dryRun == false))
        sh "aws ec2 authorize-security-group-ingress --group-id $dbSGGroupId --protocol tcp --port $dbSourcePort --source-group $dbSourceGroupID --profile saml"

    timeTakenForTest.setTestEndTime()
}
