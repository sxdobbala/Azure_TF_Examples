#!/usr/bin/env groovy
package com.optum.pipeline
import com.optum.utils.Helpers

import com.optum.config.Config

def call(Config config, Object deploymentBranchSetting)
{
    def envs = deploymentBranchSetting.ose.deployToEnv
    def oseDeploy = new OseDeploy()
    def dockerBuild = new DockerBuild()
    def notify = new Notify()
    def contrast = new Contrast()
    def customExecution = new CustomScriptExecution()

    for (i = 0; i < envs.size(); i++)
    {
        stage ("Deploy to ${envs[i].envName} environment")
        {
            try
            {
                if (deploymentBranchSetting.ose.deployPrompts != null && deploymentBranchSetting.ose.deployPrompts == true)
                {
                   def deployPromptAbortTimeout =  Helpers.getTimeout(deploymentBranchSetting.ose.deployPromptAbortTimeout)
                   timeout(time: deployPromptAbortTimeout, unit: 'MINUTES')
                   {
                       input "Deploy to ${envs[i].envName} Environment?"
                   }
                }
				
                if(deploymentBranchSetting.ose.preExecutionScript != null)
                {
                    if(deploymentBranchSetting.ose.preExecutionScript.commandset != null)
                    {
                        customExecution.customExecutionLogic(deploymentBranchSetting.ose.preExecutionScript.commandset,deploymentBranchSetting.ose.preExecutionScript.credentials)
                    }
                }

                def skipDeploy = envs[i].skipDeploy
                def oseServer = deploymentBranchSetting.ose.server
                def blueGreen = "blue"

                if (envs[i].envName == 'test' || envs[i].envName == 'tst')
                {
                    String imageTag = (envs[i].testTag ? envs[i].testTag : 'test')
                    dockerBuild.tagImage('dev', imageTag, skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                    if(skipDeploy == null || skipDeploy == false)
                    {
                        oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                        // test deploy
                    }
                }
                else if (envs[i].envName == 'stage' || envs[i].envName == 'stg')
                {
                    choice = new ChoiceParameterDefinition('OpenShift type', ['ocp', 'ose'] as String[], 'Chooses which OpenShift type is used')
                    openShiftType = input message: 'Select datacenter for deployment', parameters: [choice]

                    choice = new ChoiceParameterDefinition('Datacenter selection', ['elr', 'ctc'] as String[], 'Chooses which datacenter for deployment')
                    dataCenter = input message: 'Select datacenter for deployment', parameters: [choice]

                    choice = new ChoiceParameterDefinition('Network zone selection', ['core', 'dmz'] as String[], 'Chooses which network zone for deployment')
                    networkZone = input message: 'Select network zone for deployment', parameters: [choice]
                    
                    if (openShiftType == 'ocp')
                    {
                        networkZone += '-stg'
                    }

                    oseServer = "https://${openShiftType}-${dataCenter}-${networkZone}.optum.com"

                    if (envs[i].appGreen != null && deploymentBranchSetting.docker.repoGreen != null)
                    {
                        choice = new ChoiceParameterDefinition('Blue or Green Deployment ', ['blue', 'green'] as String[], 'Chooses to deploy to blue or green environment')
                        blueGreen = input message: 'Deploy to Blue or Green environment?', parameters: [choice]
                    }

                    if (blueGreen == 'green')
                    {
                        if (envs[i].stageTag == null && envs[i].testTag == null)
                        {
                            dockerBuild.tagImage('test', 'stage', skipDeploy, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                        }

                        else
                        {
                            dockerBuild.tagImage(envs[i].testTag, envs[i].stageTag, skipDeploy, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                        }
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repoGreen, envs[i].appGreen, oseServer, envs[i])
                            //green stage deploy
                        }
                    }
                    else
                    {
                        if (envs[i].stageTag == null && envs[i].testTag == null)
                        {
                            dockerBuild.tagImage('test', 'stage', skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                        }

                        else
                        {
                            dockerBuild.tagImage(envs[i].testTag, envs[i].stageTag, skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                        }
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                            //blue stage deploy
                        }
                    }

                    if (envs[i].multiDataCenterDeployment != null)
                    {
                        if (envs[i].multiDataCenterDeployment == true)
                        {
                            choice = new ChoiceParameterDefinition('OpenShift type', ['ocp', 'ose'] as String[], 'Chooses which OpenShift type is used')
                            openShiftType = input message: 'Select datacenter for deployment', parameters: [choice]
        
                            choice = new ChoiceParameterDefinition('Datacenter selection', ['elr', 'ctc'] as String[], 'Chooses which datacenter for deployment')
                            dataCenter = input message: 'Select datacenter for deployment', parameters: [choice]
        
                            choice = new ChoiceParameterDefinition('Network zone selection', ['core', 'dmz'] as String[], 'Chooses which network zone for deployment')
                            networkZone = input message: 'Select network zone for deployment', parameters: [choice]
                            
                            if (openShiftType == 'ocp')
                            {
                                networkZone += '-stg'
                            }
        
                            oseServer = "https://${openShiftType}-${dataCenter}-${networkZone}.optum.com"
        
                            if (blueGreen == 'green')
                            {
                                if (envs[i].stageTag == null && envs[i].testTag == null)
                                {
                                    dockerBuild.tagImage('test', 'stage', skipDeploy, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                                }

                                else
                                {
                                    dockerBuild.tagImage(envs[i].testTag, skipDeploy, envs[i].stageTag, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                                }
                                if(skipDeploy == null || skipDeploy == false)
                                {
                                    oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repoGreen, envs[i].appGreen, oseServer, envs[i])
                                    //green stage deploy
                                }
                            }

                            else
                            {
                                if (envs[i].stageTag == null && envs[i].testTag == null)
                                {
                                    dockerBuild.tagImage('test', 'stage', skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                                }

                                else
                                {
                                    dockerBuild.tagImage(envs[i].testTag, envs[i].stageTag, skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                                }
                                if(skipDeploy == null || skipDeploy == false)
                                {
                                    oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                                    //blue stage deploy
                                }
                            }
                        }
                    }
                }
                else if (envs[i].envName != 'dev' && envs[i].pullTag != null && envs[i].pushTag != null)
                {
                    choice = new ChoiceParameterDefinition('OpenShift type', ['ocp', 'ose'] as String[], 'Chooses which OpenShift type is used')
                    openShiftType = input message: 'Select datacenter for deployment', parameters: [choice]

                    choice = new ChoiceParameterDefinition('Datacenter selection', ['elr', 'ctc'] as String[], 'Chooses which datacenter for deployment')
                    dataCenter = input message: 'Select datacenter for deployment', parameters: [choice]

                    choice = new ChoiceParameterDefinition('Network zone selection', ['core', 'dmz'] as String[], 'Chooses which network zone for deployment')
                    networkZone = input message: 'Select network zone for deployment', parameters: [choice]
                    
                    if (openShiftType == 'ocp')
                    {
                        networkZone += '-stg'
                    }

                    oseServer = "https://${openShiftType}-${dataCenter}-${networkZone}.optum.com"

                    if (envs[i].appGreen != null && deploymentBranchSetting.docker.repoGreen != null)
                    {
                        choice = new ChoiceParameterDefinition('Blue or Green Deployment ', ['blue', 'green'] as String[], 'Chooses to deploy to blue or green environment')
                        blueGreen = input message: 'Deploy to Blue or Green environment?', parameters: [choice]
                    }

                    if (blueGreen == 'green')
                    {
                        dockerBuild.tagImage(envs[i].pullTag, envs[i].pushTag, skipDeploy, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repoGreen, envs[i].appGreen, oseServer, envs[i])
                            //green deploy
                        }
                    }
                    else
                    {
                        dockerBuild.tagImage(envs[i].pullTag, envs[i].pushTag, skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                        if(skipDeploy == null || skipDeploy == false)
                        {
                            oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                            //blue deploy
                        }
                    }

                    if (envs[i].multiDataCenterDeployment != null)
                    {
                        if (envs[i].multiDataCenterDeployment == true)
                        {
                            choice = new ChoiceParameterDefinition('OpenShift type', ['ocp', 'ose'] as String[], 'Chooses which OpenShift type is used')
                            openShiftType = input message: 'Select datacenter for deployment', parameters: [choice]
        
                            choice = new ChoiceParameterDefinition('Datacenter selection', ['elr', 'ctc'] as String[], 'Chooses which datacenter for deployment')
                            dataCenter = input message: 'Select datacenter for deployment', parameters: [choice]
        
                            choice = new ChoiceParameterDefinition('Network zone selection', ['core', 'dmz'] as String[], 'Chooses which network zone for deployment')
                            networkZone = input message: 'Select network zone for deployment', parameters: [choice]
                            
                            if (openShiftType == 'ocp')
                            {
                                networkZone += '-stg'
                            }
        
                            oseServer = "https://${openShiftType}-${dataCenter}-${networkZone}.optum.com"

                            if (blueGreen == 'green')
                            {
                                dockerBuild.tagImage(envs[i].pullTag, envs[i].pushTag, skipDeploy, deploymentBranchSetting.docker.repoGreen, deploymentBranchSetting, config)
                                if(skipDeploy == null || skipDeploy == false)
                                {
                                    oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repoGreen, envs[i].appGreen, oseServer, envs[i])
                                    //green deploy
                                }
                            }

                            else
                            {
                                dockerBuild.tagImage(envs[i].pullTag, envs[i].pushTag, skipDeploy, deploymentBranchSetting.docker.repo, deploymentBranchSetting, config)
                                if(skipDeploy == null || skipDeploy == false)
                                {
                                    oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                                    //blue deploy
                                }
                            }
                        }
                    }
                    //demo or prod, etc.
                }
                else
                {
                    oseDeploy(config, deploymentBranchSetting, deploymentBranchSetting.docker.repo, envs[i].app, oseServer, envs[i])
                    if(deploymentBranchSetting.contrast != null)
                    {
                        contrast.verifyContrastReport(config, deploymentBranchSetting.contrast)
                    }
                    else
                    {
                        println("Deprecated: Please setup Contrast scan with your application. This will be required in future versions.")
                    }
                    //dev deploy
                }
                if(skipDeploy == true)
                {
                    notify(config, "Build is successful - Deployment to ${envs[i].envName} Environment has been skipped")
                }
                else
                {
                    notify(config, "Success - Deployed to ${envs[i].envName} Environment")
                }
				
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
