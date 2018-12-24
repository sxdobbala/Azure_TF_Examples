#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.TimeTakenForTests


//TODO ES does not have a independent SG--Pending
def call(Object totalElasticSearchFailover)
{
    TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
    timeTakenForTest.setTestStartTime()

    String sgGroupIds = sh(script: 'aws es describe-elasticsearch-domain-config --domain-name' + totalElasticSearchFailover.domainID +'  --query \'DomainConfig.VPCOptions.Options.SecurityGroupIds\' --output text --profile saml', returnStdout: true).trim()
    String sgGroupIdWorkerNode = sh(script: 'aws ec2 describe-security-groups --group-ids $sgGroupIds --query \'SecurityGroups[?starts_with(GroupName, `nodes`)].GroupId\' --output text --profile saml', returnStdout: true).trim()

    String redisSourceGroupID = sh(script: 'aws ec2 describe-security-groups --group-ids $redisCacheGroupID --query \'SecurityGroups[*].IpPermissions[*].UserIdGroupPairs[*].GroupId\' --output text --profile saml', returnStdout: true).trim()
    Integer redisSourcePort = sh(script: 'aws elasticache describe-replication-groups --replication-group-id ' + totalElasticSearchFailover.redisReplicationGroupId + ' --query \'ReplicationGroups[*].NodeGroups[*].NodeGroupMembers[?CurrentRole==`primary`].ReadEndpoint.Port\'  --profile saml --output text', returnStdout: true).trim()

    if(totalElasticSearchFailover.dryRun == null || (totalElasticSearchFailover.dryRun !=null && totalElasticSearchFailover.dryRun == false))
    {
        println "Initiating redis failover ..."
        sh "aws ec2 revoke-security-group-ingress --group-id $redisCacheGroupID --protocol tcp --port $redisSourcePort --source-group $redisSourceGroupID --profile saml"
        println "ES failover command executed. Waiting for the failover complete ..."
    }

    sleep totalElasticSearchFailover.statusCheckSleepTimeInSeconds


    if(totalElasticSearchFailover.dryRun && totalElasticSearchFailover.dryRun == false)
        sh "aws ec2 authorize-security-group-ingress --group-id $redisCacheGroupID --protocol tcp --port $redisSourcePort --source-group $redisSourceGroupID --profile saml"

    timeTakenForTest.setTestEndTime()
}
