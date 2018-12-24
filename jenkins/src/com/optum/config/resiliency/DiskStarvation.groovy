package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DiskStarvation implements Serializable
{
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int hdd // spawn N workers spinning on write()/unlink()
    String hddBytes // write B bytes per hdd worker (default is 1GB)
    boolean hddNoclean // do not unlink files created by hdd workers
    int timeout // # timeout after N seconds
    int numberOfTimes
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}