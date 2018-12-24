#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, Object deploymentBranchSetting) {
	def notify = new Notify()
	def dockerBuild = new DockerBuild()

	try {
		checkout scm
		def additionalGradleArgs
		env.PATH = "/tools/gradle/gradle-${env.GRADLE_VERSION}/bin:${env.PATH}"
		// adding additional arguments for running the gradle build command
		if (deploymentBranchSetting.additionalGradleArgs == null) {
			additionalGradleArgs = ''
		}
		else {
			additionalGradleArgs = deploymentBranchSetting.additionalGradleArgs
		}

		/*  Add chmod +x gradlew and change the below command to ./gradlew -s build to 
		 build with gradle wrapper in any node not containing gradle
		 */

		sh"""
		. /etc/profile.d/jenkins.sh
		gradle -s build ${additionalGradleArgs}
		"""

		/*For storing artifacts in jenkins in case of a failure (to check in case 
		 of a failure) 
		 Standard practice */
		step([$class: 'ArtifactArchiver',
			artifacts: '**/*.war, **/*.jar',
			excludes: null])

		if (deploymentBranchSetting.docker != null)
		{
			def cloudEnv = deploymentBranchSetting.docker.cloudEnv
			if(cloudEnv != null && cloudEnv.equalsIgnoreCase("aws"))
			{
				stage (' AWS Docker image build and push')
				{
					dockerBuild.awsDockerBuild(config, deploymentBranchSetting)
				}
			}
			else
			{
				stage('OSE Docker image build and push')
				{
					dockerBuild.oseDockerBuild(config, deploymentBranchSetting)
				}
			}
		}

	}catch (err) {
		notify.call(config, "Failed ${err}")
		currentBuild.result = 'FAILURE'
		throw err
	}
}
