package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DeploymentBranchSetting implements Serializable 
{
    String name
    String jenkinsSlave
    Boolean mavenBuild
    Boolean gradleBuild
    String additionalMavenArgs
    String additionalGradleArgs
    Boolean deployToArtifactory
    String automationTestPath
    String testConfig
    Ose ose
    Sonar sonar
    Docker docker
    AWS aws
    Helm helm
    UI ui
    BuildVerificationTests buildVerificationTests
    Lambda lambda
    Contrast contrast
}
