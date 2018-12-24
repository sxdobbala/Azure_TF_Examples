#!/usr/bin/env groovy
package com.optum.patchmanagement

import com.optum.pipeline.BasePipeline
import com.optum.pipeline.Notify
import com.optum.config.patchmanagement.Config
import com.optum.utils.Helpers

properties(
        [
            parameters(
            [
                choice(
                choices: 'dev\nstage\nnonprod\nprod',
                description: 'Environment name',
                name: 'profile')
            ]),
            pipelineTriggers([])
        ])


def init()
{
    init("config.yml")
}

def init(String configFile)
{
    Config config
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

    def jenkinsNode = Helpers.getNode(config.jenkinsNode)
    node(jenkinsNode)
    {
        stage('Validate config')
        {
            if (!config.awsAccountId || !config.awsCredentialsId || !config.awsEnv || !config.kubeconfigfileCredentialsId)
            {
                currentBuild.result = "FAILURE"
                error("Key information missing in config.yml. The required fields are awsAccountId, awsCredentialsID, awsProfile and kubeconfigfileCredentialsId.")
            }
        }

        patchManagement(config)
    }
}


def patchManagement(Config config)
{
    def basePipeline = new BasePipeline()
    def notify = new Notify()

    stage('Configure AWS')
    {
        try
        {
            basePipeline.awsAuth(config.awsCredentialsId, config.awsAccountId)
        }
        catch (err)
        {
            currentBuild.result = "FAILURE"
            notify(config, "Failed to log into AWS.\n$err")
        }
    }


    checkout scm

    profile = params.profile

    def awsEnv = config.awsEnv

    awsEnv.each
    { awsProfile ->
        if(awsProfile.envName == profile)
        {
            def autoscalingGroups = awsProfile.autoscalingGroups
            autoscalingGroups.each
            { autoscalingGroup ->
                drainAndTerminateNodes(autoscalingGroup, config)
            }
        }
    }
}

def drainAndTerminateNodes(String autoscalingGroup, Config config)
{
    withCredentials([
        file(credentialsId: "${config.kubeconfigfileCredentialsId}", variable: 'kubeconfig')
    ])
    {
        stage("Drain and terminate nodes in autoscaling group " + autoscalingGroup)
        {
            String ec2Instances = sh(script: "aws ec2 describe-instances --filters \"Name=tag:aws:autoscaling:groupName,Values=${autoscalingGroup}\" --query 'Reservations[*].Instances[*].PrivateDnsName' --output text --profile saml", returnStdout: true).trim()
            String[] ec2InstanceArray = ec2Instances.split(/\s+|\n/)

            ec2InstanceArray.each
            { privateDnsName ->
                String allPods = sh(script: '''
                                             . /etc/profile.d/jenkins.sh >>/dev/null 2>&1
                                             export AWS_PROFILE=saml
                                             mkdir -p ~/.kube
                                             cat $kubeconfig > ~/.kube/kubeconfig
                                             export KUBECONFIG=~/.kube/kubeconfig
                                             kubectl get pods --all-namespaces -o=jsonpath='{range .items[?(@.spec.nodeName==\"''' + privateDnsName + '''\")]}{.spec.nodeName}{"\\t"}{.metadata.namespace}{"\\t"}{.metadata.name}{"\\t"}{.status.phase}{"\\n"}{end}' 
                                         ''', returnStdout: true).trim()

                String[] podsArray = allPods.split(/\n/)
                String nonRunningPods;

                podsArray.each
                { pod ->
                    if (!pod.endsWith("Running"))
                    {
                        nonRunningPods += pod + "\n"
                    }
                }

                if ( nonRunningPods != null && nonRunningPods.length() > 0)
                {
                    println "We cannot proceed with node drain and termination because there are pods that are not in 'Running' status:\n" + nonRunningPods
                    break;
                }

                println "Draining node $privateDnsName"
                sh '''
               . /etc/profile.d/jenkins.sh >>/dev/null 2>&1
               export AWS_PROFILE=saml
               mkdir -p ~/.kube
               cat $kubeconfig > ~/.kube/kubeconfig
               export KUBECONFIG=~/.kube/kubeconfig
               kubectl drain ''' + privateDnsName + ''' --ignore-daemonsets --force --delete-local-data '''

                String instanceId = sh(script: 'aws ec2 describe-instances --filters \"Name=private-dns-name,Values=' + privateDnsName + '\" --query \'Reservations[*].Instances[*].InstanceId\' --output text --profile saml', returnStdout: true).trim()
                println "Terminating node $instanceId"
                sh "aws ec2 terminate-instances --instance-ids " + instanceId + " --profile saml"
                sleep 180
            }
            String ec2InstancesAfterPatch = sh(script: "aws ec2 describe-instances --filters \"Name=tag:aws:autoscaling:groupName,Values=${autoscalingGroup}\" \"Name=instance-state-name, Values=running\" --output text --profile saml", returnStdout: true).trim()
            String[] ec2InstanceArrayAfterPatch = ec2Instances.split(/\s+|\n/)

            if(ec2InstanceArray.size() == ec2InstanceArrayAfterPatch.size())
            {
                println "Patch for nodes has been completed."
            }
            else
            {
                error "Patch for nodes is not completed, please check all the node and make sure they are running."
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