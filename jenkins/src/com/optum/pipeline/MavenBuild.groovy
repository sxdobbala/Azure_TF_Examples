#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def dockerBuild = new DockerBuild()
    def basePipeline = new BasePipeline()

    withCredentials([[
            $class: 'UsernamePasswordMultiBinding',
            credentialsId: "${config.project.credentialsId}",
            passwordVariable: 'MAVEN_PASS',
            usernameVariable: 'MAVEN_USER']])
    {
        try
        {
            checkout scm
            def pomDirectory = (config.project.pomPathExtension ? "./${config.project.pomPathExtension}/" : "./")
            def pomFile = "${pomDirectory}pom.xml"
            def pom = readMavenPom file: "${pomFile}"
            env.PATH = "/tools/maven/apache-maven-${env.MAVEN_VERSION}/bin:${env.PATH}"
            
            if(deploymentBranchSetting.name.startsWith('release'))
            {
                def releaseVersion = pom.version.replaceAll("SNAPSHOT", "RELEASE")
                sh "mvn -f ${pomFile} -Dmvn versions:set -DnewVersion=${releaseVersion}"
                // updating the name of the version from SNAPSHOT to RELEASE
            }

            def additionalMavenArgs
            if (deploymentBranchSetting.additionalMavenArgs == null)
            {
                additionalMavenArgs = ''
            }
            else
            {
                additionalMavenArgs = deploymentBranchSetting.additionalMavenArgs
            }

            if (deploymentBranchSetting.deployToArtifactory != null)
            {
                if (deploymentBranchSetting.deployToArtifactory == true)
                {
                    sh "mvn -f ${pomFile} --batch-mode -Dmvn clean org.jacoco:jacoco-maven-plugin:prepare-agent deploy ${additionalMavenArgs}"
                }
                else
                {
                    sh "mvn -f ${pomFile} --batch-mode -Denv=ci org.jacoco:jacoco-maven-plugin:prepare-agent install ${additionalMavenArgs}"
                }
            }

            else
            {
                sh "mvn -f ${pomFile} --batch-mode -Denv=ci org.jacoco:jacoco-maven-plugin:prepare-agent install ${additionalMavenArgs}"
            }

            step([$class: 'ArtifactArchiver',
                artifacts: '**/*.war, **/*.jar',
                excludes: null])

        }
        catch (err)
        {
            notify.call(config, "Failed ${err}")
            currentBuild.result = 'FAILURE'
            throw err
        }
    }
}
