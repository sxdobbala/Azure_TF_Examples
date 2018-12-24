#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.TimeTakenForTests

def call(Object redisFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String redisPrimaryNode = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + redisFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupMembers[?CurrentRole==`primary`].CacheClusterId\'  --profile saml --output text', returnStdout: true).trim()
    String nodeGroupId = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + redisFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupId\'  --profile saml --output text', returnStdout:true).trim()
    
    println "Redis current primary node is $redisPrimaryNode for the node group id $nodeGroupId"
    
    println "Initiating redis failover ..."

    if(redisFailover.dryRun == null || (redisFailover.dryRun !=null && redisFailover.dryRun == false))
    {
        sh "aws elasticache test-failover --replication-group-id ${redisFailover.redisReplicationGroupId} --node-group-id $nodeGroupId --profile saml"
        println "Redis failover command executed. Waiting for the failover complete ..."
    }

    sleep redisFailover.statusCheckSleepTimeInSeconds
    
    println "Checking status of the redis cluster ..."
    String status = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + redisFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupMembers[?CurrentRole==`primary`].CacheClusterId\' --profile saml --output text', returnStdout: true).trim()
    int counter = 0
    
    while (status != redisPrimaryNode && counter++ < 10)
    {
        println "The redis cluster is not up yet. Waiting to check status again ..."
        sleep redisFailover.statusCheckSleepTimeInSeconds
        
        println "$counter. Checking status of the redis cluster ..."
        status = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + redisFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupMembers[?CurrentRole==`primary`].CacheClusterId\' --profile saml --output text', returnStdout: true).trim()
    }
    
    if (status != redisPrimaryNode || status == "" )
    {
        println "The failover database still isn't up. There might be something wrong. Please check."
    }
    else
    {
        println "The failover complete. Test complete!!!"
    }

    timeTakenForTest.setTestEndTime()
}
