#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def deploy(String branchName, Config config, Object deploymentBranchSetting)
{
    def envs = deploymentBranchSetting.helm.deployToHelmEnv
    def namespace = deploymentBranchSetting.helm.namespace
    def dockerBuild = new DockerBuild()
    def notify = new Notify()
    def contrast = new Contrast()
    def customExecution = new CustomScriptExecution()

    if(envs != null)
    {
        for (i = 0; i < envs.size(); i++)
        {
            stage("Deploy to AWS ${envs[i].envName} environment with helm")
            {
                try
                {
                    if (deploymentBranchSetting.helm.deployPrompts != null && deploymentBranchSetting.helm.deployPrompts == true)
                    {
                        def deployPromptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.helm.deployPromptAbortTimeout)
                        timeout(time: deployPromptAbortTimeout, unit: 'MINUTES')
                        {
                            input "Deploy to AWS ${envs[i].envName} Environment with helm?"
                        }
                    }

                    if(deploymentBranchSetting.helm.preExecutionScript != null)
                    {
                        if(deploymentBranchSetting.helm.preExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.helm.preExecutionScript.commandset,deploymentBranchSetting.helm.preExecutionScript.credentials)
                        }
                    }

                    def skipDeploy = envs[i].skipDeploy
                    if(envs[i].envName == 'dev')
                    {
                        pullTag = Helpers.getTag('dev', deploymentBranchSetting.docker.tagIdentifier)
                        standardDeploy(branchName, namespace, envs[i].yamlName, pullTag, envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, envs[i].awsAccountId, config, deploymentBranchSetting)
                        verifyDeployment(deploymentBranchSetting.helm.releaseName, namespace,envs[i].kubeconfigfileCredentialsId, config.notificationEmail, deploymentBranchSetting.helm.app)
                        if(deploymentBranchSetting.contrast != null)
                        {
                            contrast.verifyContrastReport(config, deploymentBranchSetting.contrast)
                        }
                        else
                        {
                            println("Deprecated: Please setup Contrast scan with your application. This will be required in future versions.")
                        }
                    }
                    else if (envs[i].envName == 'test')
                    {
                        dockerBuild.tagImageAws('dev', 'test', skipDeploy, config, deploymentBranchSetting)
                        pullTag = Helpers.getTag('test', deploymentBranchSetting.docker.tagIdentifier)
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            standardDeploy(branchName, namespace, envs[i].yamlName, pullTag, envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, envs[i].awsAccountId, config, deploymentBranchSetting)
                        }
                    }
                    else if (envs[i].envName == 'stage' || envs[i].envName == 'stg')
                    {
                        dockerBuild.tagImageAws('test', 'stage', skipDeploy, config, deploymentBranchSetting)
                        pullTag = Helpers.getTag('stage', deploymentBranchSetting.docker.tagIdentifier)
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            standardDeploy(branchName, namespace, envs[i].yamlName, pullTag, envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, envs[i].awsAccountId, config, deploymentBranchSetting)
                        }
                    }
                    else if (envs[i].envName == 'nonprod')
                    {
                        dockerBuild.tagImageAws('stage', 'nonprod', skipDeploy, config, deploymentBranchSetting)
                        pullTag = Helpers.getTag('nonprod', deploymentBranchSetting.docker.tagIdentifier)
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            standardDeploy(branchName, namespace, envs[i].yamlName, pullTag, envs[i].kubeconfigfileCredentialsId, envs[i].awsCredentialsId, envs[i].awsAccountId, config, deploymentBranchSetting)
                        }
                    }

                    if(skipDeploy == true)
                    {
                        notify(config, "Build is successful - Deployment to ${envs[i].envName} environment with Helm has been skipped")
                    }
                    else
                    {
                        notify(config, "Success - Deployed to ${envs[i].envName} environment with Helm")
                    }
                    
                    if(deploymentBranchSetting.helm.postExecutionScript != null)
                    {
                        if(deploymentBranchSetting.helm.postExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.helm.postExecutionScript.commandset,deploymentBranchSetting.helm.postExecutionScript.credentials)
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

def standardDeploy(String branchName, String namespace, String yamlName, String dockerTag, String kubeconfigfileCredentialsId, String awsCredentialsId, String awsAccountId, Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def basePipeline = new BasePipeline()
    def customExecution = new CustomScriptExecution()

    withEnv([
        "DOCKER_REPO=${deploymentBranchSetting.docker.repo}",
        "APP=${deploymentBranchSetting.helm.app}",
        "NAMESPACE=${namespace}",
        "RELEASENAME=${deploymentBranchSetting.helm.releaseName}",
        "CHARTNAME=${deploymentBranchSetting.helm.chartName}",
        "YAMLNAME=${yamlName}",
        "DOCKER_TAG=${dockerTag}",
        "BUILD_ID=${env.BUILD_ID}"])
    {
        withCredentials([
            string(credentialsId: "${deploymentBranchSetting.helm.gitHubToken}", variable: 'token')])
        {
            withCredentials([
                file(credentialsId: "${kubeconfigfileCredentialsId}", variable: 'kubeconfig')])
            {
                try
                {
                    if(deploymentBranchSetting.helm.preExecutionScript != null)
                    {
                        if(deploymentBranchSetting.helm.preExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.helm.preExecutionScript.commandset,deploymentBranchSetting.helm.preExecutionScript.credentials)
                        }
                    }

                    def gitUrl = config.project.gitUrl
                    def credential = config.project.credentialsId
                    def helmUrl = Helpers.getHelmUrl(branchName, config, yamlName, deploymentBranchSetting.helm.chartName)
                    git branch: branchName, url: gitUrl, credentialsId: credential

                    def commitId = sh(script: "git rev-parse ${branchName}", returnStdout:true).trim()
                    def registryId = ""

                    if (deploymentBranchSetting.helm.ecrAccountId == null)
                    {
                        error("The ecrAccountId in the helm block must not be blank.")
                    }

                    registryId = " --registry-id " + deploymentBranchSetting.helm.ecrAccountId

                    basePipeline.awsAuth(awsCredentialsId, awsAccountId)

                    sh "curl -H 'Authorization: token $token' -H 'Accept: application/vnd.github.v3.raw' -O -L ${helmUrl}"
                    sh '''
                        . /etc/profile.d/jenkins.sh
                        #set +x
                        export AWS_PROFILE=saml
                        DIGEST=`aws ecr list-images''' + registryId + ''' --repository-name $DOCKER_REPO --filter tagStatus=TAGGED --output text | grep -w $DOCKER_TAG | awk '{ print $2}'`
                        mkdir -p ~/.kube
                        cat $kubeconfig > ~/.kube/kubeconfig
                        export KUBECONFIG=~/.kube/kubeconfig
                        helm upgrade $RELEASENAME $CHARTNAME -f $YAMLNAME --install --tiller-namespace $NAMESPACE --namespace $NAMESPACE --set image.digest=$DIGEST,annotation.branch='''+branchName+''',annotation.commitId='''+commitId+''',buildId=$BUILD_ID
                        '''
                    if(deploymentBranchSetting.helm.postExecutionScript != null)
                    {
                        if(deploymentBranchSetting.helm.postExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.helm.postExecutionScript.commandset,deploymentBranchSetting.helm.postExecutionScript.credentials)
                        }
                    }
                }
                catch (err)
                {
                    // If the first release failed, then helm upgrade does not patch the subsequent releases.
                    // The first release has to be deleted and a good release has to be released first.
                    sh '''
                          . /etc/profile.d/jenkins.sh
                          mkdir -p ~/.kube
                          cat $kubeconfig > ~/.kube/kubeconfig
                          export KUBECONFIG=~/.kube/kubeconfig
                          RESULT=`helm --tiller-namespace $NAMESPACE list $RELEASENAME | grep FAILED | awk '{print $2}'`
                          if [ "$RESULT" = 1 ]; then
                             helm --tiller-namespace $NAMESPACE delete --purge $RELEASENAME
                          fi
                        '''
                    notify.call(config, "Failed ${err}")
                    currentBuild.result = 'FAILURE'
                    throw err
                }
            }
        }
    }
}

def verifyDeployment(String releaseName, String namespace, String kubeconfigfileCredentialsId, String userEmail, String app)
{
    def notify = new Notify()

    withEnv([
        "NAMESPACE=${namespace}",
        "RELEASENAME=${releaseName}",
        "USR_EMAIL=${userEmail}",
        "APP=${app}"])
    {
        withCredentials([file(credentialsId: "${kubeconfigfileCredentialsId}", variable: 'kubeconfig')])
        {
            sh '''
               . /etc/profile.d/jenkins.sh >>/dev/null 2>&1
               export AWS_PROFILE=saml
               mkdir -p ~/.kube
               cat $kubeconfig > ~/.kube/kubeconfig
               export KUBECONFIG=~/.kube/kubeconfig
               exists=`kubectl -n $NAMESPACE get pods|grep -e "$APP"|awk '{print $1 }'`
               if [ -z "$exists" ]; then
                 echo "Deployment $RELEASENAME failed"
                 exit 1
               fi
            '''
        }
    }
}