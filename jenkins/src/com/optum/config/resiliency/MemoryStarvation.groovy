package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class MemoryStarvation implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int vm // spawn N workers spinning on malloc()/free()
    String vmBytes // malloc B bytes per vm worker (default is 256MB)
    String vmStride //touch a byte every B bytes (default is 4096)
    int vmHang // sleep N secs before free (default is none, 0 is inf)
    boolean vmKeep // redirty memory instead of freeing and reallocating
    int timeout // # timeout after N seconds
    int numberOfTimes
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}