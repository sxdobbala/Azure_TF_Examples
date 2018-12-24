#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def deploy(String branchName, Config config, Object deploymentBranchSetting)
{
    def envs = deploymentBranchSetting.aws.deployToAwsEnv
    def dockerBuild = new DockerBuild()
    def notify = new Notify()
    def customExecution = new CustomScriptExecution()

    if(envs != null)
    {
        for (i = 0; i < envs.size(); i++)
        {
            stage ("Deploy to AWS ${envs[i].envName} environment")
            {
                try
                {
                    if (deploymentBranchSetting.aws.prompt != null && deploymentBranchSetting.aws.prompt == true)
                    {
                        def deployPromptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.aws.deployPromptAbortTimeout)
                        timeout(time: deployPromptAbortTimeout, unit: 'MINUTES')
                        {
                            input "Deploy to AWS ${envs[i].envName} Environment?"
                        }
                    }
					
                    if(deploymentBranchSetting.aws.preExecutionScript != null)
                    {
                        if(deploymentBranchSetting.aws.preExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.aws.preExecutionScript.commandset,deploymentBranchSetting.aws.preExecutionScript.credentials)
                        }
                    }
					
                    if(envs[i].envName == 'dev' )
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, deploymentBranchSetting.aws.yamlName)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "dev", envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, awsUrl, config, deploymentBranchSetting)
                    }
                    if (envs[i].envName == 'test' && envs[i].testTag == null)
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('dev', 'test', config, deploymentBranchSetting)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "dev", envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId,awsUrl, config, deploymentBranchSetting)
                        // test deploy
                    }
                    else if (envs[i].envName == 'test' && envs[i].testTag != null)
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('dev', envs[i].testTag, config, deploymentBranchSetting)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "test", envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, awsUrl, config, deploymentBranchSetting)
                        // test deploy
                    }
                    else if (envs[i].envName == 'stage' || envs[i].envName == 'stg')
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('test', 'stage', config, deploymentBranchSetting)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "stage", envs[i].kubeconfigfileCredentialsId,envs[i].awsCredentialsId, awsUrl, config, deploymentBranchSetting)
                    }
                    else if (envs[i].envName == 'demo' || envs[i].envName == 'demo')
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('stage', 'demo', config, deploymentBranchSetting)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "demo", envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, awsUrl, config, deploymentBranchSetting)
                    }
                    else if (envs[i].envName == 'nonprod')
                    {
                        def namespace = Helpers.getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('stage', 'nonprod', config, deploymentBranchSetting)
                    }
                    else if (envs[i].envName == 'prod' || envs[i].envName == 'prd')
                    {
                        def namespace = getNameSpace(envs[i].namespace, deploymentBranchSetting)
                        def awsUrl = Helpers.Helpers.getAWSUrl(branchName, config, envs[i].yamlName)
                        dockerBuild.tagImageAws('stage', 'prod', config, deploymentBranchSetting)
                        standardDeploy(branchName, namespace, envs[i].yamlName, "prod", envs[i].kubeconfigfileCredentialsId,envs[i].awsCredentialsId, awsUrl,config, deploymentBranchSetting)
                    }

                    notify(config, "Success - Deployed to AWS ${envs[i].envName} Environment")
					
                    if(deploymentBranchSetting.aws.postExecutionScript != null)
                    {
                        if(deploymentBranchSetting.aws.postExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.aws.postExecutionScript.commandset,deploymentBranchSetting.aws.postExecutionScript.credentials)
                        }
                    }
                }
                catch(err)
                {
                    notify(config, "Failed ${err}")
                    currentBuild.result = 'FAILURE'
                    throw err
                }
            }
        }
    }
}

def standardDeploy(String branchName, String namespace, String yamlName, String dockerTag, String kubeconfigfileCredentialsId, String, awsCredentialsId, String awsUrl, Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def customExecution = new CustomScriptExecution()

        withEnv([
            "DOCKER=/tools/docker/docker-1.6.0/docker",
            "DOCKER_HOST=jenkins.optum.com:30303",
            "DOCKER_HUB=${deploymentBranchSetting.docker.hub}",
            "DOCKER_REPO=${deploymentBranchSetting.docker.repo}",
            "NAMESPACE=${namespace}",
            "APP=${deploymentBranchSetting.aws.app}",
            "dockerTag=${dockerTag}",
            "YAMLNAME=${yamlName}"])
        {
            withCredentials([string(credentialsId: "${deploymentBranchSetting.aws.gitHubToken}", variable: 'token')])
            {
                withCredentials([file(credentialsId: "${kubeconfigfileCredentialsId}", variable: 'kubeconfig')])
                {
                    try
                    {
                        if(deploymentBranchSetting.aws.preExecutionScript != null)
                        {
                            if(deploymentBranchSetting.aws.preExecutionScript.commandset != null)
                            {
                                customExecution.customExecutionLogic(deploymentBranchSetting.aws.preExecutionScript.commandset,deploymentBranchSetting.aws.preExecutionScript.credentials)
                            }
                        }

                        withCredentials([usernamePassword(credentialsId: "${awsCredentialsId}", usernameVariable: 'USER', passwordVariable: 'PASS')])
                        {
                             sh '''
                             . /etc/profile.d/jenkins.sh
                             # Export Python 3 and execute the jenkins mixin scripts
                             export PYTHON_VERSION=3.6
                              . /etc/profile.d/jenkins.sh
            
                             # Download python script and files to authenticate to AWS
                            export AUTH_LOC=$HOME/aws-cli-saml
                            rm -rf $AUTH_LOC
                            mkdir $AUTH_LOC
                            cd $AUTH_LOC
                            for file in authenticate_py3.py prod.cer sandbox.cer; do \
                             curl https://github.optum.com/raw/CommercialCloud-EAC/python-scripts/master/aws-cli-saml/$file > $AUTH_LOC/$file
                            done;
                            export AWS_SAML_ROLE="arn:aws:iam::${AWSACCOUNTID}:role/AWS_${AWSACCOUNTID}_Service"
                            python3 authenticate_py3.py -u ${USER} -p ${PASS}
                           '''
                        }
                        
                        sh "curl -H 'Authorization: token $token' -H 'Accept: application/vnd.github.v3.raw' -O -L ${awsUrl}"
                        sh '''
                            . /etc/profile.d/jenkins.sh
                            #set +x
                            export AWS_PROFILE=saml
                            DIGEST=`aws ecr list-images --repository-name $DOCKER_REPO --filter tagStatus=TAGGED --output text|grep $dockerTag|awk '{ print $2}'`
                            mkdir -p ~/.kube
                            cat $kubeconfig > ~/.kube/kubeconfig
                            export KUBECONFIG=~/.kube/kubeconfig
                            # implment refresh token for k8s api login
                            BUILD_CONFIG=`kubectl get deployment -n ${NAMESPACE}| grep ${APP} |awk \'{print $1}\'`
                            if [ "$BUILD_CONFIG" == "$APP" ]; then
                                kubectl patch deployment $APP --patch "{\\"spec\\": {\\"template\\": {\\"spec\\": {\\"containers\\": [{\\"name\\": \\"tomcat\\",\\"image\\":\\"$DOCKER_HUB/$DOCK_REPO@$DIGEST\\"}]}}}}" -n ${NAMESPACE}
                                echo "Rolling update in progress"
                            else
                                kubectl create -f $YAMLNAME -n $NAMESPACE --record
                                echo "Application deployment in progress"
                            fi
                        '''
                        if(deploymentBranchSetting.aws.postExecutionScript != null)
                        {
                            if(deploymentBranchSetting.aws.postExecutionScript.commandset != null)
                            {
                                customExecution.customExecutionLogic(deploymentBranchSetting.aws.postExecutionScript.commandset,deploymentBranchSetting.aws.postExecutionScript.credentials)
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
        }
}


