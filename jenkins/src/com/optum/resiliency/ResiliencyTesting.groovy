package com.optum.resiliency

import com.optum.config.resiliency.Config
import com.optum.pipeline.BasePipeline
import com.optum.resiliency.CpuStarvation
import com.optum.resiliency.DbFailover
import com.optum.resiliency.DiskStarvation
import com.optum.resiliency.Ec2Termination
import com.optum.resiliency.IoStarvation
import com.optum.resiliency.MemoryStarvation
import com.optum.resiliency.RedisFailover
import com.optum.utils.Helpers
import com.optum.utils.TimeTakenForTests

def init()
{
    init("config.yml")
}

def init(String configFile)
{
    node("docker-aws-slave")
    {
        Config config
        CpuStarvation cpuStarvation = new CpuStarvation()
        DbFailover dbFailover = new DbFailover()
        DiskStarvation diskStarvation = new DiskStarvation()
        Ec2Termination ec2Termination = new Ec2Termination()
        IoStarvation ioStarvation = new IoStarvation()
        MemoryStarvation memoryStarvation = new MemoryStarvation()
        RedisFailover redisFailover = new RedisFailover()
        EgressInstanceFailover egressInstanceFailover = new EgressInstanceFailover()
        TotalRedisFailover totalRedisFailover = new TotalRedisFailover()
        TotalDbFailover totalDbFailover = new TotalDbFailover()
        TotalElasticSearchFailover totalElasticSearchFailover = new TotalElasticSearchFailover()
        AZFailover azFailover = new AZFailover()
        int promptTimeout = 10 // minutes
        BasePipeline basePipeline = new BasePipeline()

        stage('Load config')
        {
            try
            {
                config = readConfig(configFile)
            }
            catch(err)
            {
                currentBuild.result = "FAILURE"
                error("Missing config file $configFile.\n$err")
            }
        }

        stage('Validate config')
        {
            if (!config.awsAccountId || !config.awsCredentialsId || !config.profile)
            {
                currentBuild.result = "FAILURE"
                error("Key information missing in config.yml. The required fields are awsAccountId, awsCredentialsID, and profile.")
            }
        }

        stage('Configure AWS')
        {
            try
            {
                basePipeline.awsAuth(config.awsCredentialsId, config.awsAccountId)
            }
            catch (err)
            {
                currentBuild.result = "FAILURE"
                error("Failed to log into AWS.\n$err")
            }
        }

        if (config.dbFailover && config.dbFailover.runTest && config.dbFailover.runTest == true)
        {
            stage('Database Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: DB Failover?"
                }
                
                try
                {
                    dbFailover.call(config.dbFailover)
                }
                catch (err)
                {
                    println "Database failover failed.\n$err"
                }
            }
        }

        if (config.redisFailover && config.redisFailover.runTest && config.redisFailover.runTest == true)
        {
            stage('Redis Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Redis Failover?"
                }
                
                try
                {
                    redisFailover.call(config.redisFailover)
                }
                catch(err)
                {
                    println "Redis failover failed.\n$err"
                }
            }
        }

        if (config.ec2Termination && config.ec2Termination.runTest && config.ec2Termination.runTest == true)
        {
            stage('EC2 Termination')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: EC2 Termination?"
                }
                
                try
                {
                    ec2Termination.call(config.ec2Termination)
                }
                catch(err)
                {
                    println "EC2 termination test failed.\n$err"
                }
            }
        }

        if (config.cpuStarvation && config.cpuStarvation.runTest && config.cpuStarvation.runTest == true)
        {
            stage('CPU Starvation')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: CPU Starvation?"
                }
                
                try
                {
                    cpuStarvation.call(config.cpuStarvation)
                }
                catch(err)
                {
                    println "CPU starvation test failed.\n$err"
                }
            }
        }

        if (config.memoryStarvation && config.memoryStarvation.runTest && config.memoryStarvation.runTest == true)
        {
            stage('Memory Starvation')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Memory Starvation?"
                }
                
                try
                {
                    memoryStarvation.call(config.memoryStarvation)
                }
                catch(err)
                {
                    println "Memory starvation test failed.\n$err"
                }
            }
        }

        if (config.diskStarvation && config.diskStarvation.runTest && config.diskStarvation.runTest == true)
        {
            stage('Disk Starvation')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Disk Starvation?"
                }
                
                try
                {
                    diskStarvation.call(config.diskStarvation)
                }
                catch(err)
                {
                    println "Disk starvation test failed.\n$err"
                }
            }
        }

        if (config.ioStarvation && config.ioStarvation.runTest && config.ioStarvation.runTest == true)
        {
            stage('IO Starvation')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: IO Starvation?"
                }
                
                try
                {
                    ioStarvation.call(config.ioStarvation)
                }
                catch(err)
                {
                    println "IO starvation test failed.\n$err"
                }
            }
        }

        if (config.egressInstanceFailover && config.egressInstanceFailover.runTest && config.egressInstanceFailover.runTest == true)
        {
            stage('Egress Instance Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Egress Instance Failover?"
                }
                
                try
                {
                    egressInstanceFailover.call(config.egressInstanceFailover)
                }
                catch(err)
                {
                    println "Engress Instance Failover test failed.\n$err"
                }
            }
        }

        if (config.totalRedisFailover && config.totalRedisFailover.runTest && config.totalRedisFailover.runTest == true)
        {
            stage('Redis Complete 100% Instance Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Total Redis Failover?"
                }
                
                try
                {
                    totalRedisFailover.call(config.totalRedisFailover)
                }
                catch(err)
                {
                    println "Redis Instance 100% failover test failed.\n$err"
                }
            }
        }

        if (config.totalDbFailover && config.totalDbFailover.runTest && config.totalDbFailover.runTest == true)
        {
            stage('DB Complete 100% Instance Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Total DB Failover?"
                }
                
                try
                {
                    totalDbFailover.call(config.totalDbFailover)
                }
                catch(err)
                {
                    println "DB Instance 100% failover test failed.\n$err"
                }
            }
        }

        if (config.totalElasticSearchFailover && config.totalElasticSearchFailover.runTest && config.totalDbFailover.runTest == true)
        {
            stage('Elastic Search Complete 100% Instance Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Total Elastic Search Failover?"
                }
                
                try
                {
                    totalDbFailover.call(config.totalElasticSearchFailover)
                }
                catch(err)
                {
                    println "Elastic Search 100% failover test failed.\n$err"
                }
            }
        }

        if (config.azFailover && config.azFailover.runTest && config.azFailover.runTest == true)
        {
            stage('Availability Zone random Failover')
            {
                timeout(time: promptTimeout, unit: 'MINUTES')
                {
                    input "Continue with the test: Availability Zone Failover?"
                }
                
                TimeTakenForTests timeTakenForTest = new TimeTakenForTests()
                timeTakenForTest.setTestStartTime()
                try
                {
                    azFailover.call(config.azFailover)
                }
                catch(err)
                {
                    println "Availability Zone random Failover.\n$err"
                }
                timeTakenForTest.setTestEndTime()
            }
        }
    }
}

Config readConfig(String configFileName)
{
    node('docker-maven-slave')
    {
        checkout scm
        def configFile = readYaml file: configFileName
        return Helpers.convertValue(configFile, Config.class)
    }
}
