package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Ec2Termination implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}