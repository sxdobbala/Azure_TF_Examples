package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class IoStarvation implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int io // spawn N workers spinning on sync()
    int timeout // # timeout after N seconds
    int numberOfTimes
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}