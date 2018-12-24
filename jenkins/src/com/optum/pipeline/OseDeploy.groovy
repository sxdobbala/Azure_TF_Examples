#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, Object deploymentBranchSetting, String dockerRepo, String oseApp, String oseServer, Object deployToEnv)
{
    def notify = new Notify()
    def customExecution = new CustomScriptExecution()
    
    withEnv([
        "DOCKER=/tools/docker/docker-1.6.0/docker",
        "DOCKER_HOST=jenkins.optum.com:30303",
        "DOCKER_HUB=docker.optum.com",
        "DOCK_REPO=${dockerRepo}",
        "ENV_NAME=${deployToEnv.envName}",
        "OSE_APP=${oseApp}",
        "OSE_PROJECT=${deployToEnv.project}",
        "OSE_SERVER=${oseServer}"])
    {
        withCredentials([[
                $class: 'UsernamePasswordMultiBinding',
                credentialsId: "${deploymentBranchSetting.ose.credentialsId}",
                passwordVariable: 'OSE_USR_NAME',
                usernameVariable: 'OSE_PW']])
        {
            withCredentials([[
                    $class: 'UsernamePasswordMultiBinding',
                    credentialsId: "${deploymentBranchSetting.docker.credentialsId}",
                    passwordVariable: 'DOC_PW',
                    usernameVariable: 'USR_NAME']])
            {
                pullTag = Helpers.getTag(ENV_NAME, deploymentBranchSetting.docker.tagIdentifier)
                try
                {
                    if(deploymentBranchSetting.ose.preExecutionScript != null)
                    {
                        if(deploymentBranchSetting.ose.preExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.ose.preExecutionScript.commandset,deploymentBranchSetting.ose.preExecutionScript.credentials)
                        }
                    }

                    sh '''
                       . /etc/profile.d/jenkins.sh
                       for n in {1..3}
                        do
                         oc login --server=${OSE_SERVER} -u ${OSE_PW} -p ${OSE_USR_NAME} --insecure-skip-tls-verify=true && break
                         sleep 15
                        done
                        oc project $OSE_PROJECT                                
                        BUILD_CONFIG=`oc get dc ${OSE_APP} | tail -1 | awk \'{print $1}\'`
                        if [ "${BUILD_CONFIG}" == "${OSE_APP}" ]; then                    
                          oc delete rc $(oc get rc | grep ${OSE_APP} | awk '$2 == 0 {print $1}') || true
                          oc deploy $OSE_APP --latest -n $OSE_PROJECT
                          sleep 10
                        else
                         oc new-app ${DOCKER_HUB}/$USR_NAME/${DOCK_REPO}:'''+ pullTag+''' --name=${OSE_APP}
                        fi;
                  '''

                    if(deploymentBranchSetting.ose.postExecutionScript != null)
                    {
                        if(deploymentBranchSetting.ose.postExecutionScript.commandset != null)
                        {
                            customExecution.customExecutionLogic(deploymentBranchSetting.ose.postExecutionScript.commandset,deploymentBranchSetting.ose.postExecutionScript.credentials)
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