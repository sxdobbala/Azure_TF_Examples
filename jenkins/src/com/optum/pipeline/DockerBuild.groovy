#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def oseDockerBuild(Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def customExecution = new CustomScriptExecution()

    if (deploymentBranchSetting.docker.prompt != null && deploymentBranchSetting.docker.prompt == true)
    {
        try
        {
            def promptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.docker.promptAbortTimeout)
            timeout(time: promptAbortTimeout, unit: 'MINUTES')
            {
                input "Build and push Docker image?"
            }
        }
        catch(err)
        {
            notify.call(config, "Aborted ${err}")
            throw err
        }
    }
    
    if(deploymentBranchSetting.docker.preExecutionScript != null)
    {
        if(deploymentBranchSetting.docker.preExecutionScript.commandset != null)
        {
            customExecution.customExecutionLogic(deploymentBranchSetting.docker.preExecutionScript.commandset,deploymentBranchSetting.docker.preExecutionScript.credentials)
        }
    }

    withEnv(["DOCK_REPO=${deploymentBranchSetting.docker.repo}", 'DOCKER=/tools/docker/docker-17.06.1/docker', 'DOCKER_HOST=jenkins.optum.com:30303', 'DOCKER_HUB=docker.optum.com'])
    {
        withCredentials([[
                $class: 'UsernamePasswordMultiBinding',
                credentialsId: "${deploymentBranchSetting.docker.credentialsId}",
                passwordVariable: 'DOCKER_PW',
                usernameVariable: 'DOCKER_USER']])
        {
            try
            {
                def dockerfilePath = './'
                if (deploymentBranchSetting.docker.dockerfilePathExtension != null)
                {
                    dockerfilePath += deploymentBranchSetting.docker.dockerfilePathExtension
                }

                def pushTag = 'dev'

                if (deploymentBranchSetting.docker.tagIdentifier != null && !deploymentBranchSetting.docker.tagIdentifier.allWhitespace)
                {
                    pushTag = Helpers.getTag(pushTag, deploymentBranchSetting.docker.tagIdentifier)
                }
                dockerfilePath += '/Dockerfile'
                sh '''
                    . /etc/profile.d/jenkins.sh
                    $DOCKER -H $DOCKER_HOST images|grep "$DOCKER_USER/$DOCK_REPO"|awk \' { print $3 } \'|xargs -0 $DOCKER -H $DOCKER_HOST rmi -f > /dev/null || true
    
                    #build from docker file and login to registry
                    $DOCKER -H $DOCKER_HOST build -f ''' + dockerfilePath + ''' --force-rm --no-cache --pull --rm=true -t $DOCKER_USER/$DOCK_REPO .
                    $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
    
                    #push the images with different tags
                    $DOCKER -H $DOCKER_HOST tag $DOCKER_USER/$DOCK_REPO $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:latest
                    $DOCKER -H $DOCKER_HOST push $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:latest
    
                    $DOCKER -H $DOCKER_HOST tag $DOCKER_USER/$DOCK_REPO $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:''' + pushTag + '''
                    $DOCKER -H $DOCKER_HOST push $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:''' + pushTag 
                
                notify.call(config, 'Success - OSE Docker build completed')
            }
            catch (err)
            {
                notify.call(config, "Failed ${err}")
                currentBuild.result = 'FAILURE'
                throw err
            }
        }
    }
    if(deploymentBranchSetting.docker.postExecutionScript != null)
    {
        if(deploymentBranchSetting.docker.postExecutionScript.commandset != null)
        {
            customExecution.customExecutionLogic(deploymentBranchSetting.docker.postExecutionScript.commandset,deploymentBranchSetting.docker.postExecutionScript.credentials)
        }
    }
}

def tagImage(String pullTag, String pushTag, Boolean skipDeploy, String dockerRepo, Object deploymentBranchSetting, Config config)
{
    def notify = new Notify()
    withEnv(["DOCK_REPO=${deploymentBranchSetting.docker.repo}", 'DOCKER=/tools/docker/docker-17.06.1/docker', 'DOCKER_HOST=jenkins.optum.com:30303', 'DOCKER_HUB=docker.optum.com'])
    {
        withCredentials([[
                $class: 'UsernamePasswordMultiBinding',
                credentialsId: "${deploymentBranchSetting.docker.credentialsId}",
                passwordVariable: 'DOCKER_PW',
                usernameVariable: 'DOCKER_USER']])
        {
            try
            {
                def tagIdentifier = deploymentBranchSetting.docker.tagIdentifier
                
                if (tagIdentifier != null && !tagIdentifier.allWhitespace)
                {
                    pushTag = Helpers.getTag(pushTag, tagIdentifier)
                    pullTag = Helpers.getTag(pullTag, tagIdentifier)
                }
                
                sh '''
                    . /etc/profile.d/jenkins.sh
                    $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
                    $DOCKER -H $DOCKER_HOST pull $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:'''+ pullTag +'''
                    $DOCKER -H $DOCKER_HOST tag $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCKER_USER/'''+ dockerRepo+ ''':''' + pushTag + '''
                    $DOCKER -H $DOCKER_HOST push $DOCKER_HUB/$DOCKER_USER/'''+ dockerRepo+''':''' + pushTag

                if(skipDeploy == true && (pushTag.contains("test") || pushTag.contains("tst")))
                {
                    sh '''
                        . /etc/profile.d/jenkins.sh
                        $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
                        $DOCKER -H $DOCKER_HOST pull $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:'''+ pullTag +'''
                        $DOCKER -H $DOCKER_HOST tag $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:test-skipped 
                        $DOCKER -H $DOCKER_HOST push $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:test-skipped
                    '''
                }
                else if(skipDeploy == true && (pushTag.contains("stage") || pushTag.contains("stg")))
                {
                    sh '''
                        . /etc/profile.d/jenkins.sh
                        $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
                        $DOCKER -H $DOCKER_HOST pull $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:'''+ pullTag +'''
                        $DOCKER -H $DOCKER_HOST tag $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:stage-skipped
                        $DOCKER -H $DOCKER_HOST push $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:stage-skipped
                    '''
                }

                if(skipDeploy == false || skipDeploy == null )
                {
                    if(pushTag.contains("test") || pushTag.contains("tst"))
                    {
                        sh '''
                            . /etc/profile.d/jenkins.sh
                            $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
                            exists=`$DOCKER -H $DOCKER_HOST images -q $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:test-skipped`
                            if [ ! -z "$exists" ]; then
                                $DOCKER  -H $DOCKER_HOST rmi -f $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:test-skipped
                            fi
                        '''
                    }
                    if (pushTag.contains("stage") || pushTag.contains("stg"))
                    {
                        sh '''
                            . /etc/profile.d/jenkins.sh
                            $DOCKER -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $DOCKER_HUB
                            exists=`$DOCKER -H $DOCKER_HOST images -q $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:stage-skipped`
                            if [ ! -z "$exists" ]; then
                                $DOCKER  -H $DOCKER_HOST rmi -f $DOCKER_HUB/$DOCKER_USER/$DOCK_REPO:stage-skipped
                            fi
                        '''
                    }
                }
            }
            catch(err)
            {
                notify.call(config, "Failed ${err}")
                currentBuild.result = 'FAILURE'
                throw err
            }
        }
    }
}

def tagImageAws(String pullTag, String pushTag, Boolean skipDeploy, Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def basePipeline = new BasePipeline()

    withEnv(["DOCKER_HUB=${deploymentBranchSetting.docker.hub}", "DOCK_REPO=${deploymentBranchSetting.docker.repo}", "DOCKER=/tools/docker/docker-17.06.1/docker", "DOCKER_HOST=jenkins.optum.com:30303"])
    {
        basePipeline.awsAuth(deploymentBranchSetting.docker.credentialsId, deploymentBranchSetting.docker.awsAccountId)
        def tagIdentifier = deploymentBranchSetting.docker.tagIdentifier

        pushTag = Helpers.getTag(pushTag, tagIdentifier)
        pullTag = Helpers.getTag(pullTag, tagIdentifier)

        if (deploymentBranchSetting.helm.ecrAccountId != null)
        {
            registryId = " --registry-id " + deploymentBranchSetting.helm.ecrAccountId
        }

        try
        {
            sh '''
                . /etc/profile.d/jenkins.sh
                set +x
                export AWS_PROFILE=saml
                `aws ecr get-login --no-include-email`
                docker -H $DOCKER_HOST pull $DOCKER_HUB/$DOCK_REPO:''' + pullTag + '''
                docker -H $DOCKER_HOST tag  $DOCKER_HUB/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCK_REPO:''' + pushTag + '''
                docker -H $DOCKER_HOST push $DOCKER_HUB/$DOCK_REPO:''' + pushTag

            if(skipDeploy == true && pushTag.contains("test"))
            {
                sh'''
                . /etc/profile.d/jenkins.sh
                docker -H $DOCKER_HOST pull $DOCKER_HUB/$DOCK_REPO:''' + pullTag + '''
                docker -H $DOCKER_HOST tag  $DOCKER_HUB/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCK_REPO:test-skipped
                docker -H $DOCKER_HOST push $DOCKER_HUB/$DOCK_REPO:test-skipped
                '''
            }
            else if(skipDeploy == true && pushTag.contains("stage"))
            {
                sh'''
                . /etc/profile.d/jenkins.sh
                docker -H $DOCKER_HOST pull $DOCKER_HUB/$DOCK_REPO:''' + pullTag + '''
                docker -H $DOCKER_HOST tag  $DOCKER_HUB/$DOCK_REPO:''' + pullTag + ''' $DOCKER_HUB/$DOCK_REPO:stage-skipped
                docker -H $DOCKER_HOST push $DOCKER_HUB/$DOCK_REPO:stage-skipped
                '''
            }

            if(skipDeploy == false || skipDeploy == null )
            {
                if( pushTag.contains("test"))
                {
                    sh'''
                        . /etc/profile.d/jenkins.sh
                        TAG_EXISTS=`aws ecr list-images''' + registryId + ''' --repository-name $DOCK_REPO --filter tagStatus=TAGGED --output text --profile saml | grep test-skipped | awk '{ print $2}'`
                        if [ "$TAG_EXISTS" ]; then
                            aws ecr batch-delete-image''' + registryId +''' --repository-name $DOCK_REPO --image-ids imageTag=test-skipped --profile saml
                        fi
                    '''
                }
                if (pushTag.contains("stage"))
                {
                    sh '''
                       . /etc/profile.d/jenkins.sh
                       TAG_EXISTS=`aws ecr list-images''' + registryId + ''' --repository-name $DOCK_REPO --filter tagStatus=TAGGED --output text --profile saml| grep stage-skipped | awk '{ print $2}'`
                       if [ "$TAG_EXISTS" ]; then
                           aws ecr batch-delete-image''' + registryId +''' --repository-name $DOCK_REPO --image-ids imageTag=stage-skipped --profile saml
                       fi
                    '''
                }
            }
        }
        catch(err)
        {
            notify.call(config, "Failed ${err}")
            currentBuild.result = 'FAILURE'
            throw err
        }
    }
}

def awsDockerBuild(Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def basePipeline = new BasePipeline()
    def customExecution = new CustomScriptExecution()

    withEnv(["DOCKER_HUB=${deploymentBranchSetting.docker.hub}", "DOCK_REPO=${deploymentBranchSetting.docker.repo}", "DOCKER=/tools/docker/docker-17.06.1/docker", "DOCKER_HOST=jenkins.optum.com:30303", "AWSACCOUNTID=${deploymentBranchSetting.docker.awsAccountId}"])
    {
        try
        {
            if (deploymentBranchSetting.docker.prompt != null && deploymentBranchSetting.docker.prompt == true)
            {
                int promptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.docker.promptAbortTimeout)
                timeout(time: promptAbortTimeout, unit: 'MINUTES')
                {
                    input "Build and push Docker image to AWS ECR?"
                }
            }
            
            if(deploymentBranchSetting.docker.preExecutionScript != null)
            {
                if(deploymentBranchSetting.docker.preExecutionScript.commandset != null)
                {
                    customExecution.customExecutionLogic(deploymentBranchSetting.docker.preExecutionScript.commandset,deploymentBranchSetting.docker.preExecutionScript.credentials)
                }
            }
            
            env.PATH = "/tools/docker/docker-17.06.1/:${env.PATH}"

            def dockerfilePath = './'
            if (deploymentBranchSetting.docker.dockerfilePathExtension != null)
            {
                dockerfilePath += deploymentBranchSetting.docker.dockerfilePathExtension
            }
            dockerfilePath += '/Dockerfile'

            basePipeline.awsAuth(deploymentBranchSetting.docker.credentialsId, deploymentBranchSetting.docker.awsAccountId)

            def pushTag = 'dev'

            if (deploymentBranchSetting.docker.tagIdentifier != null && !deploymentBranchSetting.docker.tagIdentifier.allWhitespace)
            {
                pushTag = Helpers.getTag(pushTag, deploymentBranchSetting.docker.tagIdentifier)
            }

            sh '''
                . /etc/profile.d/jenkins.sh
                #set +x
                export AWS_PROFILE=saml
                
                docker -H $DOCKER_HOST build -f ''' + dockerfilePath + ''' --force-rm --no-cache --pull --rm=true -t $DOCKER_HUB/$DOCK_REPO .
                #push the images with different tags
                `aws ecr get-login --no-include-email`
                docker -H $DOCKER_HOST push $DOCKER_HUB/$DOCK_REPO:latest
                docker -H $DOCKER_HOST tag $DOCKER_HUB/$DOCK_REPO:latest $DOCKER_HUB/$DOCK_REPO:''' + pushTag +'''
                docker -H $DOCKER_HOST push $DOCKER_HUB/$DOCK_REPO:'''+ pushTag

            notify.call(config, 'Success - AWS Docker build completed')

            if(deploymentBranchSetting.docker.backupDtr != null && deploymentBranchSetting.docker.backupDtr == true)
            {
                stage("Deploy to Optum Docker Registry")
                {
                    withCredentials([
                        [
                            $class: 'UsernamePasswordMultiBinding',
                            credentialsId: "${deploymentBranchSetting.docker.backupDtrCredentialsId}",
                            passwordVariable: 'DOCKER_PW',
                            usernameVariable: 'DOCKER_USER']
                    ])
                    {
                        try
                        {
                            sh '''
                                . /etc/profile.d/jenkins.sh
                                OPTUMDOCK_REPO=$DOCKER_USER/aws_$(echo $DOCK_REPO | sed -e 's?/?_?g')
                                OPTUMDOCK_HUB=docker.optum.com
                                docker -H $DOCKER_HOST login -u $DOCKER_USER -p $DOCKER_PW $OPTUMDOCK_HUB
                                docker -H $DOCKER_HOST tag $DOCKER_HUB/$DOCK_REPO $OPTUMDOCK_HUB/$OPTUMDOCK_REPO
                                docker -H $DOCKER_HOST push $OPTUMDOCK_HUB/$OPTUMDOCK_REPO
                            '''
                            notify.call(config, 'Success - AWS Docker push to Optum Docker completed')
                        }
                        catch(err)
                        {
                            notify.call(config, "Failed ${err}")
                            currentBuild.result = 'FAILURE'
                            throw err
                        }
                    }
                }
            }
            if(deploymentBranchSetting.docker.postExecutionScript != null)
            {
                if(deploymentBranchSetting.docker.postExecutionScript.commandset != null)
                {
                    customExecution.customExecutionLogic(deploymentBranchSetting.docker.postExecutionScript.commandset,deploymentBranchSetting.docker.postExecutionScript.credentials)
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