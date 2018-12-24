# Resiliency Testing

Resiliency testing is a way of testing the resiliency of a system by creating chaos or injecting failures into our environments. More details about our test plan can be found in our [Resiliency Testing wiki](https://github.optum.com/cloud-idp/everything-as-code/wiki/Resiliency%5BChaos%5D-Testing-Embrace-the-failure).

## How it works

1. All requirements are specified in the config.yml file.
1. Use Jenkins job to trigger the test:

```groovy
@Library('ResiliencyLibrary') _
def resiliencyInit = new com.optum.resiliency.ResiliencyTesting().init()
```

A comprehensive yaml script may look like this. Details on expectations on each run can be found in our [Resiliency Testing wiki](https://github.optum.com/cloud-idp/everything-as-code/wiki/Resiliency%5BChaos%5D-Testing-Embrace-the-failure).

```yml
profile: stage
awsCredentialsId: AWS_OID_STAGE_SERVICE_ACCOUNT
awsAccountId: 757541135089
runMode: linear
dbFailover:
    runTest: true
    dbClusterId: oid-stage-aurora-cluster
    statusCheckSleepTimeInSeconds: 30
    dryRun: false
    masterInstanceCrashUsingFaultInjection: true
ec2Termination:
    runTest: true
    percentOfNodesToTest: 25
    ec2ASGName: nodes.k8s.stage.ctkube.com
    statusCheckSleepTimeInSeconds: 30
    dryRun: false
cpuStarvation:
    runTest: true
    percentOfNodesToTest: 25
    cpu: 8 # spawn N workers spinning on sqrt()
    timeout: 300 # timeout after N seconds
    numberOfTimes: 10
    statusCheckSleepTimeInSeconds: 30
    ec2ASGName: nodes.k8s.stage.ctkube.com
    dryRun: false
memoryStarvation:
    runTest: true
    percentOfNodesToTest: 25
    vm: 1 # spawn N workers spinning on malloc()/free()
    vmBytes: 28G # malloc B bytes per vm worker (default is 256MB)
    vmStride: 4096 # touch a byte every B bytes (default is 4096)
    vmHang: 120 # sleep N secs before free (default is none, 0 is inf)
    vmKeep: true # redirty memory instead of freeing and reallocating
    timeout: 300 # timeout after N seconds
    numberOfTimes: 5
    statusCheckSleepTimeInSeconds: 120
    ec2ASGName: nodes.k8s.stage.ctkube.com
    dryRun: false
diskStarvation:
    runTest: true
    percentOfNodesToTest: 25
    ec2ASGName: nodes.k8s.stage.ctkube.com
    hdd: 10 # spawn N workers spinning on write()/unlink()
    hddBytes: 12G # write B bytes per hdd worker (default is 1GB)
    hddNoclean: false # do not unlink files created by hdd workers
    timeout: 1100 # timeout after N seconds
    numberOfTimes: 1
    statusCheckSleepTimeInSeconds: 60
    dryRun: false
ioStarvation:
    runTest: true
    percentOfNodesToTest: 25
    ec2ASGName: nodes.k8s.stage.ctkube.com
    io: 10 # spawn N workers spinning on sync()
    timeout: 900 # timeout after N seconds
    numberOfTimes: 1
    statusCheckSleepTimeInSeconds: 60
    dryRun: false
redisFailover:
    runTest: true
    redisReplicationGroupId: redis-stage
    statusCheckSleepTimeInSeconds: 30
    dryRun: false
egressInstanceFailover:
    runTest: true
    percentOfNodesToTest: 100
    ec2ASGName: egress-proxy
    statusCheckSleepTimeInSeconds: 30
    dryRun: false
totalRedisFailover:
    runTest: true
    redisReplicationGroupId: redis-stage
    statusCheckSleepTimeInSeconds: 30
    dryRun: false
totalDbFailover:
    runTest: true
    dbClusterId: oid-stage-aurora-cluster
    statusCheckSleepTimeInSeconds: 30
    dryRun: true
totalElasticSearchFailover:
    runTest: true
    domainID: oid-stage-logs
    statusCheckSleepTimeInSeconds: 30
    dryRun: true
azFailover:
    runTest: true
    percentOfNodesToTest: 50
    statusCheckSleepTimeInSeconds: 30
```
