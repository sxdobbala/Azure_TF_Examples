package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Config implements Serializable 
{
    String profile
    String awsCredentialsId
    String awsAccountId
    DbFailover dbFailover
    RedisFailover redisFailover
    Ec2Termination ec2Termination
    MasterTermination masterTermination
    CpuStarvation cpuStarvation
    MemoryStarvation memoryStarvation
    DiskStarvation diskStarvation
    IoStarvation ioStarvation
    EgressInstanceFailover egressInstanceFailover
    TotalRedisFailover totalRedisFailover
    TotalDbFailover totalDbFailover
    TotalElasticSearchFailover totalElasticSearchFailover
    AZFailover azFailover
}