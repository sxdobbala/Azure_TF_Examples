package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)

class TotalDbFailover implements  Serializable{
    boolean runTest
    String dbClusterId
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}
