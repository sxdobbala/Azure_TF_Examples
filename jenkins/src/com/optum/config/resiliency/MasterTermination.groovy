package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class MasterTermination implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}