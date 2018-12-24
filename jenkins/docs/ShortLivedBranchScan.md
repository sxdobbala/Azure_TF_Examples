# Short Lived Branch Scan config example:
```
jenkinsNode: 'docker-aws-slave'
project:
    gitUrl: 'https://github.optum.com/ct-instrumentation/sample_app_k8s.git'
    credentialsId: 'fb064600-b737-4b1c-8643-63357494dd55'
deploymentBranchSetting:
    -   name: 'US'
        mavenBuild: true
        deployToArtifactory: false
        additionalMavenArgs: "-Dmaven.test.failure.ignore=true"
        sonar:
           envUrl: 'http://sonar.optum.com'
           credentialsId: 'ec050bf9-7f7b-4e5c-a1f4-952100e43baa'
           projectName: 'ct-sample-app'
           projectVersion: '1.0-SNAPSHOT'
           sources: 'pom.xml, src'
           shortLivedBranchScan: true
           ignoreFailure: true
```

The picture below shows how shared library will execute this config:

![shotlivedbranch](/images/ShortLivedBranchScan.jpeg)