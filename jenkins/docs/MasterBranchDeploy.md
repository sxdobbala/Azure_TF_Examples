# Master Branch Deploy to test and stage env config example:
```
jenkinsNode: 'docker-aws-slave'
project:
    gitUrl: 'https://github.optum.com/ct-instrumentation/sample_app_k8s.git'
    credentialsId: 'fb064600-b737-4b1c-8643-63357494dd55'
deploymentBranchSetting:
    -   name: 'master'
        mavenBuild: true
        deployToArtifactory: true
        additionalMavenArgs: "-Dmaven.test.failure.ignore=true"
        docker:
           repo: 'admin/sampleapp'
           hub: '757541135089.dkr.ecr.us-east-1.amazonaws.com'
           credentialsId: 'AWS_OID_DEV_SERVICE_ACCOUNT'
           cloudEnv: aws
           tagIdentifier: 0.0.1
        helm:
           app: 'sample-app'
           releaseName: 'sample-app'
           namespace: 'admin'
           chartName: 'sample-app-chart'
           gitHubToken: '18c77e38-704f-443e-a8b1-fbe1d725c73e'
           dockerTag: latest
           ecrAccountId: 757541135089
           deployToHelmEnv:
               - envName: 'dev'
                 kubeconfigfileCredentialsId: '3a6807ee-21b1-4c39-b6e6-b3a345962cae'
                 awsCredentialsId: 'AWS_OID_DEV_SERVICE_ACCOUNT'
                 yamlName: 'values-dev.yaml'
               - envName: 'test'
                 kubeconfigfileCredentialsId: '3a6807ee-21b1-4c39-b6e6-b3a345962cae'
                 awsCredentialsId: 'AWS_OID_TEST_SERVICE_ACCOUNT'
                 yamlName: 'values-test.yaml'
              -  envName: 'stage'
                 kubeconfigfileCredentialsId: '3a6807ee-21b1-4c39-b6e6-b3a345962cae'
                 awsCredentialsId: 'AWS_OID_STAGE_SERVICE_ACCOUNT'
                 yamlName: 'values-stage.yaml'
```                 

The picture below shows how shared library will execute this config:
 ![masterbranchbuild](/images/MasterBranchDeploy.jpeg)