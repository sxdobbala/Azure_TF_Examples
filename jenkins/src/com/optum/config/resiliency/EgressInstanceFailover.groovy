package com.optum.config.resiliency

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class EgressInstanceFailover implements  Serializable {
    boolean runTest
    int percentOfNodesToTest
    String ec2ASGName
    int statusCheckSleepTimeInSeconds
    boolean dryRun
}
