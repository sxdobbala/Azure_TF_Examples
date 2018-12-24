#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def npmBuild(String branchName, Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()

    withEnv([
        'NODEJS_VERSION=6.5.0',
        'NPM_AUTH_KEY=""'
    ])
    {
        try
        {
            stage ('NPM Build')
            {
                String stashName = deploymentBranchSetting.ui.stashName
                String stashDir = deploymentBranchSetting.ui.stashDirectory + '/**/*'
                String gitUrl = config.project.gitUrl
                String credential = config.project.credentialsId

                git branch: branchName, url: gitUrl, credentialsId: credential

                sh '''
                    . /etc/profile.d/jenkins.sh
                    node --version
                    npm cache clean   
                    npm install gulp@3.9.1
                    npm install gulp-cli
                    npm install is-property
                    npm install
                    bower install --allow-root
                    gulp default --cloudEnv=aws --ci
                    ls -lisah tb
                '''

                stash name: stashName, includes: stashDir
                notify.call(config, "Stashed ${stashDir} folder to be used during build promotion")
                notify.call(config, "Success - Need Approval for dev environment deploy")
            }
        }
        catch (err)
        {
            notify.call(config, "Failed ${err}")
            currentBuild.result = 'FAILURE'
            throw err
        }
    }
}

def deploy(Config config, Object deploymentBranchSetting)
{
    Notify notify = new Notify()
    def basePipeline = new BasePipeline()
    def customExecution = new CustomScriptExecution()

        withEnv([
            'NODEJS_VERSION=6.5.0',
            'NPM_AUTH_KEY=""'
        ])
        {
            try
            {
                if(deploymentBranchSetting.ui.preExecutionScript != null)
                {
                    if(deploymentBranchSetting.ui.preExecutionScript.commandset != null)
                    {
                        customExecution.customExecutionLogic(deploymentBranchSetting.ui.preExecutionScript.commandset,deploymentBranchSetting.ui.preExecutionScript.credentials)
                    }
                }

                String stashName = deploymentBranchSetting.ui.stashName
                String stashDir = deploymentBranchSetting.ui.stashDirectory
                dir(stashName) { unstash stashName }
                String stash = stashName + '/' + stashDir
                def envs = deploymentBranchSetting.ui.deployToEnv

                for (int i = 0; i < envs.length; i++)
                {
                    def deployToEnv = envs[i]
                    stage("Deploy UI to ${deployToEnv.envName} environment")
                    {
                        def deployPromptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.ui.deployPromptAbortTimeout)
                        timeout(time: deployPromptAbortTimeout, unit: 'MINUTES')
                        {
                            input "Deploy UI to ${deployToEnv.envName} environment?"
                        }

                        basePipeline.awsAuth(deployToEnv.awsCredentialsId, deployToEnv.awsAccountId)
                        sh"""
                        . /etc/profile.d/jenkins.sh
                        aws s3 sync ${stash} s3://${deployToEnv.s3Bucket} --profile saml
                        """
                        notify.call(config, "Success - Deployed to ${deployToEnv.envName} environment.")
                    }
                }
				
                if(deploymentBranchSetting.ui.postExecutionScript != null)
                {
                    if(deploymentBranchSetting.ui.postExecutionScript.commandset != null)
                    {
                        customExecution.customExecutionLogic(deploymentBranchSetting.ui.postExecutionScript.commandset,deploymentBranchSetting.ui.postExecutionScript.credentials)
                    }
                }
            }
            catch (err)
            {
                notify.call(config, "Failed ${err}")
                currentBuild.result = 'FAILURE'
                throw err
            }
        }
}
