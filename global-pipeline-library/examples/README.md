# Examples and Usage


- [MAVEN](./MAVEN.md)
    -  glMavenBuild
    -  glSonarMavenScan
    -  glMavenArtifactoryDeploy   


## POST Section
The POST section to handle the behavior after the stages complete.  You can use the various sections to change the POST behavior depending upon the result of the build: always, success, failure, unstable and changed


      post {
        always {
          echo 'This will always run'
        }
        success {
          echo 'This will run only if successful'
        }
        failure {
          echo 'This will run only if failed'
        }
        unstable {
          echo 'This will run only if the run was marked as unstable'
        }
        changed {
          echo 'This will run only if the state of the Pipeline has changed'
          echo 'For example, if the Pipeline was previously failing but is now successful'
        }
      }
 

## Email Notifications
You might want to put the email sections within the appropriate POST section.<br>
Here is a simple email notification that will run during a build failure.

      post {
        failure {
          echo 'This will run only if failed'
          
          emailext body: "Build URL: ${BUILD_URL}",
            subject: "$currentBuild.currentResult-$JOB_NAME",
            to: 'emailAddressGoesHere@optum.com'
            
        }
      }

Other more complex email notification options:<br>
### Attatching a file to your email

     emailext attachmentsPattern: '**/target/emailable-report.html',  
                  body: '''Cucumber Test Result Link :- $BUILD_URL/cucumber-html-reports/overview-features.html
                  
            ''', 
        mimeType: 'text/html',
        subject: "$currentBuild.currentResult-$JOB_NAME", 
        to: 'emailAddressGoesHere@optum.com'
   
   
### Adding the contents of an HTML file to the body of your email

    emailext  body: '''Cucumber Test Result Link :- $BUILD_URL/cucumber-html-reports/overview-features.html
           <br>
           <br>
           Test Result :- <br>
           ${FILE,path="target/emailable-report.html"}
           ''', 
       mimeType: 'text/html',
       subject: "$currentBuild.currentResult-$JOB_NAME", 
       to: 'emailAddressGoesHere@optum.com'           
  
            
