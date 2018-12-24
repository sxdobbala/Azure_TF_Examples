# config.yml template and description

This page contains the config.yml template and the descriptions for each field. The mandatory ones that are included in larger headings like docker are only required if you want to run docker, ose, sonar, etc. Please see the main [ReadMe](/README.md) in order to understand how this configuration file should be used.

```yml
notificationEmail: ''
flowdock:
    org: ''
    flow: ''
    token: ''
jenkinsNode: ''
project:
    gitUrl: ''
    credentialsId: ''
    pomPathExtension: ''
    deploymentBranchSetting:
        - name: ''
          mavenBuild:
          additionalMavenArgs: ''
          gradleBuild:
          additionalGradleArgs: ''
          deployToArtifactory:
          docker:
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              repo: ''
              hub: ''
              tagIdentifier: ''
              credentialsId: ''
              awsAccountId: ''
              repoGreen: ''
              dockerfilePathExtension: ''
              prompt: ''
              promptAbortTimeout: ''
              cloudEnv: ''
              backupDtr: ''
              backupDtrCredentialsId: ''
          ose:      
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              credentialsId: ''
              server: ''
              deployPrompts:
              deployPromptAbortTimeout: ''
              deployToEnv:
                  - project: ''  
                    app: ''
                    appGreen: ''
                    envName: ''
                    testTag: ''
                    stageTag: ''
                    multiDataCenterDeployment:
                    skipDeploy: ''
          helm:
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              gitHubToken: ''
              app: ''
              releaseName: ''
              namespace: ''
              deployPrompts: ''
              deployPromptAbortTimeout: ''
              chartName: ''
              dockerTag: ''
              ecrAccountId: ''
              deployToHelmEnv:
                  - envName: ''
                    awsCredentialsId: ''
                    awsAccountId: ''
                    kubeconfigfileCredentialsId: ''
                    yamlName: ''
                    skipDeploy: ''
          sonar:
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              envUrl: ''
              version: ''
              credentialsId: ''
              projectKey: ''
              projectName: ''
              projectVersion: ''
              exclusions: ''
              ciLink: ''
              additionalParams: ''
              sonarPreview: 
              shortLivedBranchScan:           
              ignoreFailure:
          ui:
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              stashName: ''
              stashDirectory: ''
              deployPromptAbortTimeout: ''
              deployToEnv:
                  - envName: ''
                    awsCredentialsId: ''
                    awsAccountId: ''
                    s3Bucket: ''
          buildVerificationTests:
              jobName: ''
          lambda:
              preExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              postExecutionScript:
                commandset:
                    -''
                credentials:
                    -credentialsId: ''
                     type: ''
                     usernameVariable: ''
                     passwordVariable: ''
                     kubeConfigVariable: ''
                     tokenVariable: ''
              validatePrerequisites:
              runtime: ''
              functionName: ''
              testInvocationFile: ''
              s3CredentialsId: ''
              s3AccountId: ''
              s3Bucket: ''
              s3Key: ''
              deployToEnv:
                  - envName: ''
                    awsCredentialsId: ''
                    awsAccountId: ''
                    version: ''
                    alias: ''
                    deployType: ''
                    initialWeight:
                    duration:
          contrast:
              contrastProfile: ''
              applicationName: ''
              vulnerabilityCount: ''
              vulnerabilitySeverity: ''
```

# Description for this config.yml template:

- notificationEmail: The person who will be notified by email when the job is completed or if failure occurs.
- flowdock: Messages can be sent to flowdock instead of email. This uses the flowdock [messages api](https://www.flowdock.com/api/messages).
    - org: The organization or the company account. The default is "uhg". 
    - flow: The flow where you want to send the message to.
    - token: An API token is needed to send messages to flowdock. This is available in your account's flowdock [API Tokens page](https://flowdock.com/account/tokens).
- project
    - gitUrl: The source repository for the project you are setting up.
    - credentialsId: The credential for Jenkins to access the project repo.
    - pomPathExtension: The optional parameter use to set the pom path.
- deploymentBranchSetting: This section contains descriptions of the git branches that are being deployed.
     - name: The branch name you want to deploy. It can be a user story branch, develop branch, etc. There can be multiple deploy branches in one config.yml.
     - mavenBuild: Optional true/false(case sensitive) flag indicating whether maven build needs to run for this branch.
     - additionalMavenArgs: Optional field to pass in extra maven build arguments. It can be java argument or application arguments. Example: -Dmaven.test.failure.ignore=true
     - gradleBuild: Optional true/false(case sensitive) flag indicating whether gradle build needs to run for this branch.
     - additionalGradleArgs: Optional field to pass in extra gradle build arguments. It can be java argument or application arguments. Example: --stacktrace -Dorg.gradle.console=verbose
     - deployToArtifactory : Optional true/false(case sensitive) indicating whether the artifacts need to be deployed to artifactory.

## Custom Script Execution 
- preExecutionScript and postExecutionScript can be used to execute custom commands before or after the main block is executed. Any command section in the deploymentBranchSetting can contain this custom shell script.
    - Example: docker, ose, aws, helm, sonar, lambda,UI 
- preExecutionScript: Optional custom command section to be executed before the main docker block execution.
    - commandset: set of custom commands to be executed in the "pre" section
    - credentials: Optional set of credentials to be used for execution of the custom commands provided.
        - credentialsId: Jenkins credentials ID
        - type: token/kubeconfig/usernamePassword
        - usernameVariable: username variable related to the credential to be used in the custom command (Only applicable for type=usernamePassword)
        - passwordVariable: password variable related to the credential to be used in the custom command (Only applicable for type=usernamePassword)
        - tokenVariable: token variable name related to the credential to be used in the custom command (Only applicable for type=token)
        - kubeConfigVariable: kubeconfig variable name related to the credential to be used in the custom command (Only applicable for type=kubeconfig)

- postExecutionScript: Optional custom command section to be executed before the main docker block execution.
    - commandset: set of custom commands to be executed in the "post" section
    - credentials: Optional set of credentials to be used for execution of the custom commands provided.
        - credentialsId: Jenkins credentials ID
        - type: token/kubeconfig/usernamePassword
        - usernameVariable: username variable related to the credential to be used in the custom command (Only applicable for type=usernamePassword)
        - passwordVariable: password variable related to the credential to be used in the custom command (Only applicable for type=usernamePassword)
        - tokenVariable: token variable name related to the credential to be used in the custom command (Only applicable for type=token)
        - kubeConfigVariable: kubeconfig variable name related to the credential to be used in the custom command (Only applicable for type=kubeconfig)

## Use this if you want to run docker build

- docker: Information about the docker repository that contains the application image.
    - hub: The url where you host your docker image. Example: docker.optum.com, *.ecr.useast1.aws.com
    - repo: The name of the project's docker repo.
    - prompt: Optional true/false flag indicating whether to prompt during docker build. Example: "Build and push Docker image?"
    - promptAbortTimeout: Optional parameter indicating time out in minutes if user input is not provided. The job aborts if action is not taken within that time frame. The default time out is 10 mins.
    - tagIdentifier: Optional parameter will be used as docker tag.
    - credentialsId: The credential Jenkins uses to access the project's docker repo.
    - awsAccountId: In order to log in to AWS, we need the credentials and role. The awsAccountId is needed to specify the role.
    - repoGreen: Optional parameter use for the green deployment. This is useful if the application will deploy to both blue and green environments.
    - dockerfilePathExtension: Optional file path for the location of Dockerfile in case it is not in the root directory.
    - cloudEnv: Required parameter indicating which cloud environment docker image will push to example : aws/ose.
    - backupDtr: Optional true/false flag indicating whether to push the image to optum.docker.com. The default value for this is false
    - backupDtrCredentialsId: The credential Jenkins uses to access the project's optum docker repo.
    
## Use this if you want to deploy to OSE

- ose: The OSE environment where the application will be deployed to.
    - credentialsId: The credential Jenkins uses to access the OSE environment.
    - server: The URL of OSE server. Deploying to stage server will be an input parameter
    - deployPrompts: Optional true/false flag indicating whether to prompt during deployment. Example: "Deploy to Dev?"
    - deployPromptAbortTimeout: Optional parameter indicating time out in minutes if user input is not provided. The job aborts if action is not taken within that time frame. The default time out is 10 mins.
    - deployToEnv: An array containing one or more environments to deploy to.
        - project: The name the OSE project.
        - app: The name for the application.
        - appGreen: Optional parameter use for blue/green deployment to choose between blue and green deployments. Example: 'admin-stackgreen'. Only used in stage.
        - envName: The environment the application will be deployed to such as Dev, Stage, Test.
        - testTag: This parameter will be used to tag the docker image as test image. Example: test/test
        - stageTag: This parameter will be used to tag the docker image as stage image. Example: stage/stg
        - multiDataCenterDeployment: Optional true/false(case sensitive) indicating whether the application will be deployed to elr as well as ctc data centers.
        - skipDeploy: Optional true/false flag indicating whether to do deploy to environment for this build. If you only want to run the maven build and push image but not deploy to the environment, you can use this flag.

## Use this if you want to run sonar scan.

- sonar: This section defines the Sonar scan/preview parameters.
    - envUrl: The sonar URL.
    - version: The optional parameter used to override the sonar-maven-plugin version. Right now the default sonar-maven-plugin is 3.0.4
    - credentialsId: The credential used by Jenkins to access Sonar server.
    - projectKey: Optional key of Sonar project.
    - projectName: The name of sonar project.
    - projectVersion: The project version which will show up in Sonar server.
    - exclusions: Optional parameter indicating the folders/files to be excluded from sonar build. Example: pom.xml,src/main/java/com/optum/foo/datatype/*
    - ciLink: Jenkins CI link to run the sonar build.
    - additionalParams: Optional parameters for the sonar build. Example: -Dmaven.test.failure.ignore=true
    - sonarPreview: Optional true/false parameter.  If you want to run full scan of your code, set this flag to false. If you want to run the sonar preview meaning that the sonar qube reports are not generated, then set this flag to true
    - shortLivedBranchScan: Optional true/false parameter. If you want to run the short lived branch scan for your code, set this to true. This will produce your branch specific sonar qube report for a specified number of days.
    - ignoreFailure: Optional true/false flag which will allow the build to progress even if sonar fails.
    - metric: Optional parameter which allows for further testing of a specific metric (must have project key specified as well). Example: coverage
    - checkIncreaseOrDecrease: Optional parameter allows for further testing of a specific metric (must have project key specified as well). Example: decrease

## Use this if you want to deploy to AWS using helm

- helm: This section defines the AWS/helm parameters.
    - gitHubToken: The git hub token  used by Jenkins to download the yaml file.
    - app: The name for the application.
    - namespace: The k8s namespace for the deployment.
    - chartName: The name for the chart you want to use for the deployment.
    - deployPrompts: Optional true/false flag indicating whether to prompt during AWS deploy. Example: "Deploy to AWS environment using helm ?".
    - deployPromptAbortTimeout:Optional parameter indicating time out in minutes if user input is not provided. The job aborts if action is not taken within that time frame. The default time out is 10 mins.
    - dockerTag: The value used to get the docker image sha from AWS ECR.
    - ecrAccountId: The account ID needed to pull repository from ECR. Note that this may be different than the current AWS account since ECR for all environments can be maintained under one account.
    - userInputTimesOut: Optional time out value indicating time out for prompt input.
    - deployToHelmEnv: An array containing one or more environments to deploy to.
        - envName: The environment the application will be deployed to such as test, stage, demo
        - kubeconfigfileCredentialsId: The credential used by Jenkins to get kubeconfig file for this environment.
        - awsCredentialsId: The credential used by Jenkins to access AWS.
        - awsAccountId: In order to log in to AWS, we need the credentials and role. The awsAccountId is needed to specify the role.
        - yamlName: The values yaml file for this env's deployment. Example: values-test.yaml.
        - skipDeploy: Optional true/false flag indicating whether to do deploy to environment for this build. If you only want to run the maven build and push image but not deploy to the environment, you can use this flag. 

## Use this if you want to do UI AWS deploy

- ui: This section defines the UI parameters.
    - stashName: The name for stash the build example: 'tb-stash'
    - stashDirecory: The directory structures for stash
    - deployPromptAbortTimeout: Optional parameter indicating time out in minutes if user input is not provided. The job aborts if action is not taken within that time frame. The default time out is 10 mins.
    - deployToEnv: An array containing one or more environments to deploy to.
        - envName: The environment the application will be deployed to such as dev, test, stage, demo
        - awsCredentialsId: The credential used by Jenkins to access AWS.
        - awsAccountId: In order to log in to AWS, we need the credentials and role. The awsAccountId is needed to specify the role.
        - s3Bucket: The location of s3 bucket which stash will be push to

## Use this if you want to deploy Lambda functions to AWS

These parameters are based off of the AWS [reference doc](https://docs.aws.amazon.com/cli/latest/reference/lambda/update-function-code.html).
Detailed architectural description of the Lambda deployment is available [here](Lambda.md).

- lambda:
    - runtime: The runtime environment for the Lambda function. The comprehensive list of options is in [AWS doc](https://docs.aws.amazon.com/cli/latest/reference/lambda/create-function.html). The only runtime supported by shared library today is java8.
    - functionName: The name of the lambda function.
    - testInvocationFile: A file in the repository that contains the input for the test invocation. The deployment is allowed only if the test invocation is successful. 
    - s3CredentialsId: The Jenkins credential used to access the S3 bucket containing the Lambda function archive.
    - s3AccountId: The AWS account that contains the S3 bucket used to archive the Lambda function.
    - s3Bucket: The S3 bucket that contains the Lambda function archive.
    - s3Key: The S3 file prefix to store the Lambda function.
    - deployToEnv: An array of environments to deploy the Lambda function to.
        - envName: The name of the environment the Lambda function will be deployed to.
            - awsCredentialsId: The Jenkins credential used to log into the AWS environment where the Lambda function is being deployed.
            - awsAccountId: The AWS account where the funciton will be deployed to.
            - version: The version of the lambda function. This should adhere to the [Semantic Versioning Specification](https://semver.org/). The version should include `MAJOR.MINOR.PATCH`. For example, 2.1.0 - MAJOR version when there are incompatible API changes, MINOR version when adding functionality in a backwards-compatible manner, and PATCH version when there are backwards-compatible bug fixes.            
            - alias: Alias is like a pointer to a specific Lambda function version.
            - deployType: The value could be one of "AllAtOnce" and "Canary". AllAtOnce is the normal deployment where we route the Lambda function calls to just one version of the function. Canary is the deployment mechanism were a percent of traffic can be routed to the new version of the function for a specified amount of time. If there are no issues then switch to the new version of the function. For example, we are deploying version 2 of the Lambda function. We can setup canary deployment to route 20% of the function calls to version 2 for 10 minutes. This means that 80% of the calls will still go to the previous version of the function. After 10 minutes, we can check cloud watch. If the version 2 did not report any issues, then we can route 100% of the calls to version 2 of the function.   
            - initialWeight: Applies only to Canary deployment. The percent of traffic to route to the new version of the function initially. For example, 20%.
            - duration: Applies only to Canary deployment. The amount of time in minutes to route traffic to the new version of the function. At the end of this time, we will check the cloud watch metrics to determine if we can route all the traffic to the new function. If there are no errors, we can proceed and do so. 

## Use this for contrast vulnerability check

- contrast: This section defines the Contrast security scan parameters which scans the project and finds the vulnerabilities for the applications.
    - contrastProfile: contrast profile name which is configured in Jenkins Contrast plugin.
    - applicationName: application name to check in contrast server.
    - vulnerabilityCount: total count of the vulnerabilities found for this application.
    - vulnerabilitySeverity: the severity for the vulnerabilities. Example: High, Medium.
