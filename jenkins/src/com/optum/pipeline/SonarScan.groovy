#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, Object deploymentBranchSetting, String branch)
{
    def notify = new Notify()
    def dockerBuild = new DockerBuild()
    def basePipeline = new BasePipeline()
    def customExecution = new CustomScriptExecution()

    try
    {
        withCredentials([[
                $class: 'UsernamePasswordMultiBinding',
                credentialsId: "${deploymentBranchSetting.sonar.credentialsId}",
                passwordVariable: 'svn_password',
                usernameVariable: 'svn_userName']])
        {
            withCredentials([[
                    $class: 'UsernamePasswordMultiBinding',
                    credentialsId: "${config.project.credentialsId}",
                    passwordVariable: 'MAVEN_PASS',
                    usernameVariable: 'MAVEN_USER']])
            {
                if(deploymentBranchSetting.sonar.preExecutionScript != null)
                {
                    if(deploymentBranchSetting.sonar.preExecutionScript.commandset != null)
                    {
                        customExecution.customExecutionLogic(deploymentBranchSetting.sonar.preExecutionScript.commandset,deploymentBranchSetting.sonar.preExecutionScript.credentials)
                    }
                }
				
                checkout scm
                def additionalParams
                def sonarVersion ='3.4.0.905'

                if(deploymentBranchSetting.sonar.version != null)
                {
                    sonarVersion = deploymentBranchSetting.sonar.version
                }

                if (deploymentBranchSetting.sonar.additionalParams == null)
                {
                    additionalParams = ''
                }
                else
                {
                    additionalParams = deploymentBranchSetting.sonar.additionalParams
                }

                def pomDirectory = (config.project.pomPathExtension ? "./${config.project.pomPathExtension}/" : "./")
                def pomFile = "${pomDirectory}pom.xml"
                def pom = readMavenPom file: "${pomFile}"

                def projectNameCommand = ''
                if (deploymentBranchSetting.sonar.projectName != null)
                {
                    projectNameCommand = "-Dsonar.projectName=${deploymentBranchSetting.sonar.projectName}"
                }

                def mavenCommand
                if (deploymentBranchSetting.deployToArtifactory != null && deploymentBranchSetting.deployToArtifactory == true)
                {
                    mavenCommand = "deploy"
                }
                else
                {
                    mavenCommand = "install"
                }

                env.M3_HOME="/tools/maven/apache-maven-${env.MAVEN_VERSION}"
                env.PATH = "${env.M3_HOME}/bin:${env.PATH}"

                if(deploymentBranchSetting.name.startsWith('release'))
                {
                    // updating the name of the version from SNAPSHOT to RELEASE
                    def releaseVersion = pom.version.replaceAll("SNAPSHOT", "RELEASE")
                    sh "mvn -f ${pomFile} -Dmvn versions:set -DnewVersion=${releaseVersion}"
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

                if (deploymentBranchSetting.sonar.shortLivedBranchScan == true)
                {
                    // run short lived branch/ user story branch scan for the given user story branch and will create a sonar branch with this user story branch report.
                    stage('Sonar ShortLived branch Scan (with Maven Build)')
                    {
                        sh "java -version"
                        sh "mvn -f ${pomFile} --batch-mode -U clean org.jacoco:jacoco-maven-plugin:prepare-agent ${mavenCommand} ${additionalMavenArgs} org.jacoco:jacoco-maven-plugin:report -e sonar:sonar -Dsonar.host.url=${deploymentBranchSetting.sonar.envUrl} ${projectNameCommand} -Dsonar.projectVersion=${deploymentBranchSetting.sonar.projectVersion} -Dsonar.java.source=1.8 -Dsonar.login=${svn_userName} -Dsonar.password=${svn_password} -Dsonar.scm.password.secured=${svn_password} -Dsonar.scm.url=scm:git:${config.project.gitUrl} -Dsonar.analysis.mode=publish -Dsonar.branch.name=${branch} -Dsonar.scm.user.secured=${svn_userName} -Dsonar.issuesReport.console.enable=true -Dsonar.forceAnalysis=true -Dsonar.sourceEncoding=UTF-8 -Dsonar.dynamicAnalysis=reuseReports -Dsonar.scm.disabled=true -Dsonar.issuesReport.html.enable=true -Dsonar.links.scm=${config.project.gitUrl} -Dsonar.links.ci=${deploymentBranchSetting.sonar.ciLink} -Dsonar.exclusions='${deploymentBranchSetting.sonar.exclusions}' -Dgit.url='${config.project.gitUrl}' ${additionalParams}"
                    }
                }
                else  if (deploymentBranchSetting.sonar.sonarPreview == true)
                {
                    // run preview without any sonar report been sent.
                    stage('Sonar Preview Scan (with Maven Build)')
                    {
                        sh "java -version"
                        sh "mvn -f ${pomFile} --batch-mode -U clean org.jacoco:jacoco-maven-plugin:prepare-agent ${mavenCommand} ${additionalMavenArgs} org.jacoco:jacoco-maven-plugin:report -e sonar:sonar -Dsonar.host.url=${deploymentBranchSetting.sonar.envUrl} -Dsonar.login=${svn_userName} -Dsonar.password=${svn_password} -Dsonar.scm.user.secured=${svn_userName} -Dsonar.scm.password.secured=${svn_password} -Dsonar.links.ci=${deploymentBranchSetting.sonar.ciLink} -Dsonar.exclusions='${deploymentBranchSetting.sonar.exclusions}' -Dsonar.issuesReport.console.enable=true -Dsonar.analysis.mode=preview -Dsonar.github.repository=${config.project.gitUrl} -Dsonar.github.oauth=${env.githubOauth} -Dsonar.github.endpoint=https://github.optum.com/api/v3 ${additionalParams}"
                    }
                }
                else
                {
                    // run the full scan for either master branch or the branch you specified.
                    stage('SonarQube Analysis (with Maven Build)')
                    {
                        sh "java -version"
                        sh "mvn -f ${pomFile} --batch-mode -U clean org.jacoco:jacoco-maven-plugin:prepare-agent ${mavenCommand} ${additionalMavenArgs} org.jacoco:jacoco-maven-plugin:report -e sonar:sonar -Dsonar.host.url=${deploymentBranchSetting.sonar.envUrl} ${projectNameCommand} -Dsonar.projectVersion=${deploymentBranchSetting.sonar.projectVersion} -Dsonar.java.source=1.8 -Dsonar.login=${svn_userName} -Dsonar.password=${svn_password} -Dsonar.scm.password.secured=${svn_password} -Dsonar.scm.url=scm:git:${config.project.gitUrl} -Dsonar.analysis.mode=publish -Dsonar.scm.user.secured=${svn_userName} -Dsonar.issuesReport.console.enable=true -Dsonar.forceAnalysis=true -Dsonar.sourceEncoding=UTF-8 -Dsonar.dynamicAnalysis=reuseReports -Dsonar.scm.disabled=true -Dsonar.issuesReport.html.enable=true -Dsonar.links.scm=${config.project.gitUrl} -Dsonar.links.ci=${deploymentBranchSetting.sonar.ciLink} -Dsonar.exclusions='${deploymentBranchSetting.sonar.exclusions}' -Dgit.url='${config.project.gitUrl}' ${additionalParams}"
                    }
                }

                if (deploymentBranchSetting.sonar.projectKey != null && deploymentBranchSetting.sonar.metric != null && deploymentBranchSetting.sonar.checkIncreaseOrDecrease != null)
                {
                    sh "curl -X POST 'http://sonar.optum.com/api/measures/component?componentKey=${deploymentBranchSetting.sonar.projectKey}&metricKeys=${deploymentBranchSetting.sonar.metric}' -o sonarMetrics.json"
                    def sonarMetrics = readJSON file: "sonarMetrics.json"

                    if (deploymentBranchSetting.sonar.checkIncreaseOrDecrease == "decrease")
                    {
                        if (sonarMetrics.component.measures[0]["periods"][2].value < 0)
                        {
                            currentBuild.result = 'FAILURE'
                        }
                    }
                    else if (deploymentBranchSetting.sonar.checkIncreaseOrDecrease == "increase")
                    {
                        if (sonarMetrics.component.measures[0]["periods"][2].value > 0)
                        {
                            currentBuild.result = 'FAILURE'
                        }
                    }
                }

                notify.call(config, 'Success - Sonar analyisis completed')

                step([$class: 'ArtifactArchiver',
                    artifacts: '**/*.war, **/*.jar',
                    excludes: null])

                if(deploymentBranchSetting.sonar.postExecutionScript != null)
                {
                    if(deploymentBranchSetting.sonar.postExecutionScript.commandset != null)
                    {
                        customExecution.customExecutionLogic(deploymentBranchSetting.sonar.postExecutionScript.commandset,deploymentBranchSetting.sonar.postExecutionScript.credentials)
                    }
                }
            }
        }
    }
    catch (err)
    {
        if (deploymentBranchSetting.sonar.ignoreFailure != null && deploymentBranchSetting.sonar.ignoreFailure == true)
        {
            notify.call(config, "Failed ${err} - build will continue if any steps remain")
            currentBuild.result = 'FAILURE'
        }
        else
        {
            notify.call(config, "Failed ${err}")
            currentBuild.result = 'FAILURE'
            throw err
        }
    }
}
