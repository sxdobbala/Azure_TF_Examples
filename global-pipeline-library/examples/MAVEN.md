# Various Examples that could be useful for you.

Contents:
1. Links to Examples
2. Code Snippets


Links to Examples

- Simple Maven Build, Sonar Scan and Artifactory Deploy.<br>
  SCM = GitHub<br>
  Note:  Pay Attention to the behavior of the Sonar scan for a Pull Request.
     - A Sonar Scan for a Pull Request does not have its scan results saved at sonar.optum.com
     - Sonar will write comments back to the Pull Request letting you know if new Blocker or Critical Sonar Issues were introduced in the Pull Request.  If new issues have been introduced in the changes in the pull request, then Sonar will cause your Pull Request to show an error requiring you to fix the issues before being allowed to merge in your Pull Request.
  
  Links
    - [GitHub Repo](https://github.optum.com/jenkins-pipelines/example-mavenBuild)
    - [Jenkins Central Project](https://jenkins.optum.com/central/job/jpac/job/example-mavenBuild)
    

## Code Snippets

- Simplest Maven Build. Accepting all default values for the build.

        stage ('Build') {
          steps {
            glMavenBuild [:] 
          }
        }


- Maven Build if you inherit from the UHG parent POM that is expecting the ci.env property passed in.

        stage ('Build') {
          steps {
            glMavenBuild additionalProps:['ci.env':'']
          }
        }

- Maven Build if you want to compile using the JDK 1.7 rather than the default JDK 1.8

        stage ('Build') {
          steps {
            glMavenBuild javaVersion:"1.7.0"
          }
        }

        
- Sonar Scan using Maven, The gitUserCredentialsId is helpful since the SCM is GitHub.  When a Pull Request is built for GitHub, Sonar will attempt to write comments (Sonar Blocker and Critical Issues) back to the Pull Request as a comment on the Pull Request.

        stage('Sonar') {
          steps {
            glSonarMavenScan productName:"JPaC",
                             projectName:"example-mavenBuild",
                             gitUserCredentialsId:"${env.GIT_PR_CREDENTIALS_ID}"
          }
        }
        
- Artifactory Deploy, When you are inheriting from the UHG parent POM
     
        <parent>
           <groupId>com.uhg</groupId>
           <artifactId>parent</artifactId>
           <version>2.0.4</version>
        </parent>

  Here is what your Artifactory Step might look.  The Artifactory User must have write permissions to this location in Artifactory
        
        stage('Artifactory') {
          steps {
            glMavenArtifactoryDeploy artifactoryUserCredentialsId: "${env.ARTIFACTORY_DEPLOY_ID}"
          }
        }

- Artifactory Deploy When you are NOT inheriting from the UHG parent POM and you do NOT have the DistributionManagement section defined in your POM

        stage('Artifactory') {
          steps {
            glMavenArtifactoryDeploy artifactoryUserCredentialsId: "${env.ARTIFACTORY_DEPLOY_ID}",
              additionalProps:['altDeploymentRepository':'UHG-Snapshots::default::http://repo1.uhc.com/artifactory/UHG-Snapshots']
          }
        }

 

