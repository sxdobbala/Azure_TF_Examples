#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers
import com.optum.utils.TimeTakenForTests

def call(Object dbFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String dbEndpoint = sh(script: 'aws rds describe-db-clusters --filters "Name=db-cluster-id,Values=' + dbFailover.dbClusterId + '" --query \'DBClusters[*].Endpoint\' --output text --profile saml', returnStdout: true).trim()

    println "The database endpoint is $dbEndpoint"


    if(dbFailover.dryRun == null || (dbFailover.dryRun !=null && dbFailover.dryRun == false))
    {
        println "Initiating database failover ..."
        sh "aws rds failover-db-cluster --db-cluster-identifier ${dbFailover.dbClusterId} --profile saml"
        println "Database failover command executed. Waiting for the failover database to come back up ..."
    }

    sleep dbFailover.statusCheckSleepTimeInSeconds
    
    println "Checking status of the database cluster ..."
    String status = sh(script: 'aws rds describe-db-clusters --filters "Name=db-cluster-id,Values=' + dbFailover.dbClusterId + '" --query \'DBClusters[*].Status\' --output text --profile saml', returnStdout: true).trim()
    int counter = 0
    
    while (status != "available" && counter++ < 10)
    {
        println "The database cluster is not up yet. Waiting to check status again ..."
        sleep dbFailover.statusCheckSleepTimeInSeconds
        
        println "$counter. Checking status of the database cluster ..."

        // We might need to refresh the aws session if ($counter * dbFailover.statusCheckSleepTimeInSeconds)  > 50 min
        // or we should run all AWS call in a helper function that has try catch block for error handling ..should retry 
        status = sh(script: 'aws rds describe-db-clusters --filters "Name=db-cluster-id,Values=' + dbFailover.dbClusterId + '" --query \'DBClusters[*].Status\' --output text --profile saml', returnStdout: true).trim()
    }
    
    if (status != "available")
    {
        println "The failover database still isn't up. There might be something wrong. Please check."
    }
    else
    {
        println "The failover database is up. Test complete!!!"
    }
    timeTakenForTest.setTestEndTime()
}