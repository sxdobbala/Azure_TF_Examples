package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class CpuStarvation implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int cpu // spawn N workers spinning on sqrt()
    int timeout // # timeout after N seconds
    int numberOfTimes
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}