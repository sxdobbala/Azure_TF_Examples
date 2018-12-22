import com.optum.jenkins.pipeline.library.maven.MavenBuild

/**
 * Deploys to artifactory.  It is expected that the DistributionManagment (see https://maven.apache.org/pom.html#Distribution_Management)
 * section will already be defined.  You can always override what is defined in your POM using the "additionalProps" Map.
 * -DdistributionManagement.repository.id= -DdistributionManagement.repository.name=  -DdistributionManagement.repository.url=
 * OR this might also work -DaltDeploymentRepository=repositoryId::repoName::http://WhatEverURL
 * @param mavenGoals String Allows you to override the default behavior so that you can pass in the pom file for the specific module that you want to deploy.
 * @param mavenProfiles String Comma seperated list of Maven Profiles to pass into the command line.  Defaults to null.  Ex: j2ee1.6,sonar,
 * @param javaVersion String The Java version to use. Defaults to Constants.JAVA_VERSION.
 * @param mavenVersion String The Maven version to use. Defaults to Constants.MAVEN_VERSION
 * @param isDebugMode boolean Indicates if Maven should be run in debug (-X). Defaults to false.
 * @param isBatchMode boolean Indicates if Maven should be run in batch mode (-B). Defaults to true.
 * @param deployAtEnd boolean Inicates if the deployment for multi module projects should be delayed until the very end so that they all deploy together
 * @param additionalProps Map An optional map of any additional properties that should be set.
 * @param pomFile Allows you to override the pom.xml on the command line so you can use one from your repo that's different from standard
 */

def call(Map<String, Object> config){
  MavenBuild build = new MavenBuild(this)
  build.deployToArtifactory(config)
}