# The Concept of SkipDeploy:
  A deployment to a specific branch can be skipped by setting the skipDeploy flag. For example:
  1. If skipDeploy is set to true in for the test environment, then the docker image will be tagged as test and test-skipped. The presence of test-skipped flag means that the deployment for this environment will be skipped.
1. If skipDeploy is not set or set to false, then the docker image will be tagged as just test. If test-skipped tag for the image is present, then that image will be removed. The image with test tag will then be deployed to the test environment.
  
   
# Config with SkipDeploy flag present example config:
```
jenkinsNode: 'docker-aws-slave'
project:
    gitUrl: 'https://github.optum.com/ct-instrumentation/sample_app_k8s.git'
    credentialsId: 'fb064600-b737-4b1c-8643-63357494dd55'
deploymentBranchSetting:
    -   name: 'release'
        mavenBuild: true
        deployToArtifactory: false
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
                 awsCredentialsId: 'AWS_OID_DEV_SERVICE_ACCOUNT'
                 yamlName: 'values-dev.yaml'
                 skipDeploy: true
```

The picture below shows how shared library will execute this config with skipDeploy set as True:

![skipdeploy](/images/SkipDeploy.jpeg)

The picture below shows how shared library will execute this config with skipDeploy switch from True to False or skipDeploy be removed.:

![skipdeploy](/images/SkipDeployAsFalse.jpeg)