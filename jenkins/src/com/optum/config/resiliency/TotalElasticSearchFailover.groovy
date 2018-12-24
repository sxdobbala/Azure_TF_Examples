package com.optum.config.resiliency


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)

class TotalElasticSearchFailover implements Serializable {
    boolean runTest
    String redisReplicationGroupId
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}
