#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.TimeTakenForTests

def call(Object totalRedisFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String redisCacheGroupID = sh(script: 'aws elasticache describe-cache-clusters --query \'CacheClusters[?ReplicationGroupId==`' + totalRedisFailover.redisReplicationGroupId + '`]|[0].SecurityGroups[0].SecurityGroupId\' --output text --profile saml', returnStdout: true).trim()
    String redisSourceGroupID = sh(script: 'aws ec2 describe-security-groups --group-ids $redisCacheGroupID --query \'SecurityGroups[*].IpPermissions[*].UserIdGroupPairs[*].GroupId\' --output text --profile saml', returnStdout: true).trim()
    Integer redisSourcePort = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + totalRedisFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupMembers[?CurrentRole==`primary`].ReadEndpoint.Port\'  --profile saml --output text', returnStdout: true).trim()

    if(totalRedisFailover.dryRun == null || (totalRedisFailover.dryRun !=null && totalRedisFailover.dryRun == false))
    {
        println "Initiating redis failover ..."
        sh "aws ec2 revoke-security-group-ingress --group-id $redisCacheGroupID --protocol tcp --port $redisSourcePort --source-group $redisSourceGroupID --profile saml"
        println "Redis failover command executed. Waiting for the failover complete ..."
    }

    sleep totalRedisFailover.statusCheckSleepTimeInSeconds

    if(totalRedisFailover.dryRun == null || (totalRedisFailover.dryRun !=null && totalRedisFailover.dryRun == false))
        sh "aws ec2 authorize-security-group-ingress --group-id $redisCacheGroupID --protocol tcp --port $redisSourcePort --source-group $redisSourceGroupID --profile saml"

    timeTakenForTest.setTestEndTime()
}
